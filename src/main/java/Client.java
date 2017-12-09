import constants.ChatroomConstants;
import greeting.ChatroomClientGreeting;
import greeting.ClientGreeting;

import java.io.*;
import java.net.Socket;

public class Client {

    private PrintWriter outToServer;
    private BufferedReader inFromServer;
    private BufferedReader inFromUser;
    private ClientGreeting clientGreeting;

    private Client(Socket clientSocket) {
        try {
            this.outToServer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
            this.inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace(); // TODO: better logging
        }
        this.inFromUser = new BufferedReader(new InputStreamReader(System.in));
        this.clientGreeting = new ChatroomClientGreeting();
    }

    private class SendingThread implements Runnable {
        public void run() {
            try {
                while (true) {
                    String message = inFromUser.readLine();
                    outToServer.println(message);
                    if (message.equals(ChatroomConstants.LOGOUT)) {
                        System.out.println("logout triggered client side");
                        break;
                    }
                }
            } catch (IOException e) {
                System.err.println("error reading in user input");
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
        System.out.println("username: " + username);
        // start threads
        Thread send = new Thread(client.new SendingThread());
        send.start();
        // clean-up
        send.join();
        clientSocket.close();
    }
}