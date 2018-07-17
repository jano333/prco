package sk.hudak.prco.ssl;

import java.util.HashSet;
import java.util.Set;

public class PrcoSSLContants {

    // obsahuje zoznam povenych hostov, pre ktore sa nevaliduje
    public static final Set<String> ALLOWED_HOSTNAME = new HashSet<>(1);
    static {
        ALLOWED_HOSTNAME.add("hej.sk");
        ALLOWED_HOSTNAME.add("esodrogeria.eu");
    }

    private PrcoSSLContants() {
        // no instance
    }
}
