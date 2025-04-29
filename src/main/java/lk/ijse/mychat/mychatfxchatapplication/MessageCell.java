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
            // Outer container for the entire cell
            HBox outerContainer = new HBox();
            outerContainer.setMaxWidth(400);
            outerContainer.setSpacing(5);
            // If current user's message, align to right; otherwise to left.
            if (message.getSender().equals(message.getCurrentUser())) {
                outerContainer.setAlignment(Pos.CENTER_RIGHT);
            } else {
                outerContainer.setAlignment(Pos.CENTER_LEFT);
            }

            if (message.isFile()) {
                // Create a VBox to act as the "chat bubble" for file messages.
                VBox bubbleContainer = new VBox();
                bubbleContainer.setSpacing(5);
                // Add a common CSS class for the bubble.
                bubbleContainer.getStyleClass().add("chat-bubble");
                // Add different colors for sender/receiver bubbles.
                if (message.getSender().equals(message.getCurrentUser())) {
                    bubbleContainer.getStyleClass().add("owner-message");
                } else {
                    bubbleContainer.getStyleClass().add("sender-message");
                }

                String lowerName = message.getFilename().toLowerCase();
                // If it's an image file, display a preview.
                if (lowerName.endsWith(".png") || lowerName.endsWith(".jpg") ||
                        lowerName.endsWith(".jpeg") || lowerName.endsWith(".gif")) {

                    Image image = new Image(new ByteArrayInputStream(message.getFileData()));
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(200);
                    imageView.setPreserveRatio(true);
                    bubbleContainer.getChildren().add(imageView);
                } else {
                    // For other file types, display a label.
                    Label fileLabel = new Label(message.getFilename());
                    fileLabel.setWrapText(true);
                    fileLabel.setMaxWidth(300);
                    bubbleContainer.getChildren().add(fileLabel);
                }

                // Only for incoming file messages (receiver's side), add a Download button.
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
                // For text messages, create a label and apply the bubble style.
                Label messageLabel = new Label(message.getContent());
                messageLabel.setWrapText(true);
                messageLabel.setMaxWidth(300);
                messageLabel.getStyleClass().add("chat-bubble");
                if (message.getSender().equals(message.getCurrentUser())) {
                    messageLabel.getStyleClass().add("owner-message");
                } else {
                    messageLabel.getStyleClass().add("sender-message");
                }
                outerContainer.getChildren().add(messageLabel);
            }
            setGraphic(outerContainer);
        }
    }
}
