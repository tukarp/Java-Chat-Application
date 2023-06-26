import java.util.stream.Collectors;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.io.IOException;
import java.util.Optional;
import java.net.Socket;
import java.util.List;


// Server class
public class Server {
    // Initialize logo
    private static final String LOGO = """
                   __                     ________          __     ___                ___            __  _          \s
                  / /___ __   ______ _   / ____/ /_  ____ _/ /_   /   |  ____  ____  / (_)________ _/ /_(_)___  ____\s
             __  / / __ `/ | / / __ `/  / /   / __ \\/ __ `/ __/  / /| | / __ \\/ __ \\/ / / ___/ __ `/ __/ / __ \\/ __ \\
            / /_/ / /_/ /| |/ / /_/ /  / /___/ / / / /_/ / /_   / ___ |/ /_/ / /_/ / / / /__/ /_/ / /_/ / /_/ / / / /
            \\____/\\__,_/ |___/\\__,_/   \\____/_/ /_/\\__,_/\\__/  /_/  |_/ .___/ .___/_/_/\\___/\\__,_/\\__/_/\\____/_/ /_/\s
                                                                     /_/   /_/                                      \s""";

    // Allocate variables
    private ServerSocket serverSocket;                              // Server socket variable
    private final List<ClientThread> clients = new ArrayList<>();   // Clients list variable

    // Constructor
    public Server(int port) {
        try {
            // Create server socket with given port
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            // Catch IOException
            e.printStackTrace();
        }
    }

    // Listen for new clients
    public void listen() {
        // Print logo
        System.out.println(LOGO);

        // Infinite loop
        while(true) {
            // Create new client socket
            Socket clientSocket;
            try {
                // Accept new client
                clientSocket = serverSocket.accept();
                // Create new client thread
                ClientThread thread = new ClientThread(clientSocket, this);
                // Add client to clients list
                clients.add(thread);
                // Start client thread
                thread.start();
            } catch (IOException e) {
                // Catch IOException
                e.printStackTrace();
            }
        }
    }

    // Remove client from clients list method
    public void removeClient(ClientThread client) {
        // Remove client from clients list
        clients.remove(client);
        // Broadcast logout message to all clients
        broadcastLogout(client);
    }

    // Broadcast message method
    public void broadcast(ClientThread sender, String message){
        // For each client in clients list
        for(var currentClient : clients)
            // Broadcast message to the client
            currentClient.send("BR" + sender.getClientName() + ":" + message);
    }

    // Broadcast login message method
    public void broadcastLogin(ClientThread client) {
        // For each client in clients list
        for(var currentClient : clients)
            // Check if client is not the one logging in
            if(currentClient != client)
                // Broadcast login message to the client
                currentClient.send("LN" + client.getClientName());
    }

    // Broadcast logout message method
    public void broadcastLogout(ClientThread client) {
        // For each client in clients list
        for(var currentClient : clients)
            // Broadcast logout message to the client
            currentClient.send("LT" + client.getClientName());
    }

    // Get client by name method
    private Optional<ClientThread> getClient(String clientName) {
        // Return first client with given name
        return clients.stream()
                .filter(client -> clientName.equals(client.getClientName()))  // Filter clients by name
                .findFirst();  // Return first client
    }

    // Send whisper message method
    public void whisper(ClientThread sender, String message) {
        // Split message into array
        String[] messageArray = message.split(" ");
        // Get recipient name
        String recipientName = messageArray[0];

        // Get recipient client
        Optional<ClientThread> recipient = getClient(recipientName);
        // Check if recipient exists
        if(recipient.isPresent()) {
            // Send whisper message to recipient
            recipient.get().send("WH" + sender.getClientName() + " " + messageArray[1]);
        // Otherwise
        } else
            // Send error message to sender
            sender.send("NU" + recipientName);
    }

    // List online clients method
    public void online(ClientThread sender) {
        // Get list of all online clients
        String listString = clients.stream()  // Stream clients list
                .map(ClientThread::getClientName)  // Map client to client name
                .collect(Collectors.joining(" "));  // Join client names with space
        // Send list of online clients to sender
        sender.send("ON" + listString);
    }

    // Send file method
    public void sendFile(ClientThread sender, String message) throws IOException {
        // Split message into array
        String[] messageArray = message.split(" ");
        // Get recipient name
        String recipientName = messageArray[0];

        // Get file size and file name
        long fileSize = Long.parseLong(messageArray[1]);  // File size
        String fileName = messageArray[2];  // File name

        // Get recipient client
        Optional<ClientThread> recipient = getClient(recipientName);

        // Check if recipient exists
        if(recipient.isPresent()) {
            // Create file input and output streams
            DataInputStream fileIn = new DataInputStream(sender.getSocket().getInputStream());                  // File input stream
            DataOutputStream fileOut = new DataOutputStream(recipient.get().getSocket().getOutputStream());     // File output stream

            // Create file buffer, received size and counter
            byte[] buffer = new byte[64];  // File buffer
            long receivedSize = 0;  // Received size
            int counter;  // Counter

            // Send file size and file name to recipient
            recipient.get().send("FI" + sender.getClientName() + " " + fileSize + " " + fileName);
            // While file is not fully received
            while(receivedSize < fileSize) {
                // Read file input stream to buffer
                counter = fileIn.read(buffer);

                // Update received size
                receivedSize += counter;

                // Write buffer to file output stream
                fileOut.write(buffer, 0, counter);

                // Print received size and remaining size
                System.out.println(receivedSize + " " + (fileSize - receivedSize));
            }
        // Otherwise
        } else {
            // Send error message to sender
            sender.send("NU" + recipientName);
        }
    }
}
