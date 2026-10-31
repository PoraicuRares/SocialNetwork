import com.lab.domain.Friendship;
import com.lab.domain.Tuple;
import com.lab.domain.User;
import com.lab.exceptions.RepositoryException;
import com.lab.exceptions.ValidationException;
import com.lab.repository.Repository;
import com.lab.repository.factory.RepositoryFactory;
import com.lab.service.FriendshipService;
import com.lab.service.NetworkService;
import com.lab.service.UserService;
import com.lab.ui.ConsoleUi;
import com.lab.validator.FriendshipValidator;
import com.lab.validator.UserValidator;
import com.lab.validator.Validator;

void main(){
    try{
        RepositoryFactory repositoryFactory = RepositoryFactory.getInstance("config.properties");

        Validator<User> userValidator = new UserValidator()
                .andThen(user->{
                   if(user.getFirstName().length() > 50 || user.getLastName().length() > 50)
                       throw new ValidationException("Names must have at most 50 characters");
                });

        Repository<Long ,User> userRepository = repositoryFactory.createUserRepository(userValidator);
        Repository<Tuple<Long,Long> , Friendship> friendshipRepository = repositoryFactory.createFriendshipRepository(new FriendshipValidator());

        UserService userService = new UserService(userRepository);
        FriendshipService friendshipService = new FriendshipService(friendshipRepository);
        NetworkService networkService = new NetworkService(friendshipService , userService);

        ConsoleUi ui = new ConsoleUi(userService , friendshipService , networkService);
        ui.run();
    }catch (RepositoryException | ValidationException | IllegalArgumentException | UnsupportedOperationException e){
        System.out.println("Error at start of the app :  " + e);
    }

}