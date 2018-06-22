package sk.hudak.prco.parser.impl;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;

import java.io.IOException;


public class ApatcheHttpParserImpl {

    public static void main(String[] args) throws IOException {

        BasicCookieStore cookieStore = new BasicCookieStore();
        BasicClientCookie cookie = new BasicClientCookie("hbMarketCookie", "746");
        cookie.setDomain("www.hornbach.sk");
        cookie.setPath("/");
        cookieStore.addCookie(cookie);
        HttpClient httpclient = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build();

//        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://www.hornbach.sk/shop/Bosch-GBH-2-28-F-s-funkciou-Kick-Back-Control-vr-dlata-a-vrtaka/6348699/artikel.html");


        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody = httpclient.execute(httpGet, responseHandler);

        System.out.println(responseBody);

//        HttpResponse response = httpclient.execute(httpGet, responseHandler);
//        HttpEntity entity = response.getEntity();

        System.out.println("Konec");

//        try {
//            System.out.println(response1.getStatusLine());
//            HttpEntity entity1 = response1.getEntity();
//            // do something useful with the response body
//            // and ensure it is fully consumed
//            EntityUtils.consume(entity1);
//
//        } finally {
//            response1.close();
//        }

    }
}
