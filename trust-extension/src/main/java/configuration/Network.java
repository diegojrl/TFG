package configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

public final class Network {
    private byte[] prefix;
    private short mask;

    public Network(final String address) throws UnknownHostException {
        if (address.indexOf('/') > 0) {
            String[] net = address.split("/");
            this.mask = Short.parseShort(net[1]);
            this.prefix = InetAddress.getByName(net[0]).getAddress();
        }
    }


    public boolean matches(InetAddress address) {
        byte[] ip = address.getAddress();
        boolean match = true;
        int idx = mask / 8;
        for (int i = 0; match && i < idx; i++)
            if (prefix[i] != ip[i]) match = false;

        if (match && mask % 8 != 0) {
            int shift = ((8 * prefix.length) - mask) % 8;
            if ((ip[idx] >> shift) != (prefix[idx] >> shift))
                match = false;
        }

        return match;
    }
}
