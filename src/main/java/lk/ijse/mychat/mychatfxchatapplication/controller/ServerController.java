package lk.ijse.mychat.mychatfxchatapplication.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lk.ijse.mychat.mychatfxchatapplication.ChatServer;
import lk.ijse.mychat.mychatfxchatapplication.util.ChatWindowUtil;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ServerController implements Initializable {


    private ToggleGroup myToggleGroup;

    @FXML
    private Button btnNextUser;

    @FXML
    private Button btnStartChat;

    @FXML
    private Label lblUserCount;

    @FXML
    private RadioButton radioGroup;

    @FXML
    private RadioButton radioPrivate;

    @FXML
    private Spinner<Integer> spinnerUserCount;

    @FXML
    private TextField txtUserName;

    int min = 0;
    int max = 0;
    int initial = 2;

    List<String> userNames = new ArrayList<>();

    private int userEntryIndex = 0;

    private ChatServer server;



    @FXML
    void onNextUserClick(ActionEvent event) {
        String userName = txtUserName.getText().trim();

        if (!userName.isEmpty()) {
            userNames.add(userName);
            userEntryIndex++;
            txtUserName.clear();

            int totalUsers = spinnerUserCount.getValue();

            if (userEntryIndex < totalUsers - 1) {
                lblUserCount.setText("Enter User " + (userEntryIndex + 1));
                btnNextUser.setVisible(true);
                btnStartChat.setVisible(false);
            } else if (userEntryIndex == totalUsers - 1) {
                lblUserCount.setText("Enter User " + (userEntryIndex + 1));
                btnNextUser.setVisible(false);
                btnStartChat.setVisible(true);
            }
        } else {
            lblUserCount.setText("Please enter a name!");
        }
    }

    private void resetUserEntry() {
        userNames.clear();
        userEntryIndex = 0;
        txtUserName.clear();
        lblUserCount.setText("Enter User 1");
        btnNextUser.setVisible(true);
        btnStartChat.setVisible(false);
    }

    @FXML
    void onStartChatClick(ActionEvent event) throws IOException {
        String lastUserName = txtUserName.getText().trim();

        if (!lastUserName.isEmpty()) {
            userNames.add(lastUserName);
        } else {
            new Alert(Alert.AlertType.ERROR, "Please enter a last user's name!", ButtonType.OK).show();
            return;
        }
        System.out.println("User list: " + userNames);

        new Thread(() -> {
            server = new ChatServer(8888);
        }).start();

        Platform.runLater(() -> {
            for (String name : userNames) {
                try {
                    ChatWindowUtil.launch(name);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnNextUser.setVisible(true);
        btnStartChat.setVisible(false);
        setRadioButtons();
    }

    private void setRadioButtons() {
        myToggleGroup = new ToggleGroup();
        radioGroup.setToggleGroup(myToggleGroup);
        radioPrivate.setToggleGroup(myToggleGroup);

        myToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                RadioButton selected = (RadioButton) newToggle;
                String selectedText = selected.getText();
                System.out.println("Selected: " + selectedText);

                if (selected == radioGroup) {
                    doGroupChat();
                } else if (selected == radioPrivate) {
                    doPrivateChat();
                }
            }
        });
    }

    private void doPrivateChat() {
        System.out.println("doPrivateChat");

        min =2;
        max = 2;

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, initial);
        spinnerUserCount.setValueFactory(valueFactory);

        resetUserEntry();

    }

    private void doGroupChat() {
        System.out.println("doGroupChat");

        min = 3;
        max = 5;

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, initial);
        spinnerUserCount.setValueFactory(valueFactory);

        resetUserEntry();
    }
}