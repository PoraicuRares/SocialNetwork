# Social Network

## How the factory works and switching to the database

The **RepositoryFactory** class (implemented using **Simple Factory**) is the only component in the application that knows the concrete repository implementations (InMemory, File or DB). It reads the settings directly from `config.properties`.

Thanks to this decoupling, the services and the user interface depend only on the generic **Repository** interface. Switching between storages is done only by changing `repository.type` (`MEMORY`, `FILE` or `DATABASE`) in the configuration file. The code in the Service and UI layers stays exactly the same, and the factory routes requests to **InMemoryRepository**, **FileRepository** or **UserDBRepository** / **FriendshipDBRepository**.

## Configuration

`config.properties` contains the real database password, so it is listed in `.gitignore` and is not pushed to GitHub. The repository contains `config.example.properties` instead: copy it to `config.properties` in the same folder (`src/main/resources`) and set your own `db.password`. The database connection is configured with `db.url`, `db.user` and `db.password`, and the PostgreSQL driver (`org.postgresql:postgresql:42.7.7`) is added in `build.gradle.kts`.

The database schema is in `sql/schema.sql` and must be run once on the `socialnetwork` database.

## Handling a missing file and corrupted lines

- **Missing file:** In the `loadData()` method, I avoided throwing an exception by checking whether the file exists (`Files.notExists(path)`) before reading it. If the file doesn't exist, the method stops and the application starts correctly with an empty repository. The file is then created automatically on the first add from the UI (when `writeToFile` is called).
- **Corrupted lines:** If an invalid line is found, an exception is thrown and the application stops loading. The reason is architectural: I couldn't just skip the line and print a warning, because the repository is not allowed to communicate with the console (I don't use `System.out.println` in the repository). Sending the message to the UI would have complicated the code for no real benefit. Also, skipping a line could introduce serious inconsistencies in the network (for example, a friendship that refers to a user that was skipped while reading). Stopping the application forces the user to fix the file manually before even more data gets corrupted.

## Validation in the repository vs. in the service

Although in standard architectures validation belongs to the Service, in this project I chose to keep the validator call directly in the Repository. The reason is that **FileRepository** fills its memory in the constructor (through `loadData`), at a moment when the Service hasn't even been instantiated yet. If validation had been moved only to the Service, the file could have injected corrupted entities directly into the repository's in-memory cache (the Map). The database repositories follow the same rule and validate the entity before `save` and `update`.

## Deleting a user's friendships: ON DELETE CASCADE and explicit deletion

The foreign keys use ON DELETE CASCADE, and NetworkService.deleteUser also deletes the friendships explicitly before deleting the user. The explicit deletion is required because the application must behave the same with MEMORY and FILE storage, which have no cascade. The CASCADE constraint is a safety net that keeps the database consistent when a user is deleted outside the application (for example from pgAdmin).

## Id generation

The storage generates user ids, not the service: PostgreSQL through GENERATED ALWAYS AS IDENTITY (read back with getGeneratedKeys and set on the saved user), and the in-memory and file repositories through an id generator function (greatest id + 1, `IdGenerators::nextLong`) received in the constructor. UserService no longer assigns ids. Friendships do not need a generator, because their id is the pair of user ids.

## Repository contract

save returns an empty Optional on success and the entity if one with the same id exists; update returns the entity if no row was changed (executeUpdate() == 0); delete returns the deleted entity. This follows the semantics of Map.putIfAbsent, replace and remove. A null argument throws IllegalArgumentException. The repository only reports what happened; the service decides whether it is an error (for example "These users are already friends").

## Exceptions in the database repositories

Every SQLException is wrapped in a RepositoryException, which the UI catches and prints without a stack trace. A duplicate primary key (SQL state 23505) is not an error of the storage: `save` returns the entity, as the contract requires.

## The while loop in AbstractDBRepository.findAll

The services and repositories use streams instead of loops over collections. The only loop is in `AbstractDBRepository.findAll`: a ResultSet is not a collection and has no stream(), it can only be read row by row with next(), so the rows are collected with a while loop.

## SOLID principles

- **SRP (Single Responsibility Principle):** Each class has one clear purpose. UserValidator validates, FileRepository only handles files, the DB repositories only handle SQL, UserService defines the business rules, and ConsoleUi routes the user's commands.
- **OCP (Open-Closed Principle):** Modules are open for extension and closed for modification. For example, new validation rules can be added by writing new classes or composing validators with `andThen`, without changing the base structure of the repository.
- **LSP (Liskov Substitution Principle):** Subclasses can replace the base class without breaking the correct behavior of the program. UserService receives the base Repository interface, and at runtime it is replaced with InMemoryRepository, FileRepository or UserDBRepository without affecting the service logic. All implementations respect the same contract (including update and the Optional results).
- **DIP (Dependency Inversion Principle):** Both the Service and the Repository depend on abstractions (their interfaces), not on concrete implementations.

## Design patterns

- **Factory:** Used in RepositoryFactory to encapsulate creating repository instances depending on the context.
- **Template Method:** Implemented in AbstractDBRepository. The base class defines the steps of every operation (opening the connection, preparing the statement, executing it, reading the result and wrapping SQLException), and the subclasses only provide the SQL text and the conversion between rows and entities through abstract methods such as `selectAllSql`, `extractEntity` and `setIdParameters`. `readGeneratedIds` is a hook with an empty default body, overridden only by UserDBRepository.
- **Strategy:** Validation is implemented through an interface (Validator). The exact validation strategy is injected from outside when the repository is created. The line conversions of FileRepository and the id generator of InMemoryRepository are also strategies, received as functions in the constructor.
- **Singleton:** Used for the RepositoryFactory class to make sure only one instance of this object is created, since more than one isn't needed.

## OCP discussion: Simple Factory vs. Abstract Factory

The current implementation uses **Simple Factory**. This slightly breaks the OCP principle because, if in the future we want to add XML persistence, we would have to modify the code in RepositoryFactory to add another entry for XML in the maps of creator functions.

If I had implemented **Abstract Factory**, I would have created an `IRepositoryFactory` interface and separate classes (FileFactory, DbFactory). That way, for XML I would simply create a new XmlFactory class, fully respecting OCP. I still chose Simple Factory because it is flexible enough for a small project, and the abstract version would add unnecessary complexity.