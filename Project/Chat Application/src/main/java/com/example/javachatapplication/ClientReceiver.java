package com.example.javachatapplication;


// Create ClientReceiver interface
public interface ClientReceiver {
    // Create methods
    void receiveOnline(String[] clientNames);  // receiveOnline method
    void receiveLoginBroadcast(String sender);  // receiveLoginBroadcast method
    void receiveLogoutBroadcast(String sender);  // receiveLogoutBroadcast method
    void receiveWhisper(String sender, String message);  // receiveWhisper method
    void receiveBroadcast(String sender, String message);  // receiveBroadcast method
    void receiveFile(String sender, long fileSize, String fileName);  // receiveFile method
    void receiveFileProgress(int progress);  // receiveFileProgress method
}
