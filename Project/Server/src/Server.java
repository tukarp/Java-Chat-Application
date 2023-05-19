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
    // Assign variables
    private ServerSocket serverSocket;  // Server socket
    private final List<ClientThread> clients = new ArrayList<>();  // Clients list

    // Constructor
    public Server(int port) {
        try {
            // Create server socket with given port
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            // Print stack trace if error occurs
            e.printStackTrace();
        }
    }

    // Listen for new clients
    public void listen() {
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
                // Print stack trace if error occurs
                e.printStackTrace();
            }
        }
    }

    // Remove client from list
    public void removeClient(ClientThread client) {
        // Remove client from clients list
        clients.remove(client);
        // Broadcast logout message to all clients
        broadcastLogout(client);
    }

    // Broadcast message to all clients
    public void broadcast(ClientThread sender, String message){
        // For each client in clients list
        for(var currentClient : clients)
            // Send message to client
            currentClient.send("BR" + sender.getClientName() + ":" + message);
    }

    // Broadcast login message to all clients
    public void broadcastLogin(ClientThread client) {
        // For each client in clients list
        for(var currentClient : clients)
            // If client is not the one logging in
            if(currentClient != client)
                // Send login message to all clients
                currentClient.send("LN" + client.getClientName());
    }

    // Broadcast logout message to all clients
    public void broadcastLogout(ClientThread client) {
        // For each client in clients list
        for(var currentClient : clients)
            // Send logout message to all clients
            currentClient.send("LT" + client.getClientName());
    }

    // Get client by name
    private Optional<ClientThread> getClient(String clientName) {
        // Return first client with given name
        return clients.stream()
                .filter(client -> clientName.equals(client.getClientName()))
                .findFirst();
    }

    // Send whisper message to client
    public void whisper(ClientThread sender, String message) {
        // Split message into array
        String[] messageArray = message.split(" ");
        // Get recipient name
        String recipientName = messageArray[0];

        // Get recipient client
        Optional<ClientThread> recipient = getClient(recipientName);
        // If recipient exists
        if(recipient.isPresent()) {
            // Send whisper message to recipient
            recipient.get().send("WH" + sender.getClientName() + " " + messageArray[1]);
        } else
            // Send error message to sender
            sender.send("NU" + recipientName);
    }

    // List all online clients
    public void online(ClientThread sender) {
        // Get list of all online clients
        String listString = clients.stream()
                .map(ClientThread::getClientName)
                .collect(Collectors.joining(" "));
        // Send list of online clients to sender
        sender.send("ON" + listString);
    }

    // Send file to client
    public void sendFile(ClientThread sender, String message) throws IOException {
        // Split message into array
        String[] messageArray = message.split(" ");
        // Get recipient name
        String recipientName = messageArray[0];

        // Get file size and file name
        long fileSize = Long.parseLong(messageArray[1]);
        String fileName = messageArray[2];

        // Get recipient client
        Optional<ClientThread> recipient = getClient(recipientName);

        // If recipient exists
        if(recipient.isPresent()) {
            // Create file input and output streams
            DataInputStream fileIn = new DataInputStream(sender.getSocket().getInputStream());
            DataOutputStream fileOut = new DataOutputStream(recipient.get().getSocket().getOutputStream());

            // Create file buffer, received size and counter
            byte[] buffer = new byte[64];
            long receivedSize = 0;
            int counter;

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
        } else {
            // Send error message to sender
            sender.send("NU" + recipientName);
        }
    }
}
