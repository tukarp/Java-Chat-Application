package com.example.javachatapplication;


// Create ClientReceiver interface
public interface ClientReceiver {
    // Create methods
    void receiveOnline(String[] clientNames);  // receiveOnline
    void receiveLoginBroadcast(String sender);  // receiveLoginBroadcast
    void receiveLogoutBroadcast(String sender);  // receiveLogoutBroadcast
    void receiveWhisper(String sender, String message);  // receiveWhisper
    void receiveBroadcast(String sender, String message);  // receiveBroadcast
    void receiveFile(String sender, long fileSize, String fileName);  // receiveFile
    void receiveFileProgress(int progress);  // receiveFileProgress
}
