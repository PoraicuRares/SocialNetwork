package com.lab.controller;

import com.lab.domain.User;
import com.lab.exceptions.RepositoryException;
import com.lab.exceptions.ServiceException;
import com.lab.exceptions.ValidationException;
import com.lab.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controls the form used both for adding a user (no user given) and for changing an existing one.
 */
public class UserFormController {
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;

    private UserService userService;
    private User user;

    public void setService(UserService userService, User user){
        this.userService = userService;
        this.user = user;
        if(user!=null){
            firstNameField.setText(user.getFirstName());
            lastNameField.setText(user.getLastName());
        }
    }

    @FXML
    private void onSave(){
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        try{
            if(user == null){
                User saved = userService.add(new User(firstName,lastName));
                MessageAlert.showMessage(getWindow() , "User added","The user was added with id " + saved.getId());
            }
            else{
                userService.update(user.getId() , firstName,lastName);
                MessageAlert.showMessage(getWindow() , "User updated" , "The user with id " + user.getId() + " was updated");
            }
            getWindow().close();
        }catch (ValidationException e){
            MessageAlert.showErrorMessage(getWindow() ,"Invalid user" , e.getMessage());
        }catch(ServiceException | RepositoryException | IllegalArgumentException e){
            MessageAlert.showErrorMessage(getWindow() , "The user could not be saved" , e.getMessage());
        }

    }

    @FXML
    private void onCancel(){
        getWindow().close();
    }

    private Stage getWindow(){
        return(Stage)firstNameField.getScene().getWindow();
    }
}
