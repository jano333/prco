package sk.hudak.prco.manager.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.dto.error.ErrorCreateDto;
import sk.hudak.prco.dto.product.ProductDetailInfo;
import sk.hudak.prco.exception.HttpErrorPrcoRuntimeException;
import sk.hudak.prco.exception.HttpSocketTimeoutPrcoRuntimeException;
import sk.hudak.prco.manager.ErrorHandler;
import sk.hudak.prco.service.InternalTxService;

import static sk.hudak.prco.api.ErrorType.HTTP_STATUS_404_ERR;
import static sk.hudak.prco.api.ErrorType.HTTP_STATUS_ERR;
import static sk.hudak.prco.api.ErrorType.PARSING_PRODUCT_INFO_ERR;
import static sk.hudak.prco.api.ErrorType.TIME_OUT_ERR;
import static sk.hudak.prco.manager.impl.UpdateProcessResult.ERR_PARSING_ERROR_GENERIC;
import static sk.hudak.prco.manager.impl.UpdateProcessResult.ERR_PARSING_ERROR_HTTP_STATUS_404;
import static sk.hudak.prco.manager.impl.UpdateProcessResult.ERR_PARSING_ERROR_HTTP_STATUS_INVALID;
import static sk.hudak.prco.manager.impl.UpdateProcessResult.ERR_PARSING_ERROR_HTTP_TIMEOUT;

@Slf4j
@Component
public class ErrorHandlerImpl implements ErrorHandler {

    @Autowired
    private InternalTxService internalTxService;

    @Override
    public UpdateProcessResult processParsingError(Exception error, ProductDetailInfo productDetailInfo) {
        log.error("error while parsing product data for product " + productDetailInfo.getUrl(), error);

        if (error instanceof HttpErrorPrcoRuntimeException) {
            return handleHttpErrorPrcoRuntimeException((HttpErrorPrcoRuntimeException) error, productDetailInfo);
        }

        if (error instanceof HttpSocketTimeoutPrcoRuntimeException) {
            return handleHttpSocketTimeoutPrcoRuntimeException((HttpSocketTimeoutPrcoRuntimeException) error, productDetailInfo);
        }

        saveGenericParsingError(productDetailInfo.getEshopUuid(), productDetailInfo.getUrl(), error.getMessage(), error);
        return ERR_PARSING_ERROR_GENERIC;
    }

    private UpdateProcessResult handleHttpErrorPrcoRuntimeException(HttpErrorPrcoRuntimeException e, ProductDetailInfo productDetailInfo) {
        log.error("http status: " + e.getHttpStatus());
        saveInvalidHttpStatusError(productDetailInfo.getEshopUuid(), productDetailInfo.getUrl(), e.getMessage(), e);
        if (404 == e.getHttpStatus()) {
            internalTxService.removeProduct(productDetailInfo.getId());
            return ERR_PARSING_ERROR_HTTP_STATUS_404;
        }
        return ERR_PARSING_ERROR_HTTP_STATUS_INVALID;
    }

    private UpdateProcessResult handleHttpSocketTimeoutPrcoRuntimeException(HttpSocketTimeoutPrcoRuntimeException error, ProductDetailInfo productDetailInfo) {
        saveTimeout4Error(productDetailInfo.getEshopUuid(), productDetailInfo.getUrl(), error.getMessage(), error);
        return ERR_PARSING_ERROR_HTTP_TIMEOUT;
    }

    private void saveInvalidHttpStatusError(EshopUuid eshopUuid, String url, String message, HttpErrorPrcoRuntimeException e) {
        internalTxService.createError(ErrorCreateDto.builder()
                .errorType(404 == e.getHttpStatus() ? HTTP_STATUS_404_ERR : HTTP_STATUS_ERR)
                .eshopUuid(eshopUuid)
                .url(url)
                .message(message)
                .statusCode(String.valueOf(e.getHttpStatus()))
                .fullMsg(ExceptionUtils.getStackTrace(e))
                .build());
    }

    private void saveGenericParsingError(EshopUuid eshopUuid, String url, String message, Exception error) {
        internalTxService.createError(ErrorCreateDto.builder()
                .errorType(PARSING_PRODUCT_INFO_ERR)
                .eshopUuid(eshopUuid)
                .url(url)
                .message(message)
                .fullMsg(ExceptionUtils.getStackTrace(error))
                .build());
    }

    private void saveTimeout4Error(EshopUuid eshopUuid, String url, String message, HttpSocketTimeoutPrcoRuntimeException e) {
        internalTxService.createError(ErrorCreateDto.builder()
                .errorType(TIME_OUT_ERR)
                .eshopUuid(eshopUuid)
                .url(url)
                .message(message)
                .fullMsg(ExceptionUtils.getStackTrace(e))
                .build());
    }
}
