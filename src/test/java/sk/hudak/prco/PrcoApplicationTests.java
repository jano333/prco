package sk.hudak.prco;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;
import sk.hudak.prco.eshop.FeedoProductParser;

@RunWith(SpringRunner.class)
@SpringBootTest
@ComponentScan("sk.hudak.prco")
public class PrcoApplicationTests {


    @Autowired
    private FeedoProductParser feedoProductParser;

//    @Test
//    public void contextLoads() {
//        List<String> strings = feedoProductParser.parseUrlsOfProduct("nutrilon 4");
//        System.out.println(strings);
//    }

    @Test
    public void haha(){
        String detailUrl = "https://www.feedo.sk/pampers-active-baby-4-maxi-174ks-8-14kg-mesacna-zasoba-jednorazove-plienky/";

//        ProductUpdateData productUpdateData = feedoProductParser.parseProductUpdateData(detailUrl);
//        System.out.println(productUpdateData);
    }

}
