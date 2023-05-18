package com.example.ChatApplication;

import java.nio.file.Path;
import java.net.Socket;
import java.io.*;


// ServerThread class
public class ServerThread extends Thread {
    // Assign variables
    private Socket socket;  // Socket
    private PrintWriter writer;  // Writer
    private ClientReceiver receiver = null; // Receiver

    // Constructor
    public ServerThread(String address, int port) {
        try {
            // Create socket with address and port
            socket = new Socket(address, port);
        } catch (IOException e) {
            // Print stack trace if error occurs
            e.printStackTrace();
        }
    }

    // Set receiver
    public void setReceiver(ClientReceiver receiver) {
        // Set receiver
        this.receiver = receiver;
    }

    // Run server thread
    public void run(){
        try {
            // Create input and output streams
            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();

            // Create reader and writer
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            writer = new PrintWriter(output, true);

            // Create message buffer
            String message;

            // while message is not null
            while((message = reader.readLine()) != null) {
                // Get prefix and postfix of message
                String prefix = message.substring(0,2);
                String postfix = message.substring(2);

                // Print message
                System.out.println(message);

                // Switch prefix
                switch(prefix) {
                    case "BR" -> {  // Broadcast
                        // Split postfix into array
                        String[] postfixArray = postfix.split(" ",2);
                        // Receive broadcast
                        receiver.receiveBroadcast(postfixArray[0], postfixArray[1]);
                    }
                    case "WH" -> { // Whisper
                        // Split postfix into array
                        String[] postfixArray = postfix.split(" ",2);
                        // Receive whisper
                        receiver.receiveWhisper(postfixArray[0], postfixArray[1]);
                    }
                    case "FI" -> receiveFile(postfix);  // Receive file
                    case "LN" -> receiver.receiveLoginBroadcast(postfix);  // Receive login broadcast
                    case "LT" -> receiver.receiveLogoutBroadcast(postfix);  // Receive logout broadcast
                    case "ON" -> receiver.receiveOnline(postfix.split(" "));  // Receive online
                }
            }
        } catch (IOException e) {
            // Print stack trace if error occurs
            e.printStackTrace();
        }
    }

    // Login client
    public void login(String name) {
        // Send login message to server
        writer.println("LO" + " " + name);
    }

    // Send online message
    public void online() {
        // Send online message to server
        writer.println("ON");
    }

    // Send broadcast message
    public void broadcast(String message) {
        // Send broadcast message to server
        writer.println("BR" + " " + message);
    }

    // Send message
    private void send(String message){
        // Send message to server
        writer.println(message);
    }

    // Send whisper message
    public void whisper(String message) {
        // Send whisper message to server
        writer.println("WH" + message);
    }

    // Send file
    public void sendFile(String recipientName, String filePath) {
        // Create file object with file path
        File file = new File(filePath);

        try {
            // Create filesize variable
            long fileSize = file.length();

            // Send file message to server
            writer.println("FI" + recipientName + " " + fileSize + " " + file.getName());

            // Create file input and output streams
            FileInputStream fileIn = new FileInputStream(file);
            DataOutputStream fileOut = new DataOutputStream(socket.getOutputStream());

            // Create file buffer, sent size and counter
            byte[] buffer = new byte[64];
            long sentSize = 0;
            int counter;

            // While file is not fully sent
            while((counter = fileIn.read(buffer)) > 0) {
                // Write buffer to file output stream
                fileOut.write(buffer, 0, counter);

                // Increment sent size
                sentSize += counter;

                // Receive file progress
                receiver.receiveFileProgress((int)(sentSize * 100 / fileSize));
            }
            // Close file input stream
            fileIn.close();
        } catch (IOException e) {
            // Print stack trace if error occurs
            e.printStackTrace();
        }
    }

    // Receive file
    public void receiveFile(String command) {
        // Split command into array
        String[] commandArray = command.split(" ");
        // Get sender name
        String senderName = commandArray[0];

        // Get file size and file name
        long fileSize = Long.parseLong(commandArray[1]);
        String fileName = commandArray[2];

        try {
            // File object with temporary directory and file name
            File file = new File(String.valueOf(Path.of(System.getProperty("java.io.tmpdir")).resolve(fileName)));

            // Create file input stream and file output stream
            DataInputStream fileIn = new DataInputStream(socket.getInputStream());
            FileOutputStream fileOut = new FileOutputStream(file);

            // Create file buffer, received size and counter
            byte[] buffer = new byte[64];
            long receivedSize = 0;
            int counter;

            // Print receiving file message
            System.out.println("Receiving file from " + senderName + "...");

            // While file is not fully received
            while(receivedSize < fileSize) {
                // Read file input stream to buffer
                counter = fileIn.read(buffer);

                // Update received size
                receivedSize += counter;

                // Write buffer to file output stream
                fileOut.write(buffer, 0, counter);

                // Receive file progress
                receiver.receiveFileProgress((int)(receivedSize * 100 / fileSize));
            }
            // Print new line
            System.out.println();
            // Print file saved message
            System.out.println("File saved as: " + file.getAbsoluteFile());
        } catch (IOException e) {
            // Print stack trace if error occurs
            e.printStackTrace();
        }
    }
}
