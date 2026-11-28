package com.lab;

import com.lab.controller.UserController;
import com.lab.domain.Friendship;
import com.lab.domain.Tuple;
import com.lab.domain.User;
import com.lab.exceptions.RepositoryException;
import com.lab.exceptions.ValidationException;
import com.lab.repository.PagingRepository;
import com.lab.repository.Repository;
import com.lab.repository.factory.RepositoryFactory;
import com.lab.service.FriendshipRequestService;
import com.lab.service.FriendshipService;
import com.lab.service.NetworkService;
import com.lab.service.UserService;
import com.lab.ui.ConsoleUi;
import com.lab.validator.FriendshipRequestValidator;
import com.lab.validator.FriendshipValidator;
import com.lab.validator.UserValidator;
import com.lab.validator.Validator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The JavaFX application: builds the main window of the social network
 */
public class StartApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        RepositoryFactory repositoryFactory = RepositoryFactory.getInstance("config.properties");
        Validator<User> userValidator = new UserValidator()
                .andThen(user -> {
                    if (user.getFirstName().length() > 50 || user.getLastName().length() > 50)
                        throw new ValidationException("Names must have at most 50 characters");
                });
        PagingRepository<Long, User> userRepository = repositoryFactory.createUserRepository(userValidator);
        Repository<Tuple<Long, Long>, Friendship> friendshipRepository = repositoryFactory.createFriendshipRepository(new FriendshipValidator());
        UserService userService = new UserService(userRepository);
        FriendshipService friendshipService = new FriendshipService(friendshipRepository);
        NetworkService networkService = new NetworkService(friendshipService, userService);
        FriendshipRequestService requestService = new FriendshipRequestService(repositoryFactory.createFriendshipRequestRepository(new FriendshipRequestValidator()), friendshipService,userService);
        FXMLLoader loader = new FXMLLoader(StartApplication.class.getResource("/user-view.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
        UserController userController = loader.getController();
        userController.setService(userService, networkService,friendshipService,requestService , repositoryFactory.getPageSize());
        primaryStage.setTitle("Social network");
        primaryStage.show();
    }
}
