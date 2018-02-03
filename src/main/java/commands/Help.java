package commands;

import objects.ClientMessage;

import java.util.ArrayList;
import java.util.List;

public class Help {

    /**
     * send help info back to sender
     *
     * @param username sender username
     * @return client messages
     */
    public List<ClientMessage> execute(String username) {
        List<ClientMessage> messages = new ArrayList<>();
        String message = "usage: help\n\n";
        message += "whoelse: see who else is currently in chatroom\n";
        message += "broadcast <message>: sends <message> to all online clients\n";
        message += "message <username> <message>: sends <message> to <username>\n";
        message += "help: gives an overview of commands available\n";
        messages.add(new ClientMessage(username, message));
        return messages;
    }
}
