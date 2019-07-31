package sk.hudak.prco.service;

import sk.hudak.prco.api.EshopUuid;

public interface NotInterestedProductService {

    void deleteNotInterestedProducts(Long... notInterestedProductIds);

    void deleteNotInterestedProducts(EshopUuid eshopUuid);
}
