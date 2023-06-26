package com.example.javachatapplication;

import javafx.application.Platform;
import java.util.Arrays;


// Create ClientGUIReceiver class
public class ClientGUIReceiver implements ClientReceiver {
    // Allocate variables
    // Create and initialize main container
    MainContainer mainController = null;

    // Set main controller
    public void setMainController(MainContainer mainController) {
        // Initialize main controller
        this.mainController = mainController;
    }

    // Override receiveOnline method
    @Override
    public void receiveOnline(String[] strings) {
        // Main controller populates online list
        mainController.populateOnlineList(Arrays.stream(strings).toList());
    }

    // Override receiveLoginBroadcast method
    @Override
    public void receiveLoginBroadcast(String sender) {
        // Main controller receives login broadcast
        Platform.runLater(()->mainController.addToClients(sender));
    }

    // Override receiveLogoutBroadcast method
    @Override
    public void receiveLogoutBroadcast(String sender) {
        // Main controller receives logout broadcast
        Platform.runLater(()->mainController.removeFromClients(sender));
    }

    // Override receiveWhisper method
    @Override
    public void receiveWhisper(String s, String s1) {
        // Main controller shows whisper
        mainController.showWhisper(s, s1);
    }

    // Override receiveBroadcast method
    @Override
    public void receiveBroadcast(String sender, String message) {
        // Main controller shows broadcast
        mainController.showBroadcast(sender, message);
    }

    // Override receiveFile method
    @Override
    public void receiveFile(String sender, long fileSize, String fileName) {
        // Main controller shows file
        mainController.showFile(sender, fileSize, fileName);
    }

    // Override receiveFileProgress method
    @Override
    public void receiveFileProgress(int progress) {
        // Main controller sets file progress
        mainController.setFileProgress(progress);
    }
}
