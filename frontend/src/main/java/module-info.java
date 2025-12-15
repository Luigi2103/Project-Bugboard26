module it.unina.bugboard {
    exports it.unina.bugboard.app to javafx.graphics;

    opens it.unina.bugboard.app to javafx.fxml;

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires de.jensd.fx.glyphs.fontawesome;
    requires java.logging;
    requires org.slf4j;


    exports it.unina.bugboard.login;

    opens it.unina.bugboard.login to javafx.fxml, com.fasterxml.jackson.databind;

    exports it.unina.bugboard.login.exception;

    opens it.unina.bugboard.inserimentoutente to javafx.fxml, com.fasterxml.jackson.databind;

    opens it.unina.bugboard.fxml;
    opens it.unina.bugboard.foto;

    exports it.unina.bugboard.recovery;

    opens it.unina.bugboard.recovery to com.fasterxml.jackson.databind, javafx.fxml;
    opens it.unina.bugboard.homepage to javafx.fxml;
    opens it.unina.bugboard.inserimentoIssue to javafx.fxml;

    exports it.unina.bugboard.recovery.exception;

    opens it.unina.bugboard.dto to com.fasterxml.jackson.databind;
    opens it.unina.bugboard.issuedetails to javafx.fxml, com.fasterxml.jackson.databind;

}
