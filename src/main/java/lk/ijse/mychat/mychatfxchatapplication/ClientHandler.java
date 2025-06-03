package lk.ijse.mychat.mychatfxchatapplication;

import lk.ijse.mychat.mychatfxchatapplication.util.EncryptionUtil;

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
                String loginNotice = "Server: " + username + " joined the chat";
                server.broadcast("ENCRYPTED:Server:" + EncryptionUtil.encrypt(loginNotice), this);
            }

            String message;

            while ((message = in.readLine()) != null) {
                String encrypted = EncryptionUtil.encrypt(message);
                System.out.println(username + ": (RAW) - " + message + "  <--> (Encrypted) - " + encrypted);
                server.broadcast("ENCRYPTED:" + username + ":" + encrypted, this);
            }


        } catch (IOException e) {
            System.out.println("Connection lost with " + username);
        } catch (Exception e) {
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
