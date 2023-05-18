package com.example.ChatApplication;

import javafx.application.Platform;
import java.util.Arrays;


// Create ClientGUIReceiver class
public class ClientGUIReceiver implements ClientReceiver {
    // Assign variables
    // Create MainContainer object
    MainContainer mainController = null;

    // Create setMainController
    public void setMainController(MainContainer mainController) {
        // Set mainController
        this.mainController = mainController;
    }

    // Create receiveOnline
    @Override
    public void receiveOnline(String[] strings) {
        // Main controller populates online list
        mainController.populateOnlineList(Arrays.stream(strings).toList());
    }

    // Create receiveLoginBroadcast
    @Override
    public void receiveLoginBroadcast(String sender) {
        // Main controller adds to clients
        Platform.runLater(()->mainController.addToClients(sender));
    }

    // Create receiveLogoutBroadcast
    @Override
    public void receiveLogoutBroadcast(String sender) {
        // Main controller removes from clients
        Platform.runLater(()->mainController.removeFromClients(sender));
    }

    // Create receiveWhisper
    @Override
    public void receiveWhisper(String s, String s1) {
        // Main controller shows whisper
        mainController.showWhisper(s, s1);
    }

    // Create receiveBroadcast
    @Override
    public void receiveBroadcast(String sender, String message) {
        // Main controller shows broadcast
        mainController.showBroadcast(sender, message);
    }

    // Create receiveFile
    @Override
    public void receiveFile(String sender, long fileSize, String fileName) {
        // Main controller shows file
        mainController.showFile(sender, fileSize, fileName);
    }

    // Create receiveFileProgress
    @Override
    public void receiveFileProgress(int progress) {
        // Main controller sets file progress
        mainController.setFileProgress(progress);
    }
}
