package GKoder;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket clientSocket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public ClientHandler(Socket clientSocket) throws IOException {
        try {
            this.clientSocket = clientSocket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.username = bufferedReader.readLine();
            clientHandlers.add(this);
            broadcastMessage("SERVER: " + username + " has entered the chat!");

        } catch (IOException e) {
            closeEverything(clientSocket, bufferedReader, bufferedWriter);
        }

    }

    @Override
    public void run() {
        String messageFromClient;

        while(clientSocket.isConnected()) {

            try {
                messageFromClient = bufferedReader.readLine();
                broadcastMessage(messageFromClient);
            } catch (IOException e) {
                try {
                    closeEverything(clientSocket, bufferedReader, bufferedWriter);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                break;
            }
        }

    }

    private void broadcastMessage(String messageToSend) throws IOException {

        for (ClientHandler worker : clientHandlers) {
            try {

                if (!worker.username.equals(username)) {
                    worker.bufferedWriter.write(messageToSend);
                    worker.bufferedWriter.newLine();
                    worker.bufferedWriter.flush();
            }
            } catch (IOException e) {
                closeEverything(clientSocket, bufferedReader, bufferedWriter);
            }
        }
    }
    public void removeClientHandler() throws IOException {

            clientHandlers.remove(this);
            broadcastMessage("SERVER: " + username + " has left the Chat!");
        }


    private void closeEverything(Socket clientSocket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) throws IOException {
        removeClientHandler();
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
               bufferedWriter.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
