1. FUNCTIONAREA FACTORY-ULUI SI TRECEREA LA BD

Clasa RepositoryFactory (implementata ca Simple Factory) este singura componenta din sistem care cunoaste implementarile concrete ale repository-urilor (InMemory, File, DB). Citirea configuratiilor se face automat din "config.properties".
Service-urile (NetworkService, UserService ,FriendshipService) depind exclusiv de interfata generica Repository, nefiind afectate de schimbarile de la nivelul de persistenta.
Pentru lab 4, trecerea la baza de date se va realiza extrem de usor: se va modifica valoarea "repository.type=DATABASE" in fisierul config.properties. Tot codul din UI si Service va ramane absolut neschimbat, aplicatia rutand automat salvarile catre clasele UserDBRepository si FriendshipDBRepository.


2. TRATAREA FISIERELOR LIPSA SI A LINIILOR CORUPTE

- Fisier lipsa: In metoda loadData() din AbstractFileRepository, daca fisierul cautat nu exista pe disc, sistemul prinde FileNotFoundException. In implementarea curenta, se arunca un RepositoryException, semnaland o problema. (Pentru a porni cu repo-ul gol confom altei abordari, blocul catch ar putea doar sa ignore eroarea).
- Linii corupte: Cand se intalneste o linie invalida (ex: atribute lipsa in split, ID format din litere, nume fara majuscula), functia extractEntity() arunca RepositoryException sau ValidationException.
JUSTIFICARE: Am ales ca incarcarea datelor sa se opreasca cu o exceptie clara (in loc sa ignoram linia si sa continuam) pentru a asigura "Fail-Fast". Daca o linie corupta dintr-un fisier apartine unui utilizator care este implicat in mii de prietenii in celalalt fisier, ignorarea lui ar duce la o baza de date aflata intr-o stare inconstenta. Oprirea executiei obliga administratorul sa repare fisierul inainte ca aplicatia sa corupa si mai multe date la o viitoare scriere (writeToFile).



3. PRINCIPII SOLID RESPECTATE IN PROIECT
- SRP (Single Responsibility Principle): Fiecare clasa are o singura responsabilitate. UserValidator face doar validare; UserFileRepository se ocupa strict de lucrul cu fisierele text pentru utilizatori; UserService aplica regulile de business; ConsoleUI se ocupa doar de meniu si citiri de la tastatura.
- OCP (Open-Closed Principle): Sistemul este deschis pentru extensii dar inchis pentru modificari in anumite zone. De exemplu, interfata Validator permite adaugarea de noi reguli de validare creand clase noi, fara sa modificam codul din InMemoryRepository. (O exceptie de la OCP exista la Simple Factory - detalii la punctul 5).
- LSP (Liskov Substitution Principle): Obiectele de tip UserFileRepository pot inlocui oriunde obiectele de tip Repository (ex: in constructorul UserService) fara sa afecteze corectitudinea programului.
- DIP (Dependency Inversion Principle): Modulele de nivel inalt (UserService) nu depind de modulele de nivel scazut (UserFileRepository). Ambele depind de abstractizari (interfata Repository).

4. SABLOANE DE PROIECTARE UTILIZATE
- Factory (Simple Factory): Centralizeaza logica de creare a repository-urilor in clasa RepositoryFactory.
- Template Method: Prezent in AbstractFileRepository. Clasa de baza defineste "scheletul" algoritmului (loadData, writeToFile), in timp ce subclasele implementeaza doar pasii specifici: extractEntity() si createEntityAsString().
- Strategy: Folosit prin interfata Validator<T>. Logica de validare reprezinta o "strategie" care ii este injectata repository-ului, decupland logica de stocare de logica de validare a datelor de business.
- Decorator: Utilizat nativ de Java la operatiile de IO: BufferedReader "decoreaza" un FileReader simplu pentru a-i adauga functionalitatea de citire eficienta pe o memorie tampon (linii intregi vs. caracter cu caracter).

5. OCP: SIMPLE FACTORY VS ABSTRACT FACTORY

In proiect s-a utilizat Simple Factory, cu o instructiune "switch" pe RepositoryType. Din perspectiva principiului OCP (Open-Closed Principle), Simple Factory il incalca: daca pe viitor vrem sa adaugam un nou tip de persistenta (ex: XML, MongoDB), suntem obligati sa modificam clasa RepositoryFactory, adaugand un nou "case" in switch.
Alternativa Abstract Factory ar respecta perfect OCP. Am fi creat o interfata IRepositoryFactory, avand clase distincte: FileRepositoryFactory, DbRepositoryFactory. Adaugarea persistentei XML s-ar fi facut creand o clasa noua (XmlRepositoryFactory), fara a atinge in vreun fel codul claselor factory deja existente. Dezavantajul Abstract Factory este cresterea majora a complexitatii si a numarului de clase pentru o aplicatie relativ mica.