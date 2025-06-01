module com.example.gry {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.gry to javafx.fxml;
    exports com.example.gry;
}