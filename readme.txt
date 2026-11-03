# Social Network

## How the factory works and switching to the database

The **RepositoryFactory** class (implemented using **Simple Factory**) is the only component in the application that knows the concrete repository implementations (InMemory, File or DB). It reads the settings directly from `config.properties`.

Thanks to this decoupling, the services and the user interface depend only on the generic **Repository** interface. For Lab 4, switching to the database will be very simple: only `repository.type=DATABASE` needs to be changed in the configuration file. The code in the Service and UI layers will stay exactly the same, and the factory will automatically route requests to **UserDBRepository** and **FriendshipDBRepository**.

## Handling a missing file and corrupted lines

- **Missing file:** In the `loadData()` method, I avoided throwing a `FileNotFoundException` by checking whether the file exists (`file.exists()`) before opening the input stream. If the file doesn't exist, the method stops and the application starts correctly with an empty repository. The file is then created automatically on the first add from the UI (when `writeToFile` is called).
- **Corrupted lines:** If an invalid line is found, an exception is thrown and the application stops loading. The reason is architectural: I couldn't just skip the line and print a warning, because the repository is not allowed to communicate with the console (I don't use `System.out.println` in the repository). Sending the message to the UI would have complicated the code for no real benefit. Also, skipping a line could introduce serious inconsistencies in the network (for example, a friendship that refers to a user that was skipped while reading). Stopping the application forces the user to fix the file manually before even more data gets corrupted.

## Validation in the repository vs. in the service

Although in standard architectures validation belongs to the Service, in this project I chose to keep the validator call directly in the Repository. The reason is that **AbstractFileRepository** fills its memory in the constructor (through `loadData`), at a moment when the Service hasn't even been instantiated yet. If validation had been moved only to the Service, the file could have injected corrupted entities directly into the repository's in-memory cache (the Map).

## SOLID principles

- **SRP (Single Responsibility Principle):** Each class has one clear purpose. UserValidator validates, AbstractFileRepository only handles files, UserService defines the business rules, and ConsoleUI routes the user's commands.
- **OCP (Open-Closed Principle):** Modules are open for extension and closed for modification. For example, new validation rules can be added by writing new classes, without changing the base structure of the repository.
- **LSP (Liskov Substitution Principle):** Subclasses can replace the base class without breaking the correct behavior of the program. UserService receives the base Repository interface, and at runtime it is replaced with UserFileRepository without affecting the service logic.
- **DIP (Dependency Inversion Principle):** Both the Service and the Repository depend on abstractions (their interfaces), not on concrete implementations.

## Design patterns

- **Factory:** Used in RepositoryFactory to encapsulate creating repository instances depending on the context.
- **Template Method:** Implemented in AbstractFileRepository. The base class defines the steps of the algorithm (how the file is read and written), and the subclasses only implement line parsing through the abstract methods `extractEntity` and `createEntityAsString`.
- **Strategy:** Validation is implemented through an interface (Validator). The exact validation strategy is injected from outside when the repository is created.
- **Decorator:** Used natively by Java for I/O. BufferedReader was used to decorate the FileReader instance, adding new functionality to it (an in-memory buffer for reading whole lines efficiently instead of single characters).
- **Singleton:** Used for the RepositoryFactory class to make sure only one instance of this object is created, since more than one isn't needed.

## OCP discussion: Simple Factory vs. Abstract Factory

The current implementation uses **Simple Factory**. This slightly breaks the OCP principle because, if in the future we want to add XML persistence, we would have to modify the code in RepositoryFactory to add another `case XML` in the switch.

If I had implemented **Abstract Factory**, I would have created an `IRepositoryFactory` interface and separate classes (FileFactory, DbFactory). That way, for XML I would simply create a new XmlFactory class, fully respecting OCP. I still chose Simple Factory because it is flexible enough for a small project, and the abstract version would add unnecessary complexity.