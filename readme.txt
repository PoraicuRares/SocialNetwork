Interfete functionale folosite si unde
- Validator<T> (interfata proprie, adnotata cu @FunctionalInterface): are metoda default andThen(Validator<T> other), care compune doua validari. In Main, UserValidator este compus cu o regula noua scrisa ca lambda (numele au cel mult 50 de caractere), fara clasa noua si fara modificarea lui UserValidator.
- Predicate<E>: parametrul metodei default findAll(Predicate<E> filter) din Repository; criteriile de cautare din UserCriteria (hasIdIn, firstNameStartsWith, hasLastName) si FriendshipCriteria (involves) intorc Predicate si se pot combina cu and, or, negate, Predicate.not. Nu au fost adaugate metode de tipul findByLastName in repository.
- Function<T, R>: in FileRepository (Function<String, E> pentru linie -> entitate si Function<E, String> pentru entitate -> linie), in RepositoryFactory (Map<RepositoryType, Function<Validator<E>, Repository<ID, E>>>) si in Optional.map.
- Supplier<T>: argumentul lui orElseThrow(() -> new RepositoryException(...)) in UserService, FriendshipService si RepositoryFactory.
- Consumer<T>: argumentul lui forEach (System.out::println, friendshipService::remove).
- Runnable: valorile din Map-ul de comenzi al meniului din ConsoleUi (this::handleAddUser etc.), rulate cu Runnable::run.
- Comparator<T>: Comparator.comparing(User::getId), Comparator.comparing(...).thenComparing(...) pentru prietenii, Comparator.comparingInt(...) pentru cea mai sociabila comunitate, Comparator.naturalOrder() pentru id-ul maxim. Nu exista compare scris de mana sau scaderi de tipul (a, b) -> a - b.
- IntPredicate / IntConsumer / ToIntFunction: implicit, prin lambda-urile date lui filter, forEach si mapToInt pe IntStream.

Referinte la metode (toate cele patru tipuri)
- metoda statica: LineConverters::parseUser, LineConverters::formatUser, Objects::nonNull
- metoda de instanta a unui obiect anume: System.out::println, friendshipService::remove, this::handleAddUser
- metoda de instanta a unui obiect oarecare: User::getId, Friendship::getId, Runnable::run
- constructor: InMemoryRepository::new (in RepositoryFactory)


Alegerea pentru Optional in Repository
Am ales ca findOne sa intoarca Optional<E> in loc de null. Semnatura spune explicit ca rezultatul poate lipsi, iar codul care o apeleaza este obligat sa trateze cazul (orElseThrow, isEmpty, isPresent), eliminand riscul unui NullPointerException aparut departe de cauza. Am modificat consecvent interfata Repository, InMemoryRepository (Optional.ofNullable(entities.get(id))), scheletele DB, service-urile si Javadoc-ul. Valoarea nu se extrage niciodata cu get(): se foloseste orElseThrow cu exceptia proprie a aplicatiei (acelasi mesaj ca la laboratorul 3, prins de UI), orElse sau ifPresentOrElse. Optional este folosit si pentru rezultatele lui max si findAny, si in RepositoryFactory (Optional.ofNullable(...).map(...).orElseThrow(...)). Optional este folosit doar ca tip de retur, nu pentru campuri sau parametri.


Template Method vs Function in repository-urile pe fisier
Am trecut de la Template Method (AbstractFileRepository cu subclasele UserFileRepository si FriendshipFileRepository) la compozitie: o singura clasa FileRepository<ID, E> primeste prin constructor doua functii, Function<String, E> si Function<E, String>. Conversiile propriu-zise sunt metode statice in LineConverters si se transmit ca referinte la metode.
Motive:
- au disparut doua subclase care difereau doar prin doua metode; o entitate noua necesita doar doua functii de conversie, nu o clasa noua;
- in varianta veche, constructorul clasei de baza apela loadData(), care apela metoda abstracta extractEntity() inainte ca subclasa sa fie initializata (orice camp al subclasei ar fi fost inca null). Acum functiile sunt setate in constructor inainte de loadData(), deci problema nu mai exista.


