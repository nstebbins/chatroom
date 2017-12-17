package commands;

import objects.ClientMessage;

import java.util.ArrayList;
import java.util.List;

public class DirectMessage {

    /**
     * direct message from sender to receiver
     * @param senderUsername sender username
     * @param receiverUsername receiver username
     * @param message message to send
     * @return client messages
     */
    public List<ClientMessage> execute(String senderUsername, String receiverUsername, String message) {
        List<ClientMessage> messages = new ArrayList<>();
        messages.add(new ClientMessage(senderUsername, "private message has been sent"));
        messages.add(new ClientMessage(receiverUsername, message));
        return messages;
    }
}
