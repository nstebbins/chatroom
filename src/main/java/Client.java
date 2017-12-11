import constants.ChatroomConstants;
import greeting.ChatroomClientGreeting;
import greeting.ClientGreeting;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client {

    private PrintWriter outToServer;
    private BufferedReader inFromServer;
    private BufferedReader inFromUser;
    private ClientGreeting clientGreeting;
    private AtomicBoolean doneSending;

    private Client(Socket clientSocket) {
        try {
            this.outToServer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
            this.inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            System.err.println("error creating client reader and writer");
        }
        this.inFromUser = new BufferedReader(new InputStreamReader(System.in));
        this.clientGreeting = new ChatroomClientGreeting();
        this.doneSending = new AtomicBoolean();
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
                    if(sw.toString().contains("java.net.SocketException: Socket closed")) {
                        break;
                    } else {
                        System.err.println("error reading in user input");
                    }
                }
                if(!doneSending.get()) {
                    System.out.println("[server] " + message);
                } else {
                    break; // safely exit if done sending
                }
            }
        }
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
        String username = client.clientGreeting.greet(client.inFromUser, client.inFromServer, client.outToServer);
        // TODO: add logic here for handling null username (and possibly server-side)
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
}