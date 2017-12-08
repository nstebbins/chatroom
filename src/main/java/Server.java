import Constants.ChatroomConstants;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
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
    }

    class ClientThread implements Runnable {

        private Socket clientSocket;
        private BufferedReader inFromClient;
        private PrintWriter outToClient;

        // TODO: add try/catch
        public ClientThread(Socket clientSocket) throws Exception {
            this.clientSocket = clientSocket;
            this.inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.outToClient = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
        }

        public boolean greeting() throws Exception {
            int numAttempts = 0;
            boolean isValidated = false;
            String username = null;
            while (!isValidated && numAttempts < 3) {
                // username
                outToClient.println("username: ");
                username = inFromClient.readLine();
                // password
                outToClient.println("password: ");
                String password = inFromClient.readLine();
                // auth
                // TODO: also check if user is not already online
                isValidated = credentials.contains(new Credential(username, password));
                if (isValidated) {
                    outToClient.println(ChatroomConstants.OK);
                    // TODO: add to all necessary structures
                    userSockets.putIfAbsent(username, clientSocket);
                } else {
                    outToClient.println(ChatroomConstants.FAIL);
                }
                numAttempts++;
            }
            return false; // TODO: implement
        }

        @Override
        public void run() {
            try {
                greeting();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static List<Credential> readCredentials(String file) {
        List<Credential> credentials = new ArrayList<>();
        // TODO: read file from the resources folder
        try {
            BufferedReader inFromFile = new BufferedReader(new FileReader(file));
            while (inFromFile.ready()) {
                String[] credentialList = inFromFile.readLine().split(" ");
                credentials.add(new Credential(credentialList[0], credentialList[1]));

            }
            inFromFile.close();
        } catch (Exception e) {
            e.printStackTrace(); // TODO: better logging
        }
        System.out.println("number of credentials read in: " + credentials.size());
        return credentials;
    }

    public static void main(String args[]) throws Exception {
        int port = 4000;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        ServerSocket serverSocket = new ServerSocket(port);
        Server newServer = new Server(readCredentials("/Users/nstebbins/Documents/dev/chatroom/src/main/resources/user_pass.txt"));
        // accept clients
        while (true) {
            Socket clientSocket = serverSocket.accept();
            Thread clientThread = new Thread(newServer.new ClientThread(clientSocket));
            clientThread.start();
        }
    }
}
