package sk.hudak.prco.parser.html

import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.client.BasicResponseHandler
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.cookie.BasicClientCookie
import java.io.IOException


object ApatcheHttpParserImpl {

    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {

        val cookieStore = BasicCookieStore()
        val cookie = BasicClientCookie("hbMarketCookie", "746")
        cookie.domain = "www.hornbach.sk"
        cookie.path = "/"
        cookieStore.addCookie(cookie)
        val httpclient = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build()

        //        CloseableHttpClient httpclient = HttpClients.createDefault();
        val httpGet = HttpGet("https://www.hornbach.sk/shop/Bosch-GBH-2-28-F-s-funkciou-Kick-Back-Control-vr-dlata-a-vrtaka/6348699/artikel.html")


        val responseHandler = BasicResponseHandler()
        val responseBody = httpclient.execute(httpGet, responseHandler)

        println(responseBody)

        //        HttpResponse response = httpclient.execute(httpGet, responseHandler);
        //        HttpEntity entity = response.getEntity();

        println("Konec")

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
