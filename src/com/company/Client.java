package com.company;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Client extends JFrame {
    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private String message = "";
    private String serverIp;
    private Socket connection;

    public Client(String host) {
        super("Client ...");
        serverIp = host;
        userText = new JTextField();
        userText.setEditable(false);
        userText.addActionListener(e -> {
                    sendMessage(e.getActionCommand());
                    userText.setText("");
                }

        );
        add(userText, BorderLayout.NORTH);
        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow), BorderLayout.CENTER);
        setSize(300, 150);
        setVisible(true);
    }

    public void startRunning() {
        try {
            connectToServer();
            setupStreams();
            whileChatting();
            showMessage("\n Client terminated connection");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            closeCrap();
        }
    }

    private void connectToServer() throws IOException {
        showMessage("Attempting connection .. \n");
        connection = new Socket(InetAddress.getByName(serverIp), 1234);
        showMessage("Connected to: " + connection.getInetAddress().getHostName());
    }

    private void setupStreams() throws IOException {
        outputStream = new ObjectOutputStream(connection.getOutputStream());
        outputStream.flush();
        inputStream = new ObjectInputStream(connection.getInputStream());
        showMessage("\n Dude your streams are now good to go! \n");
    }


    private void whileChatting() throws IOException {
        ableToType(true);
        do {
            try {
                message = (String) inputStream.readObject();
                showMessage("\n" + message);
            } catch (ClassNotFoundException classNotFoundException) {
                showMessage("\n  :( ... ");
            }
        } while (!message.equals("Server - END"));
    }

    private void closeCrap() {
        showMessage("\n closing ....");
        ableToType(false);
        try {
            outputStream.close();
            inputStream.close();
            connection.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void sendMessage(String message) {
        try {
            outputStream.writeObject("Client - " + message);
            outputStream.flush();
            showMessage("\n Client - " + message);
        } catch (IOException ioException) {
            chatWindow.append("\n Something wrong... !");
        }
    }

    private void showMessage(String s) {
        SwingUtilities.invokeLater(
                () -> chatWindow.append(s)
        );
    }

    private void ableToType(final boolean tof) {
        SwingUtilities.invokeLater(
                () -> userText.setEditable(tof)
        );
    }

}
