package com.example.javachatapplication;

import javafx.scene.control.TextInputDialog;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Optional;


// Create ChatApplication class
public class ChatApplication extends Application {
    // Override the start method
    @Override
    public void start(Stage stage) throws IOException {
        // Create a new server thread with given host and port
        ServerThread serverThread = new ServerThread("localhost", 5000);

        // Create a new receiver
        ClientGUIReceiver receiver = new ClientGUIReceiver();
        // Set receiver for server thread
        serverThread.setReceiver(receiver);
        // Set server thread as daemon
        serverThread.setDaemon(true);
        // Start server thread
        serverThread.start();

        // Create a new FXML loader
        FXMLLoader fxmlLoader = new FXMLLoader(ChatApplication.class.getResource("view.fxml"));
        // Set controller factory
        fxmlLoader.setControllerFactory(controllerClass -> new MainContainer(serverThread, receiver));

        // Create a new scene
        Scene scene = new Scene(fxmlLoader.load(), 640, 480);

        // Create a new text input dialog
        TextInputDialog dialog = new TextInputDialog();
        // Set dialog title to "Login"
        dialog.setTitle("Login");
        // Set dialog header text to "Login"
        dialog.setHeaderText("Login");
        // Set dialog content text to "Please enter your name:"
        dialog.setContentText("Please enter your name:");

        // Show dialog and wait for result
        Optional<String> result = dialog.showAndWait();
        // Create a new string for login
        String login = null;

        // If result is present
        if(result.isPresent()) {
            // Set login to result
            login = result.get();

            // Login to server
            serverThread.login(login);

            // Set title to "Chat - " + login
            stage.setTitle("Chat - " + login);
            // Set scene
            stage.setScene(scene);
            // Show stage
            stage.show();
        }
    }

    // Main method
    public static void main(String[] args) throws IOException {
        // Launch application
        launch();
    }
}
