package lk.ijse.mychat.mychatfxchatapplication;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MessageCell extends ListCell<ChatMessage> {
    @Override
    protected void updateItem(ChatMessage message, boolean empty) {
        super.updateItem(message, empty);
        if (empty || message == null) {
            setText(null);
            setGraphic(null);
        } else {
            HBox outerContainer = new HBox();
            outerContainer.setMaxWidth(400);
            outerContainer.setSpacing(5);

            if (message.getSender().equals(message.getCurrentUser())) {
                outerContainer.setAlignment(Pos.CENTER_RIGHT);
            } else {
                outerContainer.setAlignment(Pos.CENTER_LEFT);
            }

            if (message.isFile()) {
                VBox bubbleContainer = new VBox();
                bubbleContainer.setSpacing(5);
                bubbleContainer.getStyleClass().add("chat-bubble");

                if (message.getSender().equals(message.getCurrentUser())) {
                    bubbleContainer.getStyleClass().add("owner-message");
                } else {
                    bubbleContainer.getStyleClass().add("sender-message");
                }

                String lowerName = message.getFilename().toLowerCase();
                if (lowerName.endsWith(".png") || lowerName.endsWith(".jpg") ||
                        lowerName.endsWith(".jpeg") || lowerName.endsWith(".gif")) {

                    Image image = new Image(new ByteArrayInputStream(message.getFileData()));
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(200);
                    imageView.setPreserveRatio(true);
                    bubbleContainer.getChildren().add(imageView);
                } else {
                    Label fileLabel = new Label(message.getFilename());
                    fileLabel.setWrapText(true);
                    fileLabel.setMaxWidth(300);
                    bubbleContainer.getChildren().add(fileLabel);
                }

                if (!message.getSender().equals(message.getCurrentUser())) {
                    Button downloadBtn = new Button("Download");
                    downloadBtn.getStyleClass().add("download-button");
                    downloadBtn.setOnAction(e -> {
                        FileChooser fileChooser = new FileChooser();
                        fileChooser.setInitialFileName(message.getFilename());
                        File saveFile = fileChooser.showSaveDialog(outerContainer.getScene().getWindow());
                        if (saveFile != null) {
                            try (FileOutputStream fos = new FileOutputStream(saveFile)) {
                                fos.write(message.getFileData());
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                    bubbleContainer.getChildren().add(downloadBtn);
                }

                outerContainer.getChildren().add(bubbleContainer);

            } else {
                VBox textContainer = new VBox();
                textContainer.setSpacing(2);

                // ✅ Only show sender name in group chat, if it's the first in a sequence
                if (!message.getSender().equals(message.getCurrentUser()) &&
                        !message.getSender().equalsIgnoreCase("Private")) {

                    int index = getIndex();
                    String previousSender = null;
                    if (index > 0 && index < getListView().getItems().size()) {
                        ChatMessage prevMsg = getListView().getItems().get(index - 1);
                        previousSender = prevMsg.getSender();
                    }

                    if (previousSender == null || !previousSender.equals(message.getSender())) {
                        Label senderLabel = new Label(message.getSender());
                        senderLabel.getStyleClass().add("sender-name");
                        textContainer.getChildren().add(senderLabel);
                    }
                }

                Label messageLabel = new Label(message.getContent());
                messageLabel.setWrapText(true);
                messageLabel.setMaxWidth(300);
                messageLabel.getStyleClass().add("chat-bubble");

                if (message.getSender().equals(message.getCurrentUser())) {
                    messageLabel.getStyleClass().add("owner-message");
                } else {
                    messageLabel.getStyleClass().add("sender-message");
                }

                textContainer.getChildren().add(messageLabel);
                outerContainer.getChildren().add(textContainer);
            }

            setGraphic(outerContainer);
        }
    }


}
