package lk.ijse.mychat.mychatfxchatapplication;

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

            out.println("LOGIN" + username);

            new Thread(new IncomingReader()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    private class IncomingReader implements Runnable {
        @Override
        public void run() {
            String message;

            try {
                while ((message = in.readLine()) != null) {
                    if (listener != null) {
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
