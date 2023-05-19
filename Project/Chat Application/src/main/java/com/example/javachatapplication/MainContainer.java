package com.example.javachatapplication;

import javafx.scene.layout.GridPane;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import java.util.List;
import java.io.File;


// MainContainer class
public class MainContainer {
    // Assign variables
    ServerThread serverThread;      // Server thread
    ClientGUIReceiver receiver;     // Receiver

    // Constructor
    public MainContainer(ServerThread serverThread, ClientGUIReceiver receiver) {
        // Set variables
        this.serverThread = serverThread;       // Server thread
        this.receiver = receiver;               // Receiver
        this.receiver.setMainController(this);  // Main controller
    }

    // Initialize variables
    // Input field
    @FXML
    private TextField inputField;

    // Output area
    @FXML
    private TextArea outputArea;

    // Send button
    @FXML
    private Button sendButton;

    // Client list
    @FXML
    private ListView clientList;

    // Main pane
    @FXML
    private GridPane mainPane;

    // File progress bar
    @FXML
    private ProgressBar fileProgressBar;

    // Send file button method
    @FXML
    protected void onSendButtonClick() {
        send();
    }

    // Enter input method
    @FXML
    public void onInputEnter(ActionEvent actionEvent){
        send();
    }

    // Send method
    private void send() {
        // Get text from input field
        String text = inputField.getText();
        // Broadcast text
        serverThread.broadcast(text);
    }

    // Send file button method
    @FXML
    private void onSendFileButtonClick() {
        // Create a new file chooser
        FileChooser fileChooser = new FileChooser();
        // File chooser dialog title
        File file = fileChooser.showOpenDialog(mainPane.getScene().getWindow());
        // If file is not null
        if(file != null) {
            // Send file to selected client
            serverThread.sendFile(clientList.getSelectionModel().getSelectedItem().toString(), file.getAbsolutePath());
        }
    }

    // Populate online list method
    public void populateOnlineList(List<String> clientNames) {
        // Clear client list
        clientList.getItems().clear();
        // For each client name in client names list
        clientNames.stream()
                .forEach(name -> clientList.getItems().add(name));
    }

    // Show broadcast method
    public void showBroadcast(String sender, String message) {
        // Append text to output area
        outputArea.appendText("\n" + sender + ": " + message);
        // Scroll output area to bottom
        outputArea.setScrollTop(Double.MAX_VALUE);
    }

    // Add to clients list method
    public void addToClients(String clientName) {
        // Add client name to client list
        clientList.getItems().add(clientName);
    }

    // Remove from clients list method
    public void removeFromClients(String clientName) {
        // Remove client from client list
        clientList.getItems().remove(clientName);
    }

    // Set file progress method
    public void setFileProgress(int progress) {
        // Set file bar progress
        fileProgressBar.setProgress(progress / 100.);
    }

    // Show file method
    public void showFile(String sender, long fileSize, String fileName) {
        // Create alert
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        // Set alert title
        alert.setTitle("File received");
        // Set alert header text
        alert.setHeaderText("File received from " + sender);
        // Set alert content text
        alert.setContentText("File name: " + fileName + "\n" + "File size: " + fileSize + " bytes");
        // Show and wait for alert
        alert.showAndWait();
    }

    // Show whisper method
    public void showWhisper(String s, String s1) {
        // Append text to output area
        outputArea.appendText("\n" + s + ": " + s1);
        // Scroll output area to bottom
        outputArea.setScrollTop(Double.MAX_VALUE);
    }
}
