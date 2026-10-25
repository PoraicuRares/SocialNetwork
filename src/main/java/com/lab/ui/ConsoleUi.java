package com.lab.ui;


import com.lab.domain.Friendship;
import com.lab.domain.Tuple;
import com.lab.domain.User;
import com.lab.exceptions.EmptyListException;
import com.lab.exceptions.RepositoryException;
import com.lab.exceptions.ServiceException;
import com.lab.exceptions.ValidationException;
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
        boolean isRunning = true;
        while(isRunning){
            printMenu();
            System.out.print("Choose an option : ");
            String optiune = scanner.nextLine().trim();
            try{
                switch (optiune){
                    case "1": handleAddUser(); break;
                    case "2": handleRemoveUser(); break;
                    case "3": handleShowUsers(); break;
                    case "4": handleAddFriendship(); break;
                    case "5": handleRemoveFriendship(); break;
                    case "6": handleShowFriendships(); break;
                    case "7": handleShowNrOfCommunities(); break;
                    case "8": handleShowMostSocialCommunity();break;
                    case "0":
                        isRunning = false;
                        System.out.println("Exiting..");
                        break;
                    default:
                        System.out.println("Invalid option");
                }
            }catch (ValidationException | ServiceException | RepositoryException | EmptyListException e){
                System.out.println("Error : " + e.getMessage());
            }catch (NumberFormatException e){
                System.out.println("Input error : Please enter a valid numeric ID");
            }
        }
    }

    private void printMenu(){
        System.out.println("Social network menu : ");
        System.out.println("1.Add user");
        System.out.println("2.Remove user");
        System.out.println("3.Show all users");

        System.out.println("4.Add friendship");
        System.out.println("5.Remove friendship");
        System.out.println("6.Show all friendships");

        System.out.println("7.Show number of communities");
        System.out.println("8.Show most sociable community");
        System.out.println("0.Exit");
    }

    private void handleAddUser(){
        System.out.print("Enter first name : ");
        String firstName = scanner.nextLine().trim();

        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine().trim();

        userService.add(new User(firstName,lastName));
        System.out.println("User successfully added ");
    }

    private void handleRemoveUser(){
        handleShowUsers();
        System.out.print("Enter the id of the user to remove : ");
        Long id = Long.parseLong(scanner.nextLine().trim());

        networkService.deleteUser(id);
        System.out.println("User successfully removed");
    }

    private void handleShowUsers(){
        System.out.println("\nUsers list : ");
        for(User u : userService.findAll()){
            System.out.println(u);
        }
    }

    private void handleAddFriendship(){
        handleShowUsers();
        System.out.print("Enter the first user's id : ");
        Long id1 = Long.parseLong(scanner.nextLine().trim());

        System.out.print("Enter the second user's id : ");
        Long id2 = Long.parseLong(scanner.nextLine().trim());

        networkService.addFriendship(id1 , id2);
        System.out.println("Friendship successfully added");
    }

    private void handleRemoveFriendship(){
        handleShowFriendships();
        System.out.print("Enter the first user's ID :");
        Long id1 = Long.parseLong(scanner.nextLine().trim());

        System.out.print("Enter the second user's ID :");
        Long id2 = Long.parseLong(scanner.nextLine().trim());
        friendshipService.remove(new Tuple<>(id1 , id2));
        System.out.println("Friendship successfully removed");
    }

    private void handleShowFriendships(){
        System.out.println("\nFriendship list : ");
        for(Friendship f : friendshipService.findAll()){
            System.out.println(f);
        }
    }

    private void handleShowNrOfCommunities(){
        System.out.println("Number of communities is  " + networkService.getNumberOfCommunities());
    }

    private void handleShowMostSocialCommunity(){
        for(User u : networkService.getMostPopularCommunity()){
            System.out.println(u);
        }
    }
}
