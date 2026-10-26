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

void main(){
    try{
        RepositoryFactory repositoryFactory = RepositoryFactory.getInstance("config.properties");

        Repository<Long ,User> userRepository = repositoryFactory.createUserRepository(new UserValidator());
        Repository<Tuple<Long,Long> , Friendship> friendshipRepository = repositoryFactory.createFriendshipRepository(new FriendshipValidator());

        UserService userService = new UserService(userRepository);
        FriendshipService friendshipService = new FriendshipService(friendshipRepository);
        NetworkService networkService = new NetworkService(friendshipService , userService);

        ConsoleUi ui = new ConsoleUi(userService , friendshipService , networkService);
        ui.run();
    }catch (RepositoryException | ValidationException | IllegalArgumentException e){
        System.out.println("Error at start of the app :  " + e);
    }

}