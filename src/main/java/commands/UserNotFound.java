package commands;

import objects.ClientMessage;

import java.util.ArrayList;
import java.util.List;

public class UserNotFound {

    /**
     * send "user not found" back to sender
     *
     * @param senderUsername   sender username
     * @param receiverUsername receiver username
     * @return client messages
     */
    public List<ClientMessage> execute(String senderUsername, String receiverUsername) {
        List<ClientMessage> messages = new ArrayList<>();
        messages.add(new ClientMessage(senderUsername, "user <" + receiverUsername + "> not found, please try again"));
        return messages;
    }
}