Functionarea factory-ului si trecerea la baza de date
Clasa RepositoryFactory este singura componenta din aplicatie care cunoaste implementarile concrete ale repository-urilor (InMemory, File, sau DB). Ea citeste setarile direct din "config.properties".
Factory-ul nu mai foloseste switch: fiecarui RepositoryType ii corespunde, intr-un Map, o functie Function<Validator<E>, Repository<ID, E>> care creeaza repository-ul. Functiile se executa doar pentru tipul ales, deci se citesc doar proprietatile necesare acelui tip (de exemplu, pentru FILE nu se folosesc db.url / db.user / db.password). Logica de alegere este scrisa o singura data, in metoda generica create, folosita atat pentru utilizatori, cat si pentru prietenii.
Datorita acestei decuplari, service-urile si interfata cu utilizatorul depind doar de interfata generica Repository. Pentru laboratorul 5, trecerea la baza de date se va face doar prin modificarea in fisierul de configurare "repository.type=DATABASE". Pana atunci, repository-urile DB raman schelete care arunca UnsupportedOperationException; exceptia este prinsa in Main si afisata ca mesaj de eroare la pornire.


Tratarea fisierului lipsa si a liniilor corupte
- Fisier lipsa: in loadData() se verifica Files.notExists(path) inainte de citire. Daca fisierul nu exista, aplicatia porneste cu repository-ul gol, iar fisierul se creeaza automat la prima adaugare (Files.write).
- Citirea: Files.lines(path) intr-un try-with-resources (stream-ul tine fisierul deschis), liniile sunt colectate intr-o lista, apoi parcurse cu IntStream.range(0, lines.size()) ca sa avem numarul liniei. Liniile goale sunt sarite.
- Liniile corupte: orice eroare aparuta la o linie (numar de campuri gresit, id care nu e numar, data invalida, entitate care nu trece validarea) este prinsa si impachetata intr-o RepositoryException care contine numarul liniei, iar aplicatia se opreste din incarcat. La laboratorul 3 doar o parte din aceste erori aveau numarul liniei. Motivul pentru care nu ignor linia este acelasi: repository-ul nu are voie sa comunice cu consola, iar ignorarea unei linii ar putea introduce inconsistente in retea (de exemplu, o prietenie care face referire la un utilizator ignorat). Oprirea aplicatiei forteaza utilizatorul sa repare fisierul.
- Exceptiile checked: IOException de la Files.lines si Files.write este impachetata in RepositoryException. In interiorul lambda-urilor nu se arunca exceptii checked.


Validarea in repository vs validarea in service
Am pastrat apelul validatorului direct in Repository. Justificarea este ca FileRepository isi populeaza memoria chiar in constructor (prin loadData), moment in care Service-ul nici macar nu a fost instantiat. Daca validarea ar fi fost mutata exclusiv in Service, fisierul ar fi putut injecta entitati corupte direct in memoria (Map) a repository-ului.


Bucle pastrate si de ce
- ConsoleUi.run: bucla while(isRunning) a meniului. Nu parcurge o colectie, ci repeta afisarea meniului pana cand utilizatorul alege iesirea, deci nu intra sub restrictia "fara bucle pentru parcurgerea colectiilor". Optiunea 0 ramane in afara Map-ului de comenzi pentru ca modifica variabila locala isRunning, iar o lambda nu poate modifica o variabila locala (trebuie sa fie effectively final).
- Parcurgerile de graf (visit pentru componente conexe si longestPathFrom pentru cel mai lung drum) sunt recursive, iar vecinii sunt procesati cu stream-uri. Backtracking-ul pentru cel mai lung drum nu mai modifica o multime comuna (visited.put / visited.remove): fiecare apel primeste o copie noua a drumului (metoda extend), deci functia data lui mapToInt nu are efecte secundare.


