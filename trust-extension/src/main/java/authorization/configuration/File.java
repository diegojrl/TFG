package authorization.configuration;

import java.util.List;

public class File {
    public List<Policy> permissions;

    @Override
    public String toString() {
        return permissions.toString();
    }
}
