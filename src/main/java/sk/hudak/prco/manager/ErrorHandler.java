package sk.hudak.prco.manager;

import sk.hudak.prco.dto.product.ProductDetailInfo;
import sk.hudak.prco.manager.impl.UpdateProcessResult;

public interface ErrorHandler {
    //FIXME premenovat
    UpdateProcessResult processParsingError(Exception error, ProductDetailInfo productDetailInfo);
}
