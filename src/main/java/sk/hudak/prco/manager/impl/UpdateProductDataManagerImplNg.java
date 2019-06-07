package sk.hudak.prco.manager.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.dto.ProductUpdateDataDto;
import sk.hudak.prco.dto.internal.ParsingDataResponse;
import sk.hudak.prco.dto.internal.ProductUpdateData;
import sk.hudak.prco.dto.product.ProductDetailInfo;
import sk.hudak.prco.manager.UpdateProductDataListener;
import sk.hudak.prco.manager.UpdateProductDataManager;
import sk.hudak.prco.manager.UpdateStatusInfo;
import sk.hudak.prco.mapper.PrcoOrikaMapper;
import sk.hudak.prco.parser.HtmlParser;
import sk.hudak.prco.service.InternalTxService;
import sk.hudak.prco.task.EshopTaskManager;
import sk.hudak.prco.task.InternalTaskManager;
import sk.hudak.prco.task.VoidTask;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
@Component
public class UpdateProductDataManagerImplNg implements UpdateProductDataManager {

    @Autowired
    private InternalTxService internalTxService;

    @Autowired
    private HtmlParser htmlParser;

    @Autowired
    private EshopTaskManager eshopTaskManager;

    @Autowired
    private PrcoOrikaMapper mapper;

    @Autowired
    private ErrorHandlerImpl errorHandler;

    @Autowired
    private InternalTaskManager internalTaskManager;

    @Override
    public void updateProductData(Long productId) {

        // vyhladam product na update v DB na zaklade id
        Optional<ProductDetailInfo> productForUpdate = getProductForUpdate(productId);
        if (!productForUpdate.isPresent()) {
            // nedavat tu ziadel log !!!! pozri getProductForUpdate
            return;
        }
        internalParseAndUpdateAsync(productForUpdate.get(), UpdateProductDataListenerAdapter.LOG_INSTANCE);
    }

    @Override
    public void updateProductDataForEachProductInEshop(EshopUuid eshopUuid, UpdateProductDataListener listener) {

        notifyUpdateListener(eshopUuid, listener);

        Optional<ProductDetailInfo> productForUpdateOpt = getProductForUpdate(eshopUuid);
        while (productForUpdateOpt.isPresent()) {

            Future<Void> asyncResult = internalParseAndUpdateAsync(productForUpdateOpt.get(), listener);
            waitUntilFinish(asyncResult);

            productForUpdateOpt = getProductForUpdate(eshopUuid);
        }
        log.debug("none product found for update for eshop {}", eshopUuid);
    }

    private void waitUntilFinish(Future<?> voidFuture) {
        try {
            voidFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            log.error("interrupt exception", e);
        }
    }

    private Future<Void> internalParseAndUpdateAsync(ProductDetailInfo productDetailInfo, UpdateProductDataListener listener) {
        // spustim synchronny tread na parsovanie
        ParsingDataResponse productUpdateData = eshopTaskManager.parseOneProductUpdateTask(productDetailInfo, htmlParser);

        // spracujem asynchronne
        Future<Void> voidFuture = processAsyncParsingDataResponse(productUpdateData, productDetailInfo, listener);

        return voidFuture;
    }

    // FIXME lepsie nazvy zvolit pre vstupne parametre
    private Future<Void> processAsyncParsingDataResponse(ParsingDataResponse productUpdateData,
                                                         ProductDetailInfo productDetailInfo,
                                                         UpdateProductDataListener listener) {


        Future<Void> result = internalTaskManager.processAsync(productDetailInfo.getEshopUuid(), new VoidTask() {
            @Override
            protected void doInTask() throws Exception {
                if (productUpdateData.isError()) {

                    // spracovanie parsing chyby
                    errorHandler.processParsingError(productUpdateData.getError(), productDetailInfo);

                } else {

                    // spracovanie vyparsovanych dat
                    processParsedData(productUpdateData.getProductUpdateData(), productDetailInfo, listener);
                }
            }
        });

        return result;
    }

    /**
     * @param updateData        vyparsovane data z eshopu
     * @param productDetailInfo
     * @param listener
     */
    private void processParsedData(ProductUpdateData updateData, ProductDetailInfo productDetailInfo, UpdateProductDataListener listener) {
        // if not available log error and finish
        if (!updateData.isProductAvailable()) {
            internalTxService.markProductAsUnavailable(productDetailInfo.getId());
            return;
        }

        Optional<Long> existSameProductIdOpt = internalTxService.getProductWithUrl(updateData.getUrl(), productDetailInfo.getId());
        if (existSameProductIdOpt.isPresent()) {
            log.debug("exist another product {} with url {}", existSameProductIdOpt.get(), updateData.getUrl());
            log.debug("product {} will be removed, url {} ", productDetailInfo.getId(), productDetailInfo.getUrl());
        }

        Long productIdToBeUpdated = existSameProductIdOpt.isPresent() ? existSameProductIdOpt.get() : productDetailInfo.getId();


        //FIXME premapovanie cez sk.hudak.prco mapper nie takto rucne, nech mam na jednom mieste tie preklapacky...
        internalTxService.updateProduct(ProductUpdateDataDto.builder()
                .id(productIdToBeUpdated)
                .name(updateData.getName())
                .url(updateData.getUrl())
                .priceForPackage(updateData.getPriceForPackage())
                .productAction(updateData.getProductAction())
                .actionValidity(updateData.getActionValidity())
                .pictureUrl(updateData.getPictureUrl())
                .build());

        // remove product with old URL
        if (existSameProductIdOpt.isPresent()) {
            internalTxService.removeProduct(productDetailInfo.getId());
        }

        // po dokonceni nech vola:
        notifyUpdateListener(updateData.getEshopUuid(), listener);
    }

    private void notifyUpdateListener(EshopUuid eshopUuid, UpdateProductDataListener listener) {
        listener.onUpdateStatus(
                mapper.map(internalTxService.getStatisticForUpdateForEshop(eshopUuid, eshopUuid.getOlderThanInHours()),
                        UpdateStatusInfo.class)
        );
    }


    private Optional<ProductDetailInfo> getProductForUpdate(Long productId) {
        try {
            return Optional.of(internalTxService.getProductForUpdate(productId));

        } catch (Exception e) {
            log.error("error while getting information for product with id " + productId);
            return Optional.empty();
        }
    }

    private Optional<ProductDetailInfo> getProductForUpdate(EshopUuid eshopUuid) {
        int olderThanInHours = eshopUuid.getOlderThanInHours();
        try {
            return internalTxService.getProductForUpdate(eshopUuid, olderThanInHours);

        } catch (Exception e) {
            log.error("error while getting first product for update for eshop " + eshopUuid + " older than " + olderThanInHours + " hours");
            return Optional.empty();
        }
    }


    @Override
    public void updateProductDataForEachProductInGroup(Long groupId, UpdateProductDataListener listener) {

    }


    @Override
    public void updateProductDataForEachProductInEachEshop(UpdateProductDataListener listener) {

    }

    @Override
    public void updateProductDataForEachProductNotInAnyGroup(UpdateProductDataListener listener) {

    }
}
