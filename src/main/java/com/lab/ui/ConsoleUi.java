package com.lab.ui;


import com.lab.domain.Friendship;
import com.lab.domain.User;
import com.lab.service.FriendshipService;
import com.lab.service.NetworkService;
import com.lab.service.UserService;

import java.util.Scanner;

public class ConsoleUi {
    private final UserService userService;
    private final FriendshipService friendshipService;
    private final NetworkService networkService;
    private final Scanner scanner;

    public ConsoleUi(UserService userService, FriendshipService friendshipService, NetworkService networkService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.networkService = networkService;
        this.scanner = new Scanner(System.in);
    }

    public void run(){
        while(true){
            printMenu();
            System.out.print("Alege o optiune : ");
            String optiune = scanner.nextLine().trim();
            try{
                switch (optiune){
                    case "1":
                        handleAddUser();
                        break;
                    case "2":
                        handleRemoveUser();
                        break;
                    case "3":
                        handleShowUsers();
                        break;
                    case "4":
                        handleAddFriendship();
                        break;
                    case "5":
                        handleRemoveFriendship();
                        break;
                    case "6":
                        handleShowFriendships();
                        break;
                    case "7":
                        handleShowNrOfCommunities();
                        break;
                    case "8":
                        handleShowMostSocialCommunity();
                        break;
                    default:
                        System.out.println("Optiune invalida!");
                }
            }catch (Exception e){
                System.out.println("Eroare : " + e.getMessage());
            }
        }
    }

    private void printMenu(){
        System.out.println("Meniu de optiuni : ");
        System.out.println("1.Adauga utilizator");
        System.out.println("2.Sterge utilizator");
        System.out.println("3.Afiseaza toti utilizatorii");

        System.out.println("4.Adauga prietenie");
        System.out.println("5.Sterge prietenie");
        System.out.println("6.Afiseaza toate prieteniile");

        System.out.println("7.Afiseaza numarul de comunitati");
        System.out.println("8.Afiseaza cea mai sociabila comunitate");

    }

    private void handleAddUser(){
        System.out.print("Introdu prenumele : ");
        String firstName = scanner.nextLine().trim();

        System.out.print("Introdu numele de familie : ");
        String lastName = scanner.nextLine().trim();

        System.out.print("Introdu varsta : ");
        int age = Integer.parseInt(scanner.nextLine().trim());

        userService.add(firstName,lastName,age);
        System.out.println("Utilizator adaugat cu succes!");
    }

    private void handleRemoveUser(){
        handleShowUsers();
        System.out.print("Introdu id-ul utilizatorului de sters : ");
        Integer id = Integer.parseInt(scanner.nextLine().trim());

        userService.remove(id);
        System.out.println("Utilizatorul a fost sters cu succes!");
    }

    private void handleShowUsers(){
        for(User u : userService.findAll()){
            System.out.println(u);
        }
    }

    private void handleAddFriendship(){
        handleShowUsers();
        System.out.print("Introdu id-ul primului utilizator : ");
        Integer id1 = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Introdu id-ul utilizatorului al doilea : ");
        Integer id2 = Integer.parseInt(scanner.nextLine().trim());

        friendshipService.add(id1 , id2);
        System.out.println("Prietenia a fost adaugata cu succes");
    }

    private void handleRemoveFriendship(){
        handleShowFriendships();
        System.out.print("Introdu id-ul prieteniei de sters :");
        Integer id = Integer.parseInt(scanner.nextLine().trim());

        friendshipService.remove(id);
        System.out.println("Prietenia s-a sters cu succes");
    }

    private void handleShowFriendships(){
        for(Friendship f : friendshipService.findAll()){
            System.out.println(f);
        }
    }

    private void handleShowNrOfCommunities(){
        System.out.println("Numarul de comunitati este " + networkService.getNumberOfCommunities());
    }

    private void handleShowMostSocialCommunity(){
        for(User u : networkService.getMostPopularCommunity()){
            System.out.println(u);
        }
    }
}
