import java.io.*;
import java.net.Socket;

// TODO: final check over both files to make sure we have "this" where needed
// TODO: doc strings everywhere
public class Client {

    private Socket clientSocket;
    private PrintWriter outToServer;
    private BufferedReader inFromServer;
    private BufferedReader inFromUser;

    public Client(String ip, int port) {
        try {
            this.clientSocket = new Socket(ip, port);
            this.outToServer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
            this.inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace(); // TODO: better logging
        }
        this.inFromUser = new BufferedReader(new InputStreamReader(System.in));
    }

    // TODO: try/catch eventually
    private String greeting() throws Exception {
        String username = null;
        int numAttempts = 0;
        String authMessage = "";
        while (!authMessage.equals(ServerConstants.OK) && numAttempts < 3) {
            // username
            System.out.println("[server] " + inFromServer.readLine());
            username = inFromUser.readLine();
            outToServer.println(inFromUser.readLine());
            // password
            System.out.println("[server] " + inFromServer.readLine());
            String password = inFromUser.readLine();
            outToServer.println(password);
            // auth
            authMessage = inFromServer.readLine();
            System.out.println("[server] " + authMessage);
            numAttempts++;
        }
        return username;
    }

    public void menu() {
        // TODO: implement
    }

    //    public static String greeting() throws Exception {
    //        // TODO: implement
    //        // USERNAME AND PASSWORD
    //        outToServer.println("Client process connected.");
    //
    //        String successPrompt = "";
    //        String username = "";
    //        int numFails = 0;
    //
    //        System.out.print("FROM SERVER >> " + inFromServer.readLine() + " ");
    //
    //        // what if the response from the Server is the same initial prompt for username?
    //        while (successPrompt != null && !successPrompt.contains("WELCOME!") && numFails < 3) {
    //            // USERNAME
    //            username = inFromUser.readLine();
    //            outToServer.println(username);
    //
    //            // PASSWORD
    //            System.out.print("FROM SERVER >> " + inFromServer.readLine() + " ");
    //            outToServer.println(inFromUser.readLine());
    //
    //            // AUTHENTICATION RESPONSE
    //            successPrompt = inFromServer.readLine();
    //            System.out.print("FROM SERVER >> " + successPrompt + " ");
    //
    //            numFails++;
    //        }
    //
    //        // RETURN THE USERNAME IF SUCCESSFUL, NULL IF NOT
    //        if (numFails < 3) { return username; } else { return null; }
    //    }

    // (to be implemented later)
    public static final int TIME_OUT = 1000 * 60 * 30;
    public static boolean isClosed = false;
    private static Socket clientSocketFinal;
    private static String usernameFinal;

    public static void menu(PrintWriter outToServer, BufferedReader inFromServer, BufferedReader inFromUser)
        throws Exception {
        Thread send = new Thread(new SendingThread(1, outToServer, inFromUser));
        Thread receive = new Thread(new ReceivingThread(2, inFromServer));

        send.start();
        receive.start();
    }

    public static void closeSocket(Socket clientSocket) throws Exception {
        clientSocket.close();
    }


    // THREAD FOR SENDING MESSAGES
    private static class SendingThread implements Runnable {
        PrintWriter outToServer;
        BufferedReader inFromUser;
        private int threadName;

        public SendingThread(int i, PrintWriter out, BufferedReader in) {
            threadName = i;
            outToServer = out;
            inFromUser = in;
        }

        public void run() {
            try {
                long currentTime = System.currentTimeMillis();
                clientSocketFinal.setSoTimeout(TIME_OUT);
                while (true) {

                    String toSend = inFromUser.readLine();
                    outToServer.println(toSend);

                    if (toSend.equals("logout")) {
                        System.out.println("LOGOUT TRIGGERED CLIENT SIDE.");

                        // close the socket
                        closeSocket(clientSocketFinal);
                        isClosed = true;
                        System.exit(1);
                    }
                }
            } catch (Exception e) {
                System.err.println("EXCEPTION: " + e.getMessage());

            }
        }
    }


    // THREAD FOR RECEIVING MESSAGES
    private static class ReceivingThread implements Runnable {
        BufferedReader inFromServer;
        private int threadName;

        public ReceivingThread(int i, BufferedReader in) {
            threadName = i;
            inFromServer = in;
        }

        public void run() {
            try {
                String toRead;
                while ((toRead = inFromServer.readLine()) != null) {
                    // String toRead = inFromServer.readLine();
                    System.out.println("FROM SERVER >> " + toRead);
                }
            } catch (Exception e) {
                System.err.println("EXCEPTION: " + e.getMessage());
                try {
                    closeSocket(clientSocketFinal);
                } catch (Exception f) {
                    System.err.println("EXCEPTION: " + f.getMessage());
                }

                System.exit(1);
            }
        }
    }

    public static void main(String args[]) throws Exception {

        String ip = "localhost";
        int port = 4000;
        if (args.length > 0) {
            ip = args[0];
            if (args.length > 1) {
                port = Integer.parseInt(args[1]);
            }
        }

        // TODO: implement
        Client client = new Client(ip, port);
        String username = client.greeting();
        if (username != null) {
            client.menu();
        }

        //        try {
        //            String ip_address = args[0];
        //            int portNum = Integer.parseInt(args[1]);
        //
        //            // CLIENT SOCKET
        //            Socket clientSocket = new Socket(ip_address, portNum);
        //            clientSocketFinal = clientSocket;  // GLOBAL VARIABLE CHANGED
        //
        //            // I/O
        //            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        //            PrintWriter outToServer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
        //            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        //
        //            String username = greeting(outToServer, inFromServer, inFromUser);
        //            usernameFinal = username;  // GLOBAL VARIABLE CHANGED
        //
        //            if (username != null) menu(outToServer, inFromServer, inFromUser);
        //
        //            // CLOSING THE INDIVIDUAL SOCKET
        //            // closeSocket(clientSocket, username);
        //        } catch (Exception e) {
        //            System.err.println("Sorry, but your credentials did not match the Server! Please try again.");
        //        }
    }
}