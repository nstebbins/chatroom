package greeting;

import java.io.BufferedReader;
import java.io.PrintWriter;

public interface ClientGreeting {
    String greet(BufferedReader inFromUser, BufferedReader inFromServer, PrintWriter outToServer);
}
