package sk.hudak.prco.builder;

import sk.hudak.prco.api.EshopUuid;

public interface SearchUrlBuilder {

    String buildSearchUrl(EshopUuid eshopUuid, String searchKeyword);

    String buildSearchUrl(EshopUuid eshopUuid, String searchKeyWord, int currentPageNumber);
}
