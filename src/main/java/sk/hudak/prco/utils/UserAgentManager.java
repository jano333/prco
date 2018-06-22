package sk.hudak.prco.utils;

import java.util.Random;

public class UserAgentManager {

    private UserAgentManager() {
    }

    public static final String FIREFOX_1 = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1";
    public static final String FIREFOX_2 = "Mozilla/5.0 (Windows NT 6.3; rv:36.0) Gecko/20100101 Firefox/36.0";
    public static final String FIREFOX_3 = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10; rv:33.0) Gecko/20100101 Firefox/33.0";

    public static final String CHROME_1 = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";
    public static final String CHROME_2 = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2227.1 Safari/537.36";
    public static final String CHROME_3 = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2227.0 Safari/537.36";

    public static final String IE_1 = "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; AS; rv:11.0) like Gecko";
    public static final String IE_2 = "Mozilla/5.0 (compatible, MSIE 11, Windows NT 6.3; Trident/7.0; rv:11.0) like Gecko";
    public static final String IE_3 = "Mozilla/5.0 (compatible; MSIE 10.6; Windows NT 6.1; Trident/5.0; InfoPath.2; SLCC1; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; .NET CLR 2.0.50727) 3gpp-gba UNTRUSTED/1.0";


    private static final String[] UA_S = new String[]{
            FIREFOX_1, FIREFOX_2, FIREFOX_3,
            CHROME_1, CHROME_2, CHROME_3,
            IE_1, IE_2, IE_3
    };

    public static String getRandom() {
        //random.nextInt(max - min + 1) + min
        return UA_S[new Random().nextInt(8)];
    }

}
