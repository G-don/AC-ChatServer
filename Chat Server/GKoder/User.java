package GKoder;

import java.io.*;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class User {

    private Socket socket;
    private BufferedWriter bufferedWriter;
    private static BufferedReader bufferedReader;
    private static String username;
    private static final int PORT = 55555;

    public static void main(String[] args) {
        try {
            BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter your username for MIRC: ");
            String username = scanner.readLine();

            Socket socket = new Socket("localhost", PORT);
            User user = new User(socket, username);
            user.listenForMessage();
            user.sendMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            User.username = username;
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }

    }

    public void sendMessage() {
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));
            while (socket.isConnected()) {

                String messageToSend = scanner.readLine();
                if (messageToSend.startsWith("/username ")) {
                    String newUsername = messageToSend.substring("/username ".length());
                    changeUsername(newUsername);
                }
                else if (messageToSend.startsWith("quit")){
                    endChat();
                }else {
                    bufferedWriter.write(username + ": " + messageToSend);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }
            }


        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }

    }

    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String messageFromMirc;

                while (socket.isConnected()) {
                    try {
                        messageFromMirc = bufferedReader.readLine();

                        System.out.println(messageFromMirc);
                    } catch (IOException e) {
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }

                }
            }
        }).start();
    }


    public static void setUsername(String username) {
        User.username = username;
    }
    public void changeUsername(String newUsername) {
        try {
            bufferedWriter.write("SERVER: " + username.toUpperCase() + " changed name to " + newUsername);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            username = newUsername;
            System.out.println("Your username has been changed to " + newUsername);

        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void endChat() throws IOException {
        bufferedWriter.write("SERVER: " + username.toUpperCase() + " has left the chat!!");
        bufferedWriter.newLine();
        bufferedWriter.flush();
        socket.close();
        System.exit(0);

    }

    private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {

        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}