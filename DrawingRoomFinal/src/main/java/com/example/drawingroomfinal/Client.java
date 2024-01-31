package com.example.drawingroomfinal;

import javafx.scene.paint.Paint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements Runnable{
    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter writer;

    public Client(Socket socket) {
        try {
            // assign the socket, reader, writer
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new PrintWriter(this.socket.getOutputStream(), true);
        } catch (IOException e) {
            close();
            System.out.println("Error starting up client");
        }
    }

    @Override
    // this method is used to listen for server updates
    public void run() {
        String parameters;
        while (true) {
            try {
                // waits for a newline character to proceed
                parameters = bufferedReader.readLine();
                // When a clienthandler closes, it might write a null character
                if(parameters != null && parameters.split(",").length == 5) {
                    // Parse the string of parameters into the actual types required
                    String[] paramArray = parameters.split(",");
                    double x = Double.parseDouble(paramArray[0]);
                    double y = Double.parseDouble(paramArray[1]);
                    double size = Double.parseDouble(paramArray[2]);
                    boolean isErase;
                    if(paramArray[3].equals("true")) {
                        isErase = true;
                    }
                    else {
                        isErase = false;
                    }
                    Paint paint = Paint.valueOf(paramArray[4]);

                    // draw the stuff and parse string
                    RoomController.brushDraw(RoomController.brushedShape,x,y,size,isErase,paint);
                }
                // for shape drawing
                else if(parameters != null && parameters.split(" ").length == 6) {
                    // draw the stuff and parse string
                    RoomController.shapeDraw(parameters);

                }
                // close the client if null is sent, for some reason
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
    public void writeToServer(String parameters) {
        writer.println(parameters);
    }


    // closes the reader, writer, and socket
    public void close() {
        try {
            if (bufferedReader != null) {
                this.bufferedReader.close();
            }
            if (writer != null) {
                // will send null to clienthandler
                this.writer.close();
            }
            if (socket != null) {
                this.socket.close();
            }
            System.out.println("Closed the Client");
        } catch (IOException e) {
            System.out.println("Error closing the socket/stuff");
            e.printStackTrace();
        }
    }
}
