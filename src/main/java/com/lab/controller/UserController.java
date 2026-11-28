package com.lab.controller;

import com.lab.domain.User;
import com.lab.exceptions.ServiceException;
import com.lab.service.FriendshipRequestService;
import com.lab.service.FriendshipService;
import com.lab.service.NetworkService;
import com.lab.service.UserService;
import com.lab.util.observer.Observer;
import com.lab.util.paging.Page;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

/**
 * Controls the main window: shows the users one page at a time
 * and reloads the page every time the user service announces a change.
 */
public class UserController implements Observer {

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User,Long> idColumn;
    @FXML private TableColumn<User ,String> firstNameColumn;
    @FXML private TableColumn<User,String> lastNameColumn;
    @FXML private Button previousButton;
    @FXML private Button nextButton;
    @FXML private Label pageLabel;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button loginButton;

    private final ObservableList<User> usersOnPage = FXCollections.observableArrayList();
    private UserService userService;
    private NetworkService networkService;
    private FriendshipService friendshipService;
    private FriendshipRequestService requestService;

    private int pageSize;
    private int pageNumber = 0;
    private int totalPages = 1;

    @FXML
    private void initialize(){
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        userTable.setItems(usersOnPage);

        var noSelection = userTable.getSelectionModel().selectedItemProperty().isNull();
        editButton.disableProperty().bind(noSelection);
        deleteButton.disableProperty().bind(noSelection);
        loginButton.disableProperty().bind(noSelection);
    }

    /**
     * Gives the controller its service and shows the first page
     * @param userService the service that provides and changes the users
     * @param pageSize    the number of users on a page
     */
    public void setService(UserService userService , NetworkService networkService, FriendshipService friendshipService , FriendshipRequestService requestService,int pageSize){
        this.userService = userService;
        this.networkService=networkService;
        this.friendshipService = friendshipService;
        this.requestService=requestService;
        this.pageSize = pageSize;
        userService.addObserver(this);
        loadPage();
    }

    /**
     * Reloads the current page after the users changed.
     */
    @Override
    public void update() {
        loadPage();
    }

    @FXML
    private void onPrevious(){
        if(pageNumber > 0){
            pageNumber--;
            loadPage();
        }
    }

    @FXML
    private void onNext(){
        if(pageNumber < totalPages -1){
            pageNumber++;
            loadPage();
        }
    }

    private void loadPage(){
        Page<User> page = userService.findAllOnPage(pageNumber,pageSize);
        totalPages = Math.max(1 , (page.totalNumberOfElements() + pageSize - 1)/pageSize);
        if(pageNumber >=totalPages){
            pageNumber = totalPages - 1;
            page = userService.findAllOnPage(pageNumber,pageSize);
        }
        usersOnPage.clear();
        page.elementsOnPage().forEach(usersOnPage::add);
        pageLabel.setText("Page " + (pageNumber + 1) +"/" + totalPages);
        previousButton.setDisable(pageNumber == 0);
        nextButton.setDisable(pageNumber == totalPages - 1);
    }

    @FXML
    private void onAdd(){
        openForm(null, "Add user");
    }

    @FXML
    private void onEdit(){
        User selected = userTable.getSelectionModel().getSelectedItem();
        if(selected!= null){
            openForm(selected,"Edit user");
        }
    }

    @FXML
    private void onDelete(){
        User selected = userTable.getSelectionModel().getSelectedItem();
        if(selected==null)
            return;
        Window window = userTable.getScene().getWindow();
        String text = selected.getFirstName() + " " + selected.getLastName() ;
        if(!MessageAlert.confirm(window , "Delete user" , "Delete user \"" + text + "\" ?" ))
            return;
        try{
            networkService.deleteUser(selected.getId());
            MessageAlert.showMessage(window , "User deleted" , "User \"" + text + "\" was deleted");
        }catch (ServiceException e){
            MessageAlert.showErrorMessage(window , "Deletion failed" , "Error while deleting : " +e.getMessage());
        }
    }

    @FXML
    private void onLogin(){
        User selected = userTable.getSelectionModel().getSelectedItem();
        if(selected == null){
            return;
        }
        Window window = userTable.getScene().getWindow();
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user-profile-view.fxml"));
            Stage profileWindow = new Stage();
            profileWindow.setScene(new Scene(loader.load()));
            UserProfileController profileController = loader.getController();
            profileController.setService(selected,userService,networkService,friendshipService,requestService,pageSize);
            profileWindow.setTitle("Logged in as " + selected.getFirstName() + " " +selected.getLastName());
            profileWindow.initOwner(window);
            profileWindow.setOnHidden(event -> profileController.stopListening());
            profileWindow.show();
        } catch (IOException e) {
            MessageAlert.showErrorMessage(window,"Error" , "The profile window cannot be opened " + e.getMessage());
        }
    }
    private void openForm(User user , String title){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user-form-view.fxml"));
            Stage dialog = new Stage();
            dialog.setScene(new Scene(loader.load()));
            UserFormController formController = loader.getController();
            formController.setService(userService,user);
            dialog.setTitle(title);
            dialog.initOwner(userTable.getScene().getWindow());
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.setResizable(false);
            dialog.showAndWait();
        } catch (IOException e) {
            MessageAlert.showErrorMessage(userTable.getScene().getWindow(),"Error" , "The form cannot be opened " + e.getMessage());
        }
    }
}
