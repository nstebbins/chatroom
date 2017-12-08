package Greeting;

import Constants.ChatroomConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ChatroomClientGreeting implements ClientGreeting {

    @Override
    public String greet(BufferedReader inFromUser, BufferedReader inFromServer, PrintWriter outToServer) {
        String username;
        try {
            int attempts = 0;
            String authMessage;
            do {
                System.out.println("[server] " + inFromServer.readLine());
                username = inFromUser.readLine();
                outToServer.println(username);
                // password
                System.out.println("[server] " + inFromServer.readLine());
                String password = inFromUser.readLine();
                outToServer.println(password);
                // auth
                authMessage = inFromServer.readLine();
                System.out.println("[server] " + authMessage);
                if (authMessage.equals(ChatroomConstants.OK)) {
                    // OK from server
                    return authMessage;
                }
                attempts++;
            } while (attempts < 3);
        } catch (IOException e) {
            System.err.println("error reading in user input");
            e.printStackTrace();
        }
        // no OK from server
        return null;
    }

}
