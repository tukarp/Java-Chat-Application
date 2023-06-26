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
    // Initialize logo
    private static final String LOGO = """
                   __                     ________          __     ___                ___            __  _          \s
                  / /___ __   ______ _   / ____/ /_  ____ _/ /_   /   |  ____  ____  / (_)________ _/ /_(_)___  ____\s
             __  / / __ `/ | / / __ `/  / /   / __ \\/ __ `/ __/  / /| | / __ \\/ __ \\/ / / ___/ __ `/ __/ / __ \\/ __ \\
            / /_/ / /_/ /| |/ / /_/ /  / /___/ / / / /_/ / /_   / ___ |/ /_/ / /_/ / / / /__/ /_/ / /_/ / /_/ / / / /
            \\____/\\__,_/ |___/\\__,_/   \\____/_/ /_/\\__,_/\\__/  /_/  |_/ .___/ .___/_/_/\\___/\\__,_/\\__/_/\\____/_/ /_/\s
                                                                     /_/   /_/                                      \s""";

    // Override the start method
    @Override
    public void start(Stage stage) throws IOException {
        // Print logo
        System.out.println(LOGO);

        // Server thread
        // Create a new server thread with given host and port
        ServerThread serverThread = new ServerThread("localhost", 5000);

        // Client GUI receiver
        // Create a new client GUI receiver
        ClientGUIReceiver receiver = new ClientGUIReceiver();

        // Set receiver for server thread
        serverThread.setReceiver(receiver);
        // Set server thread as daemon
        serverThread.setDaemon(true);
        // Start server thread
        serverThread.start();

        // FMXL loader
        // Create a new FXML loader
        FXMLLoader fxmlLoader = new FXMLLoader(ChatApplication.class.getResource("view.fxml"));
        // Set fxml loader controller factory
        fxmlLoader.setControllerFactory(controllerClass -> new MainContainer(serverThread, receiver));

        // Scene
        // Create a new scene with given width and height
        Scene scene = new Scene(fxmlLoader.load(), 640, 480);

        // Text input dialog
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

        // Check if result is present
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
