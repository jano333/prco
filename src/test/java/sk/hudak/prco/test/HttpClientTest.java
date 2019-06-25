package sk.hudak.prco.test;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.apache.http.HttpHeaders.USER_AGENT;

public class HttpClientTest {

    public static void main(String[] args) throws IOException {

        HttpClient client = HttpClientBuilder.create().build();

        HttpGet request = new HttpGet("https://www.mall.sk/hladaj?s=pampers");
        request.addHeader("User-Agent", USER_AGENT);

        HttpResponse response = client.execute(request);

        System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
        Header[] responseAllHeaders = response.getAllHeaders();


        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        System.out.println("---");
        System.out.println(result.toString().trim());
    }
}
