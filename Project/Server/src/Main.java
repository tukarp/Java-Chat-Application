// Java Chat Application - Server
// Made by github.com/tukarp


// Main class
public class Main {
    public static void main(String[] args) {
        // Create a new Server object with port 5000
        Server server = new Server(5000);
        // Start the server
        server.listen();
    }
}
