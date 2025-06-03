module com.example.gry {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.gry to javafx.fxml;
    exports com.example.gry;
    exports com.example.gry.go.gui.controllers;
    opens com.example.gry.go.gui.controllers to javafx.fxml;
}