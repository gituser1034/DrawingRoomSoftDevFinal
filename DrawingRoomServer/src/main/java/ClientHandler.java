import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter writer;
    private int id;
    // used to create unique id
    private static int clientCount = 0;

    public ClientHandler(Socket socket) {
        try {
            // update count to keep id unique
            clientCount++;
            this.id = clientCount;
            // assign the socket, reader, writer
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new PrintWriter(this.socket.getOutputStream(), true);
            Server.clientHandlers.add(this);
        } catch (IOException e) {
            close();
            System.out.println("Error starting up client handler");
        }
    }

    @Override
    // this method is used when the thread is started
    // it listens for a string of parameters sent by the client
    public void run() {
        String parameters;
        while (true) {
            try {
                // waits for a newline character to proceed
                parameters = bufferedReader.readLine();
                // When a client disconnects, it will write a null character
                if(parameters != null) {
                    updateClients(parameters);
                }
                // close the clienthandler and break out this loop if null is
                // sent from client
                else {
                    close();
                    break;
                }
            } catch (IOException e) {
                close();
                System.out.println("Client disconnected or error recieving msg");
                break;
            }
        }
    }

    // signal to the other clients of the changes made to sync everything up
    public void updateClients(String parameters) {
        for (ClientHandler clientHandler : Server.clientHandlers) {
            // send the information to all clients besides the one this is connected to
            if (!(clientHandler.id == this.id)) {
                clientHandler.writer.println(parameters);
            }
        }
    }

    // closes the reader, writer, and socket
    public void close() {
        // remove this clienthandler from the arraylist that contains all of them
        Server.clientHandlers.remove(this);
        try {
            if (bufferedReader != null) {
                this.bufferedReader.close();
            }
            if (writer != null) {
                this.writer.close();
            }
            if (socket != null) {
                this.socket.close();
            }
            System.out.println("Closed the Clienthandler");
        } catch (IOException e) {
            System.out.println("Error closing the socket and stuff");
            e.printStackTrace();
        }
    }
}
