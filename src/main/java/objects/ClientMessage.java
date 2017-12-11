package objects;

public class ClientMessage {
    private String username;
    private String message;

    public ClientMessage(String username, String message) {
        this.username = username;
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }
}
