import java.net.Socket;
import java.io.*;


// ClientThread class
public class ClientThread extends Thread {
    // Allocate variables
    private final Socket socket;        // Socket variable
    private final Server server;        // Server variable
    private PrintWriter writer;         // Writer variable
    private String clientName = null;   // Client name variable

    // Constructor
    public ClientThread(Socket socket, Server server) {
        // Initialize variables
        this.socket = socket;  // Initialize socket
        this.server = server;  // Initialize server
    }

    // Get socket method
    public Socket getSocket() {
        // Return socket
        return socket;
    }

    // Send message method
    public void send(String message) {
        // Write message
        writer.println(message);
    }

    // Get client name method
    public String getClientName() {
        // Return client name
        return clientName;
    }

    // Login client method
    public void login(String name) {
        // Set client name
        clientName = name;
        // Send online message to client
        server.online(this);
        // Broadcast login message to all clients
        server.broadcastLogin(this);
    }

    // Run client thread method
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

            // while message is not empty
            while((message = reader.readLine()) != null){
                // Get prefix and postfix of message
                String prefix = message.substring(0,2);           // Prefix
                String postfix = message.substring(2);  // Postfix

                // Switch prefix
                switch(prefix) {
                    case "LO" -> login(postfix);  // Login
                    case "ON" -> server.online(this);  // Online
                    case "WH" -> server.whisper(this, postfix);  // Whisper
                    case "FI" -> server.sendFile(this, postfix);  // Send file
                    case "BR" -> server.broadcast(this, postfix);  // Broadcast
                }
                // Print message
                System.out.println(message);
            }
            // Print closed message
            System.out.println("closed");
            // Remove client
            server.removeClient(this);
        } catch (IOException e) {
            // Catch IOException
            e.printStackTrace();
        }
    }
}
