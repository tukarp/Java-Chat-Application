package com.example.javachatapplication;

import java.nio.file.Path;
import java.net.Socket;
import java.io.*;


// ServerThread class
public class ServerThread extends Thread {
    // Allocate variables
    private ClientReceiver receiver = null;     // Receiver
    private PrintWriter writer;                 // Writer
    private Socket socket;                      // Socket

    // Constructor
    public ServerThread(String address, int port) {
        try {
            // Create socket with given address and port
            socket = new Socket(address, port);
        } catch (IOException e) {
            // Catch IOException
            e.printStackTrace();
        }
    }

    // Set receiver method
    public void setReceiver(ClientReceiver receiver) {
        // Set receiver
        this.receiver = receiver;
    }

    // Run server thread method
    public void run(){
        try {
            // Create input and output streams
            InputStream input = socket.getInputStream();     // Input stream
            OutputStream output = socket.getOutputStream();  // Output stream

            // Create reader and writer
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));  // Reader
            writer = new PrintWriter(output, true);  // Writer

            // Create message buffer
            String message;

            // while message is not null
            while((message = reader.readLine()) != null) {
                // Get prefix and postfix of message
                String prefix = message.substring(0,2);           // Prefix
                String postfix = message.substring(2);  // Postfix

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
            // Catch IOException
            e.printStackTrace();
        }
    }

    // Login method
    public void login(String name) {
        // Write login message
        writer.println("LO" + " " + name);
    }

    // Online method
    public void online() {
        // Write online message
        writer.println("ON");
    }

    // Broadcast method
    public void broadcast(String message) {
        // Write broadcast message
        writer.println("BR" + " " + message);
    }

    // Send method
    private void send(String message){
        // Write send message
        writer.println(message);
    }

    // Whisper method
    public void whisper(String message) {
        // Write whisper message
        writer.println("WH" + message);
    }

    // Send file method
    public void sendFile(String recipientName, String filePath) {
        // Create file object with file path
        File file = new File(filePath);

        try {
            // Create filesize variable
            long fileSize = file.length();

            // Send file message to server
            writer.println("FI" + recipientName + " " + fileSize + " " + file.getName());

            // Create file input and output streams
            FileInputStream fileIn = new FileInputStream(file);                         // File input stream
            DataOutputStream fileOut = new DataOutputStream(socket.getOutputStream());  // File output stream

            // Create file buffer, sent size and counter
            byte[] buffer = new byte[64];  // Buffer
            long sentSize = 0;  // Sent size
            int counter;  // Counter

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
            // Catch IOException
            e.printStackTrace();
        }
    }

    // Receive file method
    public void receiveFile(String command) {
        // Split command into array
        String[] commandArray = command.split(" ");
        // Get sender name
        String senderName = commandArray[0];

        // Get file size and file name
        long fileSize = Long.parseLong(commandArray[1]);  // File size
        String fileName = commandArray[2];  // File name

        try {
            // File object with temporary directory and file name
            File file = new File(String.valueOf(Path.of(System.getProperty("java.io.tmpdir")).resolve(fileName)));

            // Create file input stream and file output stream
            DataInputStream fileIn = new DataInputStream(socket.getInputStream());  // File input stream
            FileOutputStream fileOut = new FileOutputStream(file);                  // File output stream

            // Create file buffer, received size and counter
            byte[] buffer = new byte[64];  // Buffer
            long receivedSize = 0;  // Received size
            int counter;  // Counter

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
            // Catch IOException
            e.printStackTrace();
        }
    }
}
