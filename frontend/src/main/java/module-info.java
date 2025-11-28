module com.example.projectbugboard26 {
    exports com.example.projectbugboard26.app to javafx.graphics;

    opens com.example.projectbugboard26.app to javafx.fxml;

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    exports com.example.projectbugboard26.login;

    opens com.example.projectbugboard26.login to javafx.fxml;

    exports com.example.projectbugboard26.login.exception;

    opens com.example.projectbugboard26.inserimentoutente to javafx.fxml;
    opens com.example.projectbugboard26.recovery to javafx.fxml;
}