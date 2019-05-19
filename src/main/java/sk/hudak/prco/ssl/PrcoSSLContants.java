package sk.hudak.prco.ssl;

import java.util.HashSet;
import java.util.Set;

public class PrcoSSLContants {

    // obsahuje zoznam povenych hostov, pre ktore sa nevaliduje
    protected static final Set<String> ALLOWED_HOSTNAME = new HashSet<>(1);
    static {
        ALLOWED_HOSTNAME.add("hej.sk");

        ALLOWED_HOSTNAME.add("www.alza.cz");
        ALLOWED_HOSTNAME.add("www.alza.sk");

        ALLOWED_HOSTNAME.add("esodrogeria.eu");
        ALLOWED_HOSTNAME.add("premium-wask.cz");

        ALLOWED_HOSTNAME.add("pilulka-lb1.vshosting.cz");
        ALLOWED_HOSTNAME.add("pilulka.cz");
        ALLOWED_HOSTNAME.add("www.pilulka.sk");

        ALLOWED_HOSTNAME.add("www.feedo.sk");

        ALLOWED_HOSTNAME.add("obi.at");
        ALLOWED_HOSTNAME.add("www.obi.sk");

        ALLOWED_HOSTNAME.add("www.lekarna.cz");
        ALLOWED_HOSTNAME.add("www.mojalekaren.sk");

        ALLOWED_HOSTNAME.add("www.drmax.sk");

        ALLOWED_HOSTNAME.add("kidmarket.sk");
        ALLOWED_HOSTNAME.add("admin.asdata.sk");

        ALLOWED_HOSTNAME.add("www.brendon.sk");
        ALLOWED_HOSTNAME.add("brendon.hu");

        ALLOWED_HOSTNAME.add("www.4kids.sk");

        ALLOWED_HOSTNAME.add("mamaaja.sk");
        ALLOWED_HOSTNAME.add("orbi-02.webglobe.sk");

        ALLOWED_HOSTNAME.add("www.amddrogeria.sk");

        ALLOWED_HOSTNAME.add("www.drogeria-vmd.sk");
        ALLOWED_HOSTNAME.add("b2bexchange.vmd-drogerie.cz");

        ALLOWED_HOSTNAME.add("amy.onebit.cz");
        ALLOWED_HOSTNAME.add("dave.onebit.cz");
        ALLOWED_HOSTNAME.add("www.gigalekaren.sk");

        ALLOWED_HOSTNAME.add("www.prva-lekaren.sk");

        ALLOWED_HOSTNAME.add("www.lekaren-bella.sk");

        ALLOWED_HOSTNAME.add("www.lekarenvkocke.sk");

        ALLOWED_HOSTNAME.add("elbiahosting.sk");
        ALLOWED_HOSTNAME.add("www.magano.sk");

        ALLOWED_HOSTNAME.add("www.esodrogeria.eu");

        ALLOWED_HOSTNAME.add("drogerka.sk");

        ALLOWED_HOSTNAME.add("moonlake.cz");
        ALLOWED_HOSTNAME.add("www.lekarenexpres.sk");





    }

    private PrcoSSLContants() {
        // no instance
    }
}
