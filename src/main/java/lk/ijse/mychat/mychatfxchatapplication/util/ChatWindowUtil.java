package lk.ijse.mychat.mychatfxchatapplication.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lk.ijse.mychat.mychatfxchatapplication.ChatClient;
import lk.ijse.mychat.mychatfxchatapplication.controller.ChatController;

import java.io.IOException;

public class ChatWindowUtil {
    public static void launch(String username) throws IOException {
        FXMLLoader loader = new FXMLLoader(ChatWindowUtil.class.getResource("/view/chat-view.fxml"));
        Parent root = loader.load();

        ChatController controller = loader.getController();
        controller.setUsername(username);
        //set user name

        ChatClient client = new ChatClient("localhost", 8888, controller, username);
        controller.setClient(client);

        Stage stage = new Stage();
        stage.setTitle("Chat - " + username);
        stage.setScene(new Scene(root));
        stage.getScene().getStylesheets().add(ChatWindowUtil.class.getResource("/styles/chat-view.css").toExternalForm());

        stage.show();
    }
}
