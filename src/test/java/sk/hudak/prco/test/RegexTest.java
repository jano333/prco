package sk.hudak.prco.test;

import org.junit.Test;

public class RegexTest {

    @Test
    public void testReqex(){
        String productName = "Pampers Fresh Clean Čistiace Obrúsky 4x64 Kusov";
        String[] words = productName.split(" ");
        for (int i = 0; i < words.length; i++) {
            String word = words[i];

            if(word.matches("\\d+x\\d+")){
                System.out.println("word "+word+" result ano");
            }else {
                System.out.println("word "+word+" result nie");
            }
        }


    }
}
