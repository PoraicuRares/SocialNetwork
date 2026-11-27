package com.lab.controller;

import com.lab.domain.User;
import com.lab.service.UserService;
import com.lab.util.observer.Observer;
import com.lab.util.paging.Page;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

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

    private final ObservableList<User> usersOnPage = FXCollections.observableArrayList();
    private UserService userService;
    private int pageSize;
    private int pageNumber = 0;
    private int totalPages = 1;

    @FXML
    private void initialize(){
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        userTable.setItems(usersOnPage);
    }

    /**
     * Gives the controller its service and shows the first page
     * @param userService the service that provides and changes the users
     * @param pageSize    the number of users on a page
     */
    public void setService(UserService userService , int pageSize){
        this.userService = userService;
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
}
