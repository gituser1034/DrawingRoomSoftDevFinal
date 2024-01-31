package com.example.drawingroomfinal;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController
{
    @FXML
    private Button enterButton;

    @FXML
    private Button Table;

    @FXML
    protected void onEnterButtonClick() throws IOException
    {
        // Switch Scenes here
        Parent root = FXMLLoader.load(getClass().getResource("room-view.fxml"));
        Stage window = (Stage)enterButton.getScene().getWindow();
        window.setScene(new Scene(root,600,400));
        // Also add functionality for connecting to server
    }

    @FXML
    protected void onTableButtonClick() throws IOException
    {
        // Switch Scenes here
        Parent root = FXMLLoader.load(getClass().getResource("table-view.fxml"));
        Stage window = (Stage)Table.getScene().getWindow();
        window.setScene(new Scene(root,600,400));
        // Also add functionality for connecting to server
    }

    // Here just closes application
    // Go back home button from room disconnects the server
    @FXML
    protected void onExitButtonClick()
    {
        Platform.exit();
    }
}