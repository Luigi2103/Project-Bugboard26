module it.unina.bugboard {
    exports it.unina.bugboard.app to javafx.graphics;

    opens it.unina.bugboard.app to javafx.fxml;

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires de.jensd.fx.glyphs.fontawesome;

    exports it.unina.bugboard.login;

    opens it.unina.bugboard.login to javafx.fxml, com.fasterxml.jackson.databind;

    exports it.unina.bugboard.login.exception;

    opens it.unina.bugboard.inserimentoutente to javafx.fxml;
    opens it.unina.bugboard.recovery to javafx.fxml;

    opens it.unina.bugboard.fxml;
    opens it.unina.bugboard.foto;
}
