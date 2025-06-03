package lk.ijse.mychat.mychatfxchatapplication;

import lk.ijse.mychat.mychatfxchatapplication.util.EncryptionUtil;

import java.io.*;
import java.net.Socket;

public class ChatClient {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private MessageListener listener;
    private String username;

    public interface MessageListener {
        void onMessageReceived(String message);
    }

    public ChatClient(String serverAddress, int port, MessageListener listener, String username) {
        this.listener = listener;
        this.username = username;

        try {
            socket = new Socket(serverAddress, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println("LOGIN:" + username);

            new Thread(new IncomingReader()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
//            String encrypted = EncryptionUtil.encrypt(message);
//            out.println(encrypted);
            out.println(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class IncomingReader implements Runnable {
        @Override
        public void run() {
            String message;
            try {
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("ENCRYPTED:")) {
                        // Format: ENCRYPTED:username:encryptedText
                        String[] parts = message.split(":", 3);
                        if (parts.length == 3) {
                            String sender = parts[1];
                            String encryptedText = parts[2];

                            try {
                                String decryptedText = EncryptionUtil.decrypt(encryptedText);
                                listener.onMessageReceived(sender + " : " + decryptedText);
                            } catch (Exception e) {
                                System.err.println("Failed to decrypt message: " + message);
                                e.printStackTrace();
                            }
                        }
                    } else {
                        // Fallback if message isn't encrypted
                        listener.onMessageReceived(message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public void closeConnection() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
