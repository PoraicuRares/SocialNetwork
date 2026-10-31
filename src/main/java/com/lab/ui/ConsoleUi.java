package com.lab.ui;


import com.lab.domain.Tuple;
import com.lab.domain.User;
import com.lab.exceptions.EmptyListException;
import com.lab.exceptions.RepositoryException;
import com.lab.exceptions.ServiceException;
import com.lab.exceptions.ValidationException;
import com.lab.service.FriendshipService;
import com.lab.service.NetworkService;
import com.lab.service.UserService;

import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleUi {
    private final UserService userService;
    private final FriendshipService friendshipService;
    private final NetworkService networkService;
    private final Scanner scanner;
    private final Map<String ,Runnable> commands;

    public ConsoleUi(UserService userService, FriendshipService friendshipService, NetworkService networkService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.networkService = networkService;
        this.scanner = new Scanner(System.in);
        this.commands = Map.of(
                "1", this::handleAddUser,
                "2", this::handleRemoveUser,
                "3", this::handleShowUsers,
                "4", this::handleAddFriendship,
                "5", this::handleRemoveFriendship,
                "6", this::handleShowFriendships,
                "7", this::handleShowNrOfCommunities,
                "8", this::handleShowMostSocialCommunity
        );
    }

    public void run(){
        boolean isRunning = true;
        while(isRunning){
            printMenu();
            System.out.print("Choose an option : ");
            String option = scanner.nextLine().trim();
            if(option.equals("0")){
                isRunning = false;
                System.out.println("Exiting..");
            }
            else{
                executeCommand(option);
            }
        }
    }

    private void executeCommand(String option){
        try{
            Optional.ofNullable(commands.get(option))
                    .ifPresentOrElse(Runnable::run ,  () -> System.out.println("Invalid option"));
        }catch(ValidationException | ServiceException | RepositoryException | EmptyListException e){
            System.out.println("Error :" + e.getMessage());
        }catch (NumberFormatException e){
            System.out.println("Input error : please enter a valid numeric id");
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
        userService.findAll().forEach(System.out::println);
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
        friendshipService.findAll().forEach(System.out::println);
    }

    private void handleShowNrOfCommunities(){
        System.out.println("Number of communities is  " + networkService.getNumberOfCommunities());
    }

    private void handleShowMostSocialCommunity(){
        networkService.getMostPopularCommunity().forEach(System.out::println);
    }
}
