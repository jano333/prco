package sk.hudak.prco.test;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.api.SearchTemplateConstants;
import sk.hudak.prco.builder.SearchUrlBuilder;
import sk.hudak.prco.builder.SearchUrlBuilderImpl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static sk.hudak.prco.api.EshopUuid.ALZA;
import static sk.hudak.prco.api.EshopUuid.BAMBINO;
import static sk.hudak.prco.api.EshopUuid.DR_MAX;

@Slf4j
public class SearchUrlBuilderImplTest {

    public static final String SEARCH_KEYWORD = "pampers 5";

    @AllArgsConstructor
    private static class Structure {
        String plain;
        String page1;
        String page2;
    }

    private static Map<EshopUuid, Structure> data = new HashMap<>();

    static {
        data.put(ALZA, new Structure(
                "https://www.alza.sk/search.htm?exps=pampers%205",
                "https://www.alza.sk/search-p1.htm?exps=pampers%205",
                "https://www.alza.sk/search-p2.htm?exps=pampers%205"));
        data.put(BAMBINO, new Structure(
                "https://www.bambino.sk/vyhladavanie?search=pampers%205",
                "https://www.bambino.sk/vyhladavanie/1?search=pampers%205",
                "https://www.bambino.sk/vyhladavanie/2?search=pampers%205"));
        data.put(DR_MAX, new Structure(
                "https://www.drmax.sk/catalog/search/?q=pampers%205",
                "https://www.drmax.sk/catalog/search/?q=pampers%205&offset=0&limit=24",
                "https://www.drmax.sk/catalog/search/?q=pampers%205&offset=24&limit=24"));


    }

    @Test
    public void testIt() {
        SearchUrlBuilder searchUrlBuilder = new SearchUrlBuilderImpl();
        Arrays.stream(EshopUuid.values()).forEach(eshopUuid -> {

            String result = searchUrlBuilder.buildSearchUrl(eshopUuid, SEARCH_KEYWORD);
            log.debug(eshopUuid.name() + " " + result);
//            String plain = data.get(eshopUuid).plain;
//            assertTrue(plain.equals(result));


            if (EshopUuid.HORNBACH.equals(eshopUuid)) {
                log.debug("");
                return;
            }

            String result2 = searchUrlBuilder.buildSearchUrl(eshopUuid, SEARCH_KEYWORD, 1);
            log.debug(eshopUuid.name() + " " + result2);
            assertNotNull(result2);
            assertFalse(result2.contains(SearchTemplateConstants.KEYWORD_TEMP));
            assertFalse(result2.contains(SearchTemplateConstants.PAGE_NUMBER_TEMP));

            String result3 = searchUrlBuilder.buildSearchUrl(eshopUuid, SEARCH_KEYWORD, 2);
            log.debug(eshopUuid.name() + " " + result3);
            assertNotNull(result3);
            assertFalse(result3.contains(SearchTemplateConstants.KEYWORD_TEMP));
            assertFalse(result3.contains(SearchTemplateConstants.PAGE_NUMBER_TEMP));


            log.debug("");

        });
    }

}
