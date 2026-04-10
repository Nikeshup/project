package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import model.User;
import service.LibraryService;

public class LoginController {

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    public void login() {
        User user = LibraryService.login(
            username.getText(),
            password.getText()
        );

        if (user != null) {
            showAlert("Login Success: " + user.getRole());
        } else {
            showAlert("Invalid Login");
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.show();
    }
}