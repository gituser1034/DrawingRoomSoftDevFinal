package com.example.drawingroomfinal;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class TableController implements Initializable {

    @FXML
    private TableView<Stats> table;

    @FXML
    private TableColumn<Stats, String> Type;

    @FXML
    private TableColumn<Stats, Integer> Count;

    @FXML
    private Button backHome;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //read data from csv file into Arraylist
        List<String> statistics = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader("stats.txt"))) {
            String line = "";
            while ((line = br.readLine()) != null) {
                String[] lineArr = line.split(",");
                for(int i = 0; i < lineArr.length; i++){
                    statistics.add(lineArr[i]);
                }
            }
        } catch(IOException e){

        }

        ObservableList<com.example.drawingroomfinal.Stats> list = FXCollections.observableArrayList(
                new Stats(statistics.get(0), statistics.get(1)),
                new Stats(statistics.get(2), statistics.get(3)),
                new Stats(statistics.get(4), statistics.get(5))

        );

        Type.setCellValueFactory(new PropertyValueFactory<Stats,String>("Type"));
        Count.setCellValueFactory(new PropertyValueFactory<Stats,Integer>("Count"));

        table.setItems(list);
    }

    // Here we should disconnect from server, then exit button just closes platform
    @FXML
    protected void onBackHomeButtonClick() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("home-view.fxml"));
        Stage window = (Stage)backHome.getScene().getWindow();
        window.setScene(new Scene(root,600,400));
    }
}
