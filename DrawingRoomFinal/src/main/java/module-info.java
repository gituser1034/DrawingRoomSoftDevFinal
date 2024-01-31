module com.example.drawingroomfinal {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens com.example.drawingroomfinal to javafx.fxml;
    exports com.example.drawingroomfinal;
}