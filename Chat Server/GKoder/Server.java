package GKoder;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {


    private ServerSocket serverSocket;
    private static final int PORT = 55555;
    private User user;

    public static void main(String[] args) {

        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            Server server = new Server(serverSocket);
            server.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {

        try {
            System.out.println("Listening for user request at port " + PORT);
            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();

                System.out.println("User connected: " + clientSocket);
                ClientHandler clientHandler = new ClientHandler(clientSocket);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            System.err.println("Unable to establish connection");
        }
    }
    public void closeServerSocket() {
        try {
            if(serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
