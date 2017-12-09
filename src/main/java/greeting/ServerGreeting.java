package greeting;

import objects.Credential;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.List;

public interface ServerGreeting {
    String greet(BufferedReader inFromClient, PrintWriter outToClient, List<Credential> credentials);
}
