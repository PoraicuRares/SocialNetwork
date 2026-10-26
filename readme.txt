
Functionarea factory-ului si trecerea la baza de date
Clasa RepositoryFactory (implementata folosind Simple Factory) este singura componenta din aplicatie care cunoaste implementarile concrete ale repository-urilor (InMemory, File, sau DB). Ea citeste setarile direct din "config.properties".
Datorita acestei decuplari, service-urile si interfata cu utilizatorul depind doar de interfata generica Repository. Pentru laboratorul 4, trecerea la baza de date se va face extrem de simplu: doar se va modifica in fisierul de configurare "repository.type=DATABASE". Codul din Service si UI va ramane absolut neschimbat, factory-ul rutand automat cererile catre UserDBRepository si FriendshipDBRepository.

Tratarea fisierului lipsa si a liniilor corupte
- Fisier lipsa: In metoda loadData() am evitat aruncarea unei exceptii de tip FileNotFoundException prin verificarea existentei fisierului (file.exists()) inainte de a deschide fluxul de citire. Daca fisierul nu exista, executia metodei se opreste, aplicatia pornind corect cu repository-ul gol, urmand ca fisierul sa se creeze automat la prima adaugare din UI (la apelul writeToFile).
- Liniile corupte: Daca se intalneste o linie invalida,  se arunca o exceptie si aplicatia se opreste din incarcat. Motivul este unul arhitectural: nu puteam doar sa ignor linia si sa afisez un mesaj de avertizare, deoarece repository-ul nu are voie sa comunice cu consola (nu folosesc System.out.println in repo). Trimiterea mesajului catre UI ar fi complicat codul inutil. In plus, ignorarea unei linii ar putea introduce inconsistente grave in retea (de exemplu, o prietenie care face referire la un utilizator ignorat la citire). Oprirea aplicatiei forteaza utilizatorul sa repare fisierul manual inainte sa corupa si mai multe date.

Validarea in repository vs validarea in service:
Desi in arhitecturile standard validarea apartine de Service, in acest proiect am ales sa pastrez apelul validatorului direct in Repository. Justificarea este ca AbstractFileRepository isi populeaza memoria chiar in constructor (prin loadData), moment in care Service-ul nici macar nu a fost instantiat. Daca validarea ar fi fost mutata exclusiv in Service, fisierul ar fi putut injecta entitati corupte direct in memoria cache (Map) a repository-ului.

Principii SOLID
- SRP (Single Responsibility Principle): Fiecare clasa are un singur scop clar. UserValidator valideaza, AbstractFileRepository se ocupa doar de fisiere, UserService dicteaza regulile de business, iar ConsoleUI ruteaza comenzile utilizatorului.
- OCP (Open-Closed Principle): Modulele sunt deschise la extensie si inchise la modificare. Exemplu: se pot adauga noi criterii de validare dezvoltand clase noi, fara a modifica structura de baza a repository-ului.
- LSP (Liskov Substitution Principle): Subclasele pot inlocui clasa de baza fara a altera functionarea corecta a programului. In UserService a fost injectata interfata de baza Repository, iar la runtime aceasta este inlocuita de UserFileRepository fara ca logica service-ului sa fie afectata.
- DIP (Dependency Inversion Principle): Atat Service-ul cat si Repository-ul depind de abstractizari (interfetele lor), nu de implementarile de baza.



Sabloane de proiectare
- Factory: Folosit in RepositoryFactory pentru a incapsula crearea instantelor de repository in functie de context.
- Template Method: Implementat in AbstractFileRepository. Clasa de baza dicteaza pasii algoritmului (cum se citeste si scrie in fisier), iar subclasele implementeaza doar parsarea liniilor prin metodele abstracte extractEntity si createEntityAsString.
- Strategy: Validarea este implementata printr-o interfata (Validator). Strategia exacta de validare este injectata din exterior la instantierea repository-ului.
- Decorator: Utilizat nativ de limbajul Java in gestionarea I/O. BufferedReader a fost folosit pentru a decora instanta de FileReader, adaugand-i o functionalitate noua (un buffer in memorie pentru a citi eficient linii intregi in loc de caractere singulare).
- Singleton: Utilizat pentru clasa RepositoryFactory pentru a asigura ca se creaza doar o instanta a acestui obiect, neavand nevoie de mai multe


Discutie teoretica OCP: Simple Factory vs Abstract Factory
Implementarea curenta foloseste Simple Factory. Aceasta incalca putin principiul OCP deoarece, daca in viitor vom dori sa adaugam o persistenta pe XML, va trebui sa modificam codul din RepositoryFactory pentru a adauga inca un "case XML" in switch.
Daca am fi implementat Abstract Factory, am fi creat o interfata IRepositoryFactory si clase distincte (FileFactory, DbFactory). Astfel, pentru XML am fi creat pur si simplu o noua clasa XmlFactory, respectand perfect OCP. Am ales totusi varianta Simple Factory pentru ca este suficient de flexibila pentru un proiect mic, varianta abstracta crescand inutil complexitatea.