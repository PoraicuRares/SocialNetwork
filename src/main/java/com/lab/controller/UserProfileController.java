package com.lab.controller;

import com.lab.domain.FriendshipRequest;
import com.lab.domain.FriendshipStatus;
import com.lab.domain.User;
import com.lab.exceptions.RepositoryException;
import com.lab.exceptions.ServiceException;
import com.lab.service.FriendshipRequestService;
import com.lab.service.FriendshipService;
import com.lab.service.NetworkService;
import com.lab.service.UserService;
import com.lab.util.observer.Observable;
import com.lab.util.observer.Observer;
import com.lab.util.paging.Page;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

/**
 * Controls the window of a logged in user: the friends on pages, the received requests
 * and the other users, to whom a friendship request can be sent.
 * Reloads itself every time the users, the friendships or the requests change.
 */
public class UserProfileController implements Observer {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    @FXML private Label currentUserLabel;
    @FXML private TableView<User> friendsTable;
    @FXML private TableColumn<User,Long> friendIdColumn;
    @FXML private TableColumn<User,String> friendFirstNameColumn;
    @FXML private TableColumn<User,String> friendLastNameColumn;
    @FXML private Button previousFriendsButton;
    @FXML private Button nextFriendsButton;
    @FXML private Label friendsPageLabel;
    @FXML private TableView<FriendshipRequest> requestsTable;
    @FXML private TableColumn<FriendshipRequest , String> requestFromColumn;
    @FXML private TableColumn<FriendshipRequest , String> requestDateColumn;
    @FXML private Button approveButton;
    @FXML private Button rejectButton;
    @FXML private TableView<User> otherUsersTable;
    @FXML private TableColumn<User, Long> otherIdColumn;
    @FXML private TableColumn<User, String> otherFirstNameColumn;
    @FXML private TableColumn<User , String> otherLastNameColumn;
    @FXML private Button sendRequestButton;

    private final ObservableList<User> friendsOnPage = FXCollections.observableArrayList();
    private final ObservableList<FriendshipRequest> receivedRequests = FXCollections.observableArrayList();
    private final ObservableList<User> otherUsers = FXCollections.observableArrayList();

    private User currentUser;
    private UserService userService;
    private NetworkService networkService;
    private FriendshipService friendshipService;
    private FriendshipRequestService requestService;

    private int pageSize;
    private int friendsPageNumber = 0;
    private int friendsTotalPages = 1;

    @FXML
    private void initialize(){
        friendIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        friendFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        friendLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        friendsTable.setItems(friendsOnPage);

        requestFromColumn.setCellValueFactory(cell -> new SimpleStringProperty(nameOf(cell.getValue().getFromId())));
        requestDateColumn.setCellValueFactory(cell->new SimpleStringProperty(cell.getValue().getDate().format(DATE_FORMAT)));
        requestsTable.setItems(receivedRequests);

        otherIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        otherFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        otherLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        otherUsersTable.setItems(otherUsers);

        var noAnswerable = Bindings.createBooleanBinding(
                () -> requestsTable.getSelectionModel().getSelectedItem() == null
                        || requestsTable.getSelectionModel().getSelectedItem().getStatus() != FriendshipStatus.PENDING,
                requestsTable.getSelectionModel().selectedItemProperty());
        approveButton.disableProperty().bind(noAnswerable);
        rejectButton.disableProperty().bind(noAnswerable);
        sendRequestButton.disableProperty().bind(otherUsersTable.getSelectionModel().selectedItemProperty().isNull());
    }

    public void setService(User user,UserService userService, NetworkService networkService,
                           FriendshipService friendshipService,FriendshipRequestService requestService,int pageSize){
        this.currentUser = user;
        this.userService=userService;
        this.networkService =networkService;
        this.friendshipService = friendshipService;
        this.requestService = requestService;
        this.pageSize = pageSize;
        userService.addObserver(this);
        friendshipService.addObserver(this);
        requestService.addObserver(this);
        reload();
    }

    /**
     * Stops receiving the changes of the services; called when the window is closed.
     */
    public void stopListening(){
        userService.removeObserver(this);
        friendshipService.removeObserver(this);
        requestService.removeObserver(this);
    }

    @Override
    public void update() {
        if(userService.findOne(currentUser.getId()).isEmpty()){
            ((Stage) getWindow()).close();
        }
        else{
            reload();
        }
    }

    @FXML
    private void onPreviousFriends(){
        if(friendsPageNumber > 0){
            friendsPageNumber--;
            loadFriendsPage();
        }
    }

    @FXML
    private void onNextFriends(){
        if(friendsPageNumber < friendsTotalPages - 1){
            friendsPageNumber++;
            loadFriendsPage();
        }
    }

    @FXML
    private void onApprove(){
        FriendshipRequest request = requestsTable.getSelectionModel().getSelectedItem();
        try{
            requestService.approve(request.getId() , currentUser.getId());
            MessageAlert.showMessage(getWindow() , "Request approved" , "You are now friends with " + nameOf(request.getFromId()));
        }catch (ServiceException | RepositoryException e){
            MessageAlert.showErrorMessage(getWindow() , "The request could not be approved" , e.getMessage());
        }
    }

    @FXML
    private void onReject(){
        FriendshipRequest request = requestsTable.getSelectionModel().getSelectedItem();
        try{
            requestService.reject(request.getId() , currentUser.getId());
            MessageAlert.showMessage(getWindow() , "Request rejected" , "You rejected the request from " + nameOf(request.getFromId()));
        }catch (ServiceException | RepositoryException e){
            MessageAlert.showErrorMessage(getWindow() , "The request could not be rejected" , e.getMessage());
        }
    }

    @FXML
    private void onSendRequest(){
        User selected = otherUsersTable.getSelectionModel().getSelectedItem();
        try{
            requestService.sendRequest(currentUser.getId() , selected.getId());
            MessageAlert.showMessage(getWindow() , "Request sent" , "You sent a friendship request to " + nameOf(selected.getId()));
        }catch (ServiceException|RepositoryException|IllegalArgumentException e){
            MessageAlert.showErrorMessage(getWindow() , "The request could not be sent" , e.getMessage());
        }
    }

    private void reload(){
        try{
            currentUserLabel.setText("Logged as " +nameOf(currentUser.getId()));
            loadFriendsPage();
            receivedRequests.setAll(requestService.findReceivedRequests(currentUser.getId()));
            otherUsers.setAll(findOtherUsers());
        }catch (ServiceException | RepositoryException e){
            MessageAlert.showErrorMessage(getWindow() , "The data of the user cannot be loaded" , e.getMessage());
        }
    }

    private List<User> findOtherUsers(){
        Set<Long> friendsId = Set.copyOf(friendshipService.getFriendsId(currentUser.getId()));
        return userService.stream()
                .filter(user->!user.getId().equals(currentUser.getId()))
                .filter(user->!friendsId.contains(user.getId()))
                .toList();
    }

    private void loadFriendsPage(){
        Page<User> page = networkService.findFriendsOnPage(currentUser.getId(),friendsPageNumber,pageSize);
        friendsTotalPages = Math.max(1,(page.totalNumberOfElements() + pageSize - 1)/pageSize);
        if(friendsPageNumber >= friendsTotalPages){
            friendsPageNumber = friendsTotalPages - 1;
            page = networkService.findFriendsOnPage(currentUser.getId(),friendsPageNumber,pageSize);
        }
        friendsOnPage.clear();
        page.elementsOnPage().forEach(friendsOnPage::add);
        friendsPageLabel.setText("Page " + (friendsPageNumber + 1) + "/" + friendsTotalPages);
        previousFriendsButton.setDisable(friendsPageNumber == 0);
        nextFriendsButton.setDisable(friendsPageNumber == friendsTotalPages - 1);
    }

    private String nameOf(Long userId){
        return userService.findOne(userId)
                .map(user->user.getFirstName() + " " + user.getLastName())
                .orElse("user " + userId);
    }

    private Window getWindow(){
        return (Window)currentUserLabel.getScene().getWindow();
    }
}
