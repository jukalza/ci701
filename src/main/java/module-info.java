module ci701.hellojavafx {
    requires javafx.controls;
    requires javafx.fxml;


    opens ci701.hellojavafx to javafx.fxml;
    exports ci701.hellojavafx;
}