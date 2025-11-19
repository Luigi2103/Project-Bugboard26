module com.example.projectbugboard26 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.projectbugboard26 to javafx.fxml;
    exports com.example.projectbugboard26;
}