package lk.ijse.mychat.mychatfxchatapplication.controller;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import lk.ijse.mychat.mychatfxchatapplication.ChatClient;
import lk.ijse.mychat.mychatfxchatapplication.ChatMessage;
import lk.ijse.mychat.mychatfxchatapplication.MessageCell;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;
import java.util.ResourceBundle;

public class ChatController implements ChatClient.MessageListener, Initializable {

    @FXML
    private ListView<ChatMessage> chatList;

    @FXML
    private TextField messageField;

    @FXML
    private Button sendButton;

    @FXML
    private ImageView fileButton;


    private ChatClient client;
    private String username;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setClient(ChatClient client) {
        this.client = client;
    }


    @Override
    public void onMessageReceived(String receivedMessage) {
        // Check if the received message contains our file transfer marker "FILE:"
        if (receivedMessage.contains("FILE:")) {
            // Assume the format is: "sender: FILE:filename:filesize:base64Data"
            int fileIndex = receivedMessage.indexOf("FILE:");
            String sender = "";
            String fileMessagePart;
            if (fileIndex > 0) {
                sender = receivedMessage.substring(0, fileIndex).replace(":", "").trim();
                fileMessagePart = receivedMessage.substring(fileIndex);
            } else {
                sender = "SERVER";
                fileMessagePart = receivedMessage;
            }
            // Split the file part into 4 parts: "FILE", filename, filesize, base64Data
            String[] parts = fileMessagePart.split(":", 4);
            if (parts.length == 4) {
                String filename = parts[1].trim();
                // You may parse filesize if desired: int filesize = Integer.parseInt(parts[2].trim());
                String base64Data = parts[3].trim();
                byte[] fileData = java.util.Base64.getDecoder().decode(base64Data);

                // Use a final variable for sender, so it can be used in the lambda.
                final String finalSender = sender.isEmpty() ? "SERVER" : sender;
                Platform.runLater(() -> {
                    try {
                        // Create a ChatMessage for a file message.
                        ChatMessage fileMsg = new ChatMessage(finalSender, filename, fileData, username);
                        chatList.getItems().add(fileMsg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } else {
                System.out.println("Invalid file message format received: " + receivedMessage);
            }
        } else {
            // Process as a regular text message.
            // Expected format: "sender: message"
            String[] parts = receivedMessage.split(":", 2);
            if (parts.length >= 2) {
                String sender = parts[0].trim();
                String text = parts[1].trim();
                ChatMessage chatTextMessage = new ChatMessage(sender, text, username);
                Platform.runLater(() -> chatList.getItems().add(chatTextMessage));
            } else {
                System.out.println("Invalid text message format received: " + receivedMessage);
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chatList.setCellFactory(list -> new MessageCell());
        sendButton.setOnAction(e -> sendMessage());

        fileButton.setOnMouseClicked(e -> sendFile());

        chatList.getItems().addListener((ListChangeListener<ChatMessage>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    // Schedule the scroll on the JavaFX Application Thread
                    Platform.runLater(() -> {
                        chatList.scrollTo(chatList.getItems().size() - 1);
                    });
                }
            }
        });

    }

    @FXML
    public void sendMessage() {
        String text = messageField.getText();

        if (text != null && !text.trim().isEmpty()) {
            ChatMessage myMessage = new ChatMessage(username, text, username);
//            chatList.getItems().add(myMessage);

            Platform.runLater(() -> {
                chatList.getItems().add(myMessage);
                chatList.scrollTo(chatList.getItems().size() - 1);
            });

            client.sendMessage(text);
            messageField.clear();
        }
    }

    public void sendFile() {
        FileChooser fileChooser = new FileChooser();
        // Allow standard file types – you can add more as needed.
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showOpenDialog(fileButton.getScene().getWindow());

        if (selectedFile != null) {
            try {
                // Read the file bytes
                byte[] fileBytes = java.nio.file.Files.readAllBytes(selectedFile.toPath());
                // Encode the file to Base64 (for sending over a text-based protocol)
                String base64Data = java.util.Base64.getEncoder().encodeToString(fileBytes);
                // Build a header. We use a fixed format: "FILE:<filename>:<filesize>:<base64Data>"
                String header = "FILE:" + selectedFile.getName() + ":" + fileBytes.length + ":";
                String fileMessage = header + base64Data;

                // Send the complete file message
                client.sendMessage(fileMessage);

                // Immediately update sender's UI with a ChatMessage representing the file sent.
                ChatMessage selfFileMessage = new ChatMessage(username, selectedFile.getName(), fileBytes, username);
                chatList.getItems().add(selfFileMessage);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
