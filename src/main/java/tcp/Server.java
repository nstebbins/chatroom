package tcp;

import commands.*;
import constants.ChatroomConstants;
import objects.ClientMessage;
import objects.Credential;
import util.ArrayUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

// TODO: add doc strings
public class Server {

    private List<Credential> credentials;
    private Map<String, Queue<String>> messageQueue;
    private Map<String, PrintWriter> outToClients;

    private Server(List<Credential> credentials) {
        this.credentials = credentials;
        this.messageQueue = new ConcurrentHashMap<>();
        this.outToClients = new ConcurrentHashMap<>();
        for (Credential credential : this.credentials) {
            this.messageQueue.put(credential.getUsername(), new ConcurrentLinkedQueue<>());
        }
    }

    // exposed helper method
    public synchronized ConcurrentLinkedQueue<String> getAvailableUsers() {
        return new ConcurrentLinkedQueue<>(this.outToClients.keySet());
    }

    private class SendingThread implements Runnable {
        public void run() {
            while (true) {
                for (Map.Entry<String, Queue<String>> e : messageQueue.entrySet()) {
                    Queue<String> messages = e.getValue();
                    while (!messages.isEmpty()) {
                        PrintWriter outToClient = outToClients.get(e.getKey());
                        outToClient.println(messages.poll());
                    }
                }
            }
        }
    }

    private class ClientThread implements Runnable {

        private BufferedReader inFromClient;
        private PrintWriter outToClient;
        private String username;
        // commands
        private WhoElse whoElse;
        private DirectMessage directMessage;
        private Broadcast broadcast;
        private CommandNotFound commandNotFound;
        private UserNotFound userNotFound;

        private ClientThread(Socket clientSocket) {
            try {
                this.inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                this.outToClient = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
            } catch (IOException e) {
                System.err.println("error creating client reader and writer");
            }
            // commands
            this.whoElse = new WhoElse();
            this.directMessage = new DirectMessage();
            this.broadcast = new Broadcast();
            this.commandNotFound = new CommandNotFound();
            this.userNotFound = new UserNotFound();
        }

        @Override
        public void run() {
            // greet
            ClientGreeting clientGreeting = this.new ClientGreeting();
            this.username = clientGreeting.greet();
            // chatroom
            if (this.username != null) {
                outToClients.put(username, outToClient);
                Thread receive = new Thread(this.new ReceivingThread());
                receive.start();
            }
        }

        private class ClientGreeting {
            /**
             * server-side authentication
             * @return username if authenticated successfully, null otherwise
             */
            public String greet() {
                String username = null;
                boolean authenticated = false;
                try {
                    int attempts = 0;
                    do {
                        // username
                        outToClient.println("username: ");
                        username = inFromClient.readLine();
                        // password
                        outToClient.println("password: ");
                        String password = inFromClient.readLine();
                        // auth
                        if (credentials.contains(new Credential(username, password)) && !getAvailableUsers().contains(username)) {
                            outToClient.println(ChatroomConstants.OK);
                            authenticated = true;
                            break;
                        } else {
                            outToClient.println(ChatroomConstants.FAIL);
                        }
                        attempts++;
                    } while (attempts < 3);
                } catch (IOException e) {
                    System.err.println("error reading in client input");
                    e.printStackTrace();
                }
                return authenticated ? username : null;
            }
        }

        // TODO: add 'help' command
        private class ReceivingThread implements Runnable {
            public void run() {
                do {
                    messageQueue.get(username).add("enter a command: ");
                    try {
                        String message = inFromClient.readLine();
                        // process message
                        String[] command = message.split(" ");
                        List<ClientMessage> clientMessages;
                        // command parsing
                        switch (command[0]) {
                            case "whoelse":
                                clientMessages = whoElse.execute(username, getAvailableUsers());
                                break;
                            case "message":
                                clientMessages = directMessage.execute(username, command[1], ArrayUtil.joinArraySubsetBySpace(command, 2));
                                break;
                            case "broadcast":
                                clientMessages = broadcast.execute(username, getAvailableUsers(), ArrayUtil.joinArraySubsetBySpace(command, 1));
                                break;
                            default:
                                clientMessages = commandNotFound.execute(username);
                                break;
                        }
                        // add client messages to message queue
                        for (int i = 0; i < clientMessages.size(); i++) {
                            ClientMessage clientMessage = clientMessages.get(i);
                            if (getAvailableUsers().contains(clientMessage.getUsername())) {
                                messageQueue.get(clientMessage.getUsername()).add(clientMessage.getMessage());
                            } else {
                                clientMessages.addAll(userNotFound.execute(username, clientMessage.getUsername()));
                            }
                        }
                    } catch (IOException e) {
                        System.err.println("error reading in client input");
                    } catch (NullPointerException e) {
                        System.out.println("client left the platform");
                        outToClients.remove(username);
                        break;
                    }
                } while (true);
            }
        }
    }

    public static void main(String args[]) throws Exception {
        int port = 4000;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        ServerSocket serverSocket = new ServerSocket(port);
        // TODO: modify path
        Server server = new Server(Credential.readCredentials("/Users/nstebbins/Documents/dev/chatroom/src/main/resources/user_pass.txt"));
        Thread send = new Thread(server.new SendingThread());
        send.start();
        // accept clients
        while (true) {
            Socket clientSocket = serverSocket.accept();
            Thread client = new Thread(server.new ClientThread(clientSocket));
            client.start();
        }
    }
}
