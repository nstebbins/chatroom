package commands;

import objects.ClientMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class WhoElse {

    /**
     * message list of chatroom member usernames
     * @param username sender username
     * @param availableUsers available users
     * @return client messages
     */
    public List<ClientMessage> execute(String username, Queue<String> availableUsers) {
        List<ClientMessage> messages = new ArrayList<>();
        String message = "";
        for (String availableUser : availableUsers) {
            message += availableUser + " ";
        }
        messages.add(new ClientMessage(username, message));
        return messages;
    }

}
