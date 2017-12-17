package commands;

import objects.ClientMessage;

import java.util.ArrayList;
import java.util.List;

public class CommandNotFound {

    /**
     * send "message not found" back to sender
     * @param username sender username
     * @return client messages
     */
    public List<ClientMessage> execute(String username) {
        List<ClientMessage> messages = new ArrayList<>();
        messages.add(new ClientMessage(username, "command not found, please try again"));
        return messages;
    }
}
