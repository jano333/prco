package sk.hudak.prco.ssl;

import java.util.HashSet;
import java.util.Set;

public class PrcoSSLContants {

    // obsahuje zoznam povenych hostov, pre ktore sa nevaliduje
    protected static final Set<String> ALLOWED_HOSTNAME = new HashSet<>(1);
    static {
        ALLOWED_HOSTNAME.add("hej.sk");

        ALLOWED_HOSTNAME.add("esodrogeria.eu");

        ALLOWED_HOSTNAME.add("pilulka-lb1.vshosting.cz");
        ALLOWED_HOSTNAME.add("www.pilulka.sk");

        ALLOWED_HOSTNAME.add("www.feedo.sk");

        ALLOWED_HOSTNAME.add("obi.at");
        ALLOWED_HOSTNAME.add("www.obi.sk");

        ALLOWED_HOSTNAME.add("www.lekarna.cz");
        ALLOWED_HOSTNAME.add("www.mojalekaren.sk");
    }

    private PrcoSSLContants() {
        // no instance
    }
}
