package com.example.drawingroomfinal;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class RoomController implements Initializable
{
    @FXML
    private Button backHome;

    @FXML
    private Canvas canvas;

    @FXML
    private TextField circleData;

    @FXML
    private TextField rectangleData;

    // Shows user how to draw shapes
    @FXML
    private Label help;

    // User enters size of brush
    @FXML
    private TextField brush;

    // Choose brush color
    @FXML
    private ColorPicker colorPicker;

    @FXML
    private CheckBox eraser;

    private Client client;
    private Thread clientThread;
    public static GraphicsContext brushedShape;

    //Counts for the number of circles and rectangles drawn and colors used
    public int circleCount = 0;
    public int rectangleCount = 0;
    public int colorCount = 0;
    //Arraylist of colors used
    ArrayList<String> colors = new ArrayList<String>();

    // User adds circle dimensions and color to textfield
    // data is used to draw a circle
    @FXML
    protected void onMakeCircleButtonClick()
    {
        drawCircleRec(circleData, "circle");

        // someone else does data sent to server, server then draws shape and prints it to screen
        client.writeToServer("circle" + " " + circleData.getText());
    }

    // Draw circle or rectangle
    public GraphicsContext drawCircleRec(TextField shapeData, String circleOrRec)
    {

        // Put shapes dimensions into seperate places in a list
        String shapeString = shapeData.getText();
        String [] shapeList = shapeString.split(" ");
        GraphicsContext shape = canvas.getGraphicsContext2D();

        //If the color is not present in the colors Arraylist add it and increase the color count
        if(!colors.contains(shapeList[4])){
            colorCount++;
            colors.add(shapeList[4]);
        }
        else{
            colorCount+=0;
        }

        // String of color is stored in the 4th index, convert to a Color object
        Color shapeColor = Color.web(shapeList[4]);
        shape.setFill(shapeColor);

        // When shape is circle, fill oval, otherwise fill rect
        if (circleOrRec.equals("circle"))
        {
            // String of dimensions are stored in 0-3 indexes
            shape.fillOval(Double.parseDouble(shapeList[0]),Double.parseDouble(shapeList[1]),
                    Double.parseDouble(shapeList[2]), Double.parseDouble(shapeList[3]));

            circleCount++;
        }
        else
        {
            shape.fillRect(Double.parseDouble(shapeList[0]),Double.parseDouble(shapeList[1]),
                    Double.parseDouble(shapeList[2]), Double.parseDouble(shapeList[3]));

            rectangleCount++;
        }

        //write the counting stats to a csv file called stats.txt and overwrite any prior stats
        try{
            FileWriter fw = new FileWriter("stats.txt");
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);

            pw.println("Rectangles Drawn"+","+rectangleCount);
            pw.println("Circles Drawn"+","+circleCount);
            pw.println("Colors Used"+","+colorCount);
            pw.flush();
            pw.close();
        }
        catch(Exception E){
        }

        return shape;
    }

    // User adds circle dimensions and color to textfield
    // data is used to draw a rectangle
    @FXML
    protected void onMakeRectangleButtonClick()
    {
        drawCircleRec(rectangleData, "rectangle");
        client.writeToServer("rectangle" + " " + rectangleData.getText());
    }

    // Hide the help label
    @FXML
    protected void onHideHelpButtonClick()
    {
        help.setVisible(false);
    }

    // Show the help label
    @FXML
    protected void onShowHelpButtonClick()
    {
        help.setVisible(true);
    }

    // Here we should disconnect from server, then exit button just closes platform
    @FXML
    protected void onBackHomeButtonClick() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("home-view.fxml"));
        Stage window = (Stage)backHome.getScene().getWindow();
        window.setScene(new Scene(root,600,400));
    }

    public void initialize(URL location, ResourceBundle resources)
    {
        // Add a brush to draw whatever user wants, and eraser
        // to erase designs
        brushedShape = canvas.getGraphicsContext2D();

        // Create the client
        try {
            client = new Client(new Socket("localhost", 6666));
            clientThread = new Thread(client);
            clientThread.start();
        } catch (IOException e) {
            System.out.println("cannot connect to server");
        }
        // Use mouse for a brush
        canvas.setOnMouseDragged(e ->
        {
            double size = Double.parseDouble(brush.getText());
            double x = e.getX() - size / 2;
            double y = e.getY() - size / 2;

            if (eraser.isSelected())
            {
                brushedShape.clearRect(x,y,size,size);
            }
            else
            {
                brushedShape.setFill(colorPicker.getValue());
                brushedShape.fillRect(x,y,size,size);
            }
            // create a comma seperated string of parameters that will be used to write to
            // the other client
            String parameters = String.join(",", Double.toString(x),
                    Double.toString(y),Double.toString(size),Boolean.toString(eraser.isSelected()),
                    colorPicker.getValue().toString());
            client.writeToServer(parameters);
        });

    }
    public static void brushDraw(GraphicsContext brushedShape, double x, double y, double size, boolean isErase, Paint paint) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (isErase) {
                    brushedShape.clearRect(x,y,size,size);
                }
                else {
                    brushedShape.setFill(paint);
                    brushedShape.fillRect(x,y,size,size);
                }
            }
        });
    }

    public static void shapeDraw(String data) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                // Put shapes dimensions into seperate places in a list
                String [] shapeList = data.split(" ");
                // String of color is stored in the 4th index, convert to a Color object
                Color shapeColor = Color.web(shapeList[5]);
                brushedShape.setFill(shapeColor);

                // When shape is circle, fill oval, otherwise fill rect
                if (shapeList[0].equals("circle"))
                {
                    // String of dimensions are stored in 0-3 indexes
                    brushedShape.fillOval(Double.parseDouble(shapeList[1]),Double.parseDouble(shapeList[2]),
                            Double.parseDouble(shapeList[3]), Double.parseDouble(shapeList[4]));
                }
                else
                {
                    brushedShape.fillRect(Double.parseDouble(shapeList[1]),Double.parseDouble(shapeList[2]),
                            Double.parseDouble(shapeList[3]), Double.parseDouble(shapeList[4]));
                }
            }
        });
    }
}
