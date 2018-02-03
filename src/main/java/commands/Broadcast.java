package commands;

import objects.ClientMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Broadcast {

    /**
     * broadcast message to all users in chatroom
     *
     * @param username       sender username
     * @param availableUsers available users
     * @param message        message to send
     * @return client messages
     */
    public List<ClientMessage> execute(String username, Queue<String> availableUsers, String message) {
        List<ClientMessage> messages = new ArrayList<>();
        for (String availableUser : availableUsers) {
            messages.add(new ClientMessage(availableUser, message));
        }
        messages.add(new ClientMessage(username, "broadcast message has been sent"));
        return messages;
    }
}
