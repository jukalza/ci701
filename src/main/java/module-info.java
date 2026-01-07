module ci701.hellojavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens ci701.hellojavafx to javafx.fxml;
    exports ci701.hellojavafx;
}