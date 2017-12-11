package commands;

import objects.ClientMessage;

import java.util.Queue;

public class WhoElse {

    public WhoElse() {}

    public ClientMessage execute(String username, Queue<String> availableUsers) {
        String message = "";
        for (String availableUser : availableUsers) {
            message += availableUser + " ";
        }
        return new ClientMessage(username, message);
    }

}
