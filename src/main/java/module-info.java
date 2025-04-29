module lk.ijse.mychat.mychatfxchatapplication {
    requires javafx.controls;
    requires javafx.fxml;


    opens lk.ijse.mychat.mychatfxchatapplication to javafx.fxml;
    exports lk.ijse.mychat.mychatfxchatapplication;
    exports lk.ijse.mychat.mychatfxchatapplication.controller;
    opens lk.ijse.mychat.mychatfxchatapplication.controller to javafx.fxml;
}