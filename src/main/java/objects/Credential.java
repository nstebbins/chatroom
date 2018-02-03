package objects;

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
     * @return list of credentials
     */
    public static List<Credential> generateTestCredentials() {
        List<Credential> credentials = new ArrayList<>();
        credentials.add(new Credential("Columbia", "116bway"));
        credentials.add(new Credential("SEAS", "winterbreakisover"));
        credentials.add(new Credential("csee4119", "lotsofexams"));
        credentials.add(new Credential("foobar", "passpass"));
        credentials.add(new Credential("windows", "withglass"));
        credentials.add(new Credential("Google", "hasglasses"));
        credentials.add(new Credential("facebook", "wastingtime"));
        credentials.add(new Credential("wikipedia", "donation"));
        credentials.add(new Credential("network", "seemsez"));
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

