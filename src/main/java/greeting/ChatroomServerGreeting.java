package greeting;

import constants.ChatroomConstants;
import objects.Credential;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class ChatroomServerGreeting implements ServerGreeting {

    @Override
    public boolean greet(BufferedReader inFromClient, PrintWriter outToClient, List<Credential> credentials) {
        boolean validated = false;
        try {
            int attempts = 0;
            do {
                // username
                outToClient.println("username: ");
                String username = inFromClient.readLine();
                // password
                outToClient.println("password: ");
                String password = inFromClient.readLine();
                // auth
                if (credentials.contains(new Credential(username, password))) {
                    outToClient.println(ChatroomConstants.OK);
                    validated = true;
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
        return validated;
    }
}
