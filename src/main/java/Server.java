import commands.Broadcast;
import commands.CommandNotFound;
import commands.DirectMessage;
import commands.WhoElse;
import greeting.ChatroomServerGreeting;
import greeting.ServerGreeting;
import objects.ClientMessage;
import objects.Credential;
import util.ArrayUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

// TODO: prevent multiple people of same user from authenticating in
// TODO: some kind of graceful check that message queue has valid users for messages
// TODO: get available users should only be users at current moment; thus remove users when they logout (handle outToClients)
public class Server {

    private List<Credential> credentials;
    private Map<String, Queue<String>> messageQueue;
    private Map<String, PrintWriter> outToClients;
    private ServerGreeting serverGreeting;

    private Server(List<Credential> credentials) {
        this.credentials = credentials;
        this.messageQueue = new ConcurrentHashMap<>();
        this.outToClients = new ConcurrentHashMap<>();
        for (Credential credential : this.credentials) {
            this.messageQueue.put(credential.getUsername(), new ConcurrentLinkedQueue<>());
        }
        this.serverGreeting = new ChatroomServerGreeting();
    }

    // helper methods
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

        private Socket clientSocket;
        private BufferedReader inFromClient;
        private PrintWriter outToClient;
        private String username;
        // commands
        private WhoElse whoElse;
        private DirectMessage directMessage;
        private Broadcast broadcast;
        private CommandNotFound commandNotFound;

        private ClientThread(Socket clientSocket) {
            this.clientSocket = clientSocket;
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
        }

        @Override
        public void run() {
            this.username = serverGreeting.greet(inFromClient, outToClient, credentials);
            if (this.username != null) {
                outToClients.put(username, outToClient);
                Thread receive = new Thread(this.new ReceivingThread());
                receive.start();
            }
        }

        private class ReceivingThread implements Runnable {
            public void run() {
                do {
                    messageQueue.get(username).add("enter a command: ");
                    try {
                        String message = inFromClient.readLine();
                        // process message
                        String[] command = message.split(" ");
                        List<ClientMessage> clientMessages = new ArrayList<>();
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
                        for (ClientMessage clientMessage : clientMessages) {
                            messageQueue.get(clientMessage.getUsername()).add(clientMessage.getMessage());
                        }
                    } catch (IOException e) {
                        System.err.println("error reading in client input");
                    } catch (NullPointerException e) {
                        System.out.println("client left the platform");
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
