import com.lab.domain.Friendship;
import com.lab.domain.User;
import com.lab.repository.Repository;
import com.lab.service.FriendshipService;
import com.lab.service.NetworkService;
import com.lab.service.UserService;
import com.lab.ui.ConsoleUi;
import com.lab.validator.FriendshipValidator;
import com.lab.validator.UserValidator;
import com.lab.validator.Validator;

void main(){
    Repository<Integer , User> userRepository = new InMemoryUserRepo();
    Repository<Integer , Friendship> friendRepository = new InMemoryFriendRepo();
    Validator<User> userValidator = new UserValidator();
    Validator<Friendship> friendValidator = new FriendshipValidator();

    FriendshipService friendshipService = new FriendshipService(friendRepository , userRepository , friendValidator);
    UserService userService = new UserService(userRepository , userValidator);
    NetworkService networkService= new NetworkService(userRepository , friendRepository);

    ConsoleUi ui = new ConsoleUi(userService , friendshipService , networkService);
    ui.run();
}