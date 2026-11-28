package com.lab.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Window;

import java.util.concurrent.ForkJoinWorkerThread;

/**
 * Shows the message windows of the application: errors, information and confirmations.
 */
public final class MessageAlert {
    private MessageAlert(){

    }

    /**
     * Shows an error message and waits until it is closed.
     * @param owner  the window the message belongs to, or null
     * @param header a short title of the error
     * @param text   the message of the error
     */
    public static void showErrorMessage(Window owner , String header ,String text){
        show(owner, Alert.AlertType.ERROR , header, text);
    }

    /**
     * Shows an information message and waits until it is closed.
     * @param owner  the window the message belongs to, or null
     * @param header a short title of the message
     * @param text   the message
     */
    public static void showMessage(Window owner, String header , String text){
        show(owner ,Alert.AlertType.INFORMATION , header , text);
    }

    /**
     * Asks the user a yes / no question.
     * @param owner  the window the question belongs to, or null
     * @param header a short title of the question
     * @param text   the question
     * @return true if the user chose Yes
     */
    public static boolean confirm(Window owner , String header , String text){
        Alert alert = create(owner, Alert.AlertType.CONFIRMATION , header , text , ButtonType.NO , ButtonType.YES);
        return alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    private static void show(Window owner , Alert.AlertType type, String header, String text){
        create(owner , type , header , text).showAndWait();
    }

    private static Alert create(Window owner , Alert.AlertType type , String header , String text , ButtonType... buttons){
        Alert alert = new Alert(type,text , buttons);
        alert.setHeaderText(header);
        if(owner!=null){
            alert.initOwner(owner);
        }
        return alert;
    }
}
