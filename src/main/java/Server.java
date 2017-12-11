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

    private class SendingThread implements Runnable {
        public void run() {
            // TODO: sending based off of the message queue
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

        private ClientThread(Socket clientSocket) {
            this.clientSocket = clientSocket;
            try {
                this.inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                this.outToClient = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
            } catch (IOException e) {
                System.err.println("error creating client reader and writer");
            }
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
                // TODO: build the dedicated receiver
                // TODO: handle logout
                String message = "";
                do {
                    messageQueue.get(username).add("enter a command: ");
                    try {
                        message = inFromClient.readLine();
                    } catch (IOException e) {
                        System.err.println("error reading in client input");
                    }
                } while (message != null);
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
