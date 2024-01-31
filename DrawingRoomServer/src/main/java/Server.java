
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Scanner;

public class Server{

    private ServerSocket serverSocket;
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    public Server(ServerSocket serverSocket) throws SocketException {
        // initialize the socket
        this.serverSocket = serverSocket;
        this.serverSocket.setReuseAddress(true);
    }

    public void startServer() {
        try {
            // will keep listening for client connections
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New connection");
                // creates a new clienthandler to listen for that client's data
                ClientHandler clientHandler = new ClientHandler(socket);
                // create a new thread for the client handler
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            close();
        }
    }

    // closes the server socket, so no more clients can connect
    public void close() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.out.println("Error closing the socket");
        }
    }
    // shuts down all connections (client handlers too)
    public void shutdown() {
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.close();
        }
        close();
    }
    public static void main(String[] args) {
        try {
            // create a server socket at port 6666
            ServerSocket serverSocket = new ServerSocket(6666);
            Server server = new Server(serverSocket);
            System.out.println("Type 'exit' to shutdown the server and connections");
            // new thread to listen for user input, incase the server needs to be
            // shutdown
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Scanner scanner = new Scanner(System.in);
                    String message = "";
                    while(!message.equals("exit")){
                        // this doesn't work in the terminal for some reason
                        message = scanner.nextLine();
                    }
                    server.shutdown();
                    scanner.close();
                }
            }).start();
            server.startServer();
        }catch(Exception e) {
            System.out.println("Error creating server");
        }
    }
}