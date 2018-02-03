package tcp;

import constants.ChatroomConstants;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client {

    private PrintWriter outToServer;
    private BufferedReader inFromServer;
    private BufferedReader inFromUser;
    private AtomicBoolean doneSending;

    private Client(Socket clientSocket) {
        try {
            this.outToServer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
            this.inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            System.err.println("error creating client reader and writer");
        }
        this.inFromUser = new BufferedReader(new InputStreamReader(System.in));
        this.doneSending = new AtomicBoolean();
    }

    public static void main(String args[]) throws Exception {
        // parse program arguments
        String ip = "localhost";
        int port = 4000;
        if (args.length > 0) {
            ip = args[0];
            if (args.length > 1) {
                port = Integer.parseInt(args[1]);
            }
        }
        // initialize
        Socket clientSocket = new Socket(ip, port);
        Client client = new Client(clientSocket);
        // greet
        ClientGreeting clientGreeting = client.new ClientGreeting();
        String username = clientGreeting.greet();
        // chatroom
        if (username != null) {
            System.out.println("greeted! welcome to the chatroom, " + username + "!");
            // start threads
            Thread send = new Thread(client.new SendingThread());
            Thread receive = new Thread(client.new ReceivingThread());
            send.start();
            receive.start();
            // clean-up
            send.join();
        } else {
            System.out.println("not authenticated into chatroom after three tries. goodbye!");
        }
        clientSocket.close();
    }


    private class ClientGreeting {
        /**
         * client-side authentication
         *
         * @return username if authenticated successfully, null otherwise
         */
        public String greet() {
            String username = null;
            boolean authenticated = false;
            try {
                int attempts = 0;
                do {
                    // username
                    System.out.println("[server] " + inFromServer.readLine());
                    username = inFromUser.readLine();
                    outToServer.println(username);
                    // password
                    System.out.println("[server] " + inFromServer.readLine());
                    String password = inFromUser.readLine();
                    outToServer.println(password);
                    // auth
                    String authMessage = inFromServer.readLine();
                    System.out.println("[server] " + authMessage);
                    if (authMessage.equals(ChatroomConstants.OK)) {
                        authenticated = true;
                        break;
                    }
                    attempts++;
                } while (attempts < 3);
            } catch (IOException e) {
                System.err.println("error reading in user input");
                e.printStackTrace();
            }
            return authenticated ? username : null;
        }
    }


    private class SendingThread implements Runnable {
        public void run() {
            String message = "";
            while (true) {
                try {
                    message = inFromUser.readLine();
                } catch (IOException e) {
                    System.err.println("error reading in user input");
                }
                outToServer.println(message);
                if (message.equals(ChatroomConstants.LOGOUT)) {
                    doneSending.set(true);
                    System.out.println("logout triggered client side");
                    break;
                }
            }
        }
    }


    private class ReceivingThread implements Runnable {
        public void run() {
            String message = "";
            while (message != null) {
                try {
                    message = inFromServer.readLine();
                } catch (IOException e) {
                    // safely exit if socket is closed
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);
                    if (sw.toString().contains("java.net.SocketException: Socket closed")) {
                        break;
                    } else {
                        System.err.println("error reading in user input");
                    }
                }
                if (!doneSending.get()) {
                    System.out.println("[server] " + message);
                } else {
                    break; // safely exit if done sending
                }
            }
        }
    }
}