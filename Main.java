package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        Label title = new Label("Online Library Management System");

        TextField username = new TextField();
        username.setPromptText("Username");

        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        Button loginBtn = new Button("Login");
        Label message = new Label();

        loginBtn.setOnAction(e -> {
            if(username.getText().equals("admin") && password.getText().equals("admin")) {
                message.setText("Login Successful!");
            } else {
                message.setText("Invalid Credentials!");
            }
        });

        VBox root = new VBox(10, title, username, password, loginBtn, message);
        Scene scene = new Scene(root, 400, 300);

        stage.setTitle("OLMS");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
