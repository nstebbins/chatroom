package commands;

import objects.ClientMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class DirectMessage {

    public List<ClientMessage> execute(String senderUsername, String receiverUsername, String message) {
        List<ClientMessage> messages = new ArrayList<>();
        messages.add(new ClientMessage(senderUsername, "private message has been sent"));
        messages.add(new ClientMessage(receiverUsername, message));
        return messages;
    }
}
