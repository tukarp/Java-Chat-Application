import java.net.Socket;
import java.io.*;


// ClientThread class
public class ClientThread extends Thread {
    // Assign variables
    private final Socket socket;        // Socket
    private final Server server;        // Server
    private PrintWriter writer;         // Writer
    private String clientName = null;   // Client name

    // Constructor
    public ClientThread(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    // Get socket
    public Socket getSocket() {
        return socket;
    }

    // Send message
    public void send(String message) {
        // Print message
        writer.println(message);
    }

    // Get client name
    public String getClientName() {
        // Return client name
        return clientName;
    }

    // Login client
    public void login(String name) {
        // Set client name
        clientName = name;
        // Send online message to client
        server.online(this);
        // Broadcast login message to all clients
        server.broadcastLogin(this);
    }

    // Run client thread
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
            while((message = reader.readLine()) != null){
                // Get prefix and postfix of message
                String prefix = message.substring(0,2);
                String postfix = message.substring(2);

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
            // Close socket
            System.out.println("closed");
            // Remove client
            server.removeClient(this);
        } catch (IOException e) {
            // Print stack trace if error occurs
            e.printStackTrace();
        }
    }
}
