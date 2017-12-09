import greeting.ChatroomServerGreeting;
import greeting.ServerGreeting;
import objects.Credential;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Server {

    private List<Credential> credentials;
    private Map<String, Queue<String>> messageQueue;
    private Map<String, Queue<String>> blockedUsers;
    private Map<String, Socket> userSockets;
    private ServerGreeting serverGreeting;

    public Server(List<Credential> credentials) {
        this.credentials = credentials;
        this.messageQueue = new ConcurrentHashMap<>();
        this.blockedUsers = new ConcurrentHashMap<>();
        this.userSockets = new ConcurrentHashMap<>();
        for (Credential credential : this.credentials) {
            this.messageQueue.put(credential.getUsername(), new ConcurrentLinkedQueue<>());
            this.blockedUsers.put(credential.getUsername(), new ConcurrentLinkedQueue<>());
            this.userSockets.put(credential.getUsername(), new Socket());
        }
        this.serverGreeting = new ChatroomServerGreeting();
    }

    class ClientThread implements Runnable {

        private Socket clientSocket; // TODO: use later
        private BufferedReader inFromClient;
        private PrintWriter outToClient;

        ClientThread(Socket clientSocket) throws Exception {
            // TODO: add try/catch
            this.clientSocket = clientSocket;
            this.inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.outToClient = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
        }

        @Override
        public void run() {
            serverGreeting.greet(inFromClient, outToClient, credentials);
        }
    }

    public static void main(String args[]) throws Exception {
        int port = 4000;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        ServerSocket serverSocket = new ServerSocket(port);
        Server newServer = new Server(Credential.readCredentials("/Users/nstebbins/Documents/dev/chatroom/src/main/resources/user_pass.txt"));
        // accept clients
        while (true) {
            Socket clientSocket = serverSocket.accept();
            Thread clientThread = new Thread(newServer.new ClientThread(clientSocket));
            clientThread.start();
        }
    }
}