Modificari de comportament fata de laboratorul 3
- Numarul de comunitati cand nu exista prietenii: la laboratorul 3 se afisa eroarea "There are no friendships added"; acum fiecare utilizator fara prieteni formeaza propria comunitate, deci se afiseaza numarul de utilizatori. Fara utilizatori, rezultatul este 0.
- Cea mai sociabila comunitate (componenta conexa cu cel mai lung drum, nu cea cu cele mai multe noduri) contine aceiasi utilizatori, dar sunt afisati sortati dupa id, nu in ordinea parcurgerii DFS.
- Listele de utilizatori si de prietenii sunt afisate sortate (utilizatorii dupa id, prieteniile dupa primul id si apoi dupa al doilea), nu in ordinea din HashMap.
- La citirea prieteniilor, id-urile sunt normalizate (cel mai mic in stanga), ca o prietenie scrisa "7;6;..." in fisier sa poata fi stearsa din UI.


Principii SOLID
- SRP (Single Responsibility Principle): Fiecare clasa are un singur scop clar. UserValidator valideaza, FileRepository se ocupa doar de fisiere, LineConverters doar de conversia linie <-> entitate, UserService dicteaza regulile de business, iar ConsoleUi ruteaza comenzile utilizatorului.
- OCP (Open-Closed Principle): Modulele sunt deschise la extensie si inchise la modificare. Exemple: o regula noua de validare se adauga cu andThen si o lambda, fara a modifica UserValidator; un criteriu nou de cautare este doar un Predicate nou, fara metode noi in repository; o entitate noua salvata in fisier necesita doar doua functii de conversie, fara o clasa noua de repository.
- LSP (Liskov Substitution Principle): Subclasele pot inlocui clasa de baza fara a altera functionarea corecta a programului. In UserService a fost injectata interfata de baza Repository, iar la runtime aceasta este inlocuita de FileRepository sau InMemoryRepository fara ca logica service-ului sa fie afectata.
- DIP (Dependency Inversion Principle): Atat Service-ul cat si Repository-ul depind de abstractizari (interfetele lor), nu de implementarile concrete.


Sabloane de proiectare
- Factory: Folosit in RepositoryFactory pentru a incapsula crearea instantelor de repository in functie de configurare; alegerea se face printr-un Map de functii, nu printr-un switch.
- Strategy: Validarea este implementata printr-o interfata functionala (Validator). Strategia exacta de validare este injectata din exterior la instantierea repository-ului, iar strategiile se pot compune cu andThen. Tot ca strategii se transmit si conversiile linie <-> entitate catre FileRepository, si criteriile de cautare (Predicate) catre findAll.
- Singleton: Utilizat pentru clasa RepositoryFactory pentru a asigura ca se creeaza o singura instanta a acestui obiect.
- Tabel de comenzi: meniul din ConsoleUi asociaza fiecarei optiuni o actiune (Runnable) intr-un Map, in loc de un switch.
- Template Method a fost folosit la laboratorul 3 in AbstractFileRepository si a fost inlocuit cu compozitie (vezi sectiunea "Template Method vs Function").


Discutie teoretica OCP: Simple Factory vs Abstract Factory
Implementarea curenta este tot un Simple Factory, dar mai apropiat de OCP decat la laboratorul 3: adaugarea unei persistente pe XML inseamna o valoare noua in RepositoryType si o intrare noua in Map-urile de functii, fara a modifica un switch din mai multe metode. Clasa RepositoryFactory tot trebuie modificata, deci OCP nu este respectat complet.
Daca am fi implementat Abstract Factory, am fi creat o interfata IRepositoryFactory si clase distincte (FileFactory, DbFactory). Astfel, pentru XML am fi creat pur si simplu o noua clasa XmlFactory, respectand perfect OCP. Am ales totusi varianta Simple Factory pentru ca este suficient de flexibila pentru un proiect mic, varianta abstracta crescand inutil complexitatea.