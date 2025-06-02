package authorization.configuration;

import java.util.List;

public final class File {
    public List<Policy> permissions;

    @Override
    public String toString() {
        return permissions.toString();
    }
}
