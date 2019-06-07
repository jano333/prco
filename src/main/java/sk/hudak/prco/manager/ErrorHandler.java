package sk.hudak.prco.manager;

import sk.hudak.prco.dto.product.ProductDetailInfo;

public interface ErrorHandler {

    void processParsingError(Exception error, ProductDetailInfo productDetailInfo);
}
