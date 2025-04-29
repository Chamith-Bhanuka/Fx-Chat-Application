package lk.ijse.mychat.mychatfxchatapplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private ChatServer server;
    private BufferedReader in;
    private PrintWriter out;
    private String username = "Guest";

    public ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        try {
            String loginMessage = in.readLine();

            if (loginMessage != null && loginMessage.startsWith("LOGIN:")) {
                username = loginMessage.substring(6);
                server.broadcast("Server: " + username + " joined the chat", this);
            }

            String message;

            while ((message = in.readLine()) != null) {
                System.out.println(username + " : " + message);
                server.broadcast(username + " : " + message, this);
            }


        } catch (IOException e) {
            System.out.println("Connection lost with " + username);
            throw new RuntimeException(e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }
}
