package objects;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public final class Credential {
    private final String username;
    private final String password;

    public Credential(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * read credentials
     *
     * @param file file containing credentials
     * @return list of credentials
     */
    public static List<Credential> readCredentials(String file) {
        List<Credential> credentials = new ArrayList<>();
        try {
            BufferedReader inFromFile = new BufferedReader(new FileReader(file));
            while (inFromFile.ready()) {
                String[] credentialList = inFromFile.readLine().split(" ");
                credentials.add(new Credential(credentialList[0], credentialList[1]));

            }
            inFromFile.close();
        } catch (Exception e) {
            System.err.println("error reading in credentials");
        }
        System.out.println("number of credentials read in: " + credentials.size());
        return credentials;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public int hashCode() {
        int result = username.hashCode();
        result = 31 * result + password.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Credential that = (Credential) o;
        return username.equals(that.username) && password.equals(that.password);
    }
}

