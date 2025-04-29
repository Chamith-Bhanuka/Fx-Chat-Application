package lk.ijse.mychat.mychatfxchatapplication;

import javafx.scene.image.Image;

public class ChatMessage {
    private String sender;
    private String content;        // For text messages
    private boolean isFile;        // True if the message represents a file
    private String filename;       // The name of the received file
    private byte[] fileData;       // Raw file bytes
    private String currentUser;    // The current user for alignment purposes

    // Constructor for text messages
    public ChatMessage(String sender, String content, String currentUser) {
        this.sender = sender;
        this.content = content;
        this.currentUser = currentUser;
        this.isFile = false;
    }

    // Constructor for file messages
    public ChatMessage(String sender, String filename, byte[] fileData, String currentUser) {
        this.sender = sender;
        this.filename = filename;
        this.fileData = fileData;
        this.currentUser = currentUser;
        this.isFile = true;
        // You can set content as a placeholder if desired
        this.content = "File received: " + filename;
    }

    // Getters
    public String getSender() { return sender; }
    public String getContent() { return content; }
    public boolean isFile() { return isFile; }
    public String getFilename() { return filename; }
    public byte[] getFileData() { return fileData; }
    public String getCurrentUser() { return currentUser; }
}
