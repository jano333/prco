package sk.hudak.prco.manager.impl;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.dto.ProductUpdateDataDto;
import sk.hudak.prco.dto.internal.ParsingDataResponse;
import sk.hudak.prco.dto.internal.ProductUpdateData;
import sk.hudak.prco.dto.product.ProductDetailInfo;
import sk.hudak.prco.dto.product.ProductFullDto;
import sk.hudak.prco.manager.ErrorHandler;
import sk.hudak.prco.manager.UpdateProductDataListener;
import sk.hudak.prco.manager.UpdateProductDataManager;
import sk.hudak.prco.manager.UpdateStatusInfo;
import sk.hudak.prco.mapper.PrcoOrikaMapper;
import sk.hudak.prco.parser.HtmlParser;
import sk.hudak.prco.service.InternalTxService;
import sk.hudak.prco.task.EshopTaskManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static sk.hudak.prco.manager.impl.UpdateProcessResult.ERR_UPDATE_ERROR_PRODUCT_IS_UNAVAILABLE;
import static sk.hudak.prco.manager.impl.UpdateProcessResult.OK;
import static sk.hudak.prco.utils.ThreadUtils.sleepRandomSafe;
import static sk.hudak.prco.utils.ThreadUtils.sleepSafe;

@Slf4j
@Component
public class UpdateProductDataManagerImpl implements UpdateProductDataManager {

    @Autowired
    private HtmlParser htmlParser;

    @Autowired
    private InternalTxService internalTxService;

    @Autowired
    private EshopTaskManager eshopTaskManager;

    @Autowired
    private PrcoOrikaMapper mapper;

    @Autowired
    private ErrorHandler errorHandler;

    @Override
    public void updateProductData(Long productId) {
        // vyhladam product na update v DB na zaklade id
        Optional<ProductDetailInfo> productForUpdate = getProductForUpdate(productId);
        if (!productForUpdate.isPresent()) {
            // nedavat tu ziadel log !! pozri getProductForUpdate
            return;
        }
        final EshopUuid eshopUuid = productForUpdate.get().getEshopUuid();

        eshopTaskManager.submitTask(eshopUuid, () -> {

            //FIXME spojit tie dve volania do jedneho
            // ak je to volane hned po sebe tak sleepnem
            eshopTaskManager.sleepIfNeeded(eshopUuid);
            eshopTaskManager.markTaskAsRunning(eshopUuid);

            UpdateProcessResult updateProcessResult = internalParseAndUpdate(productForUpdate.get(), UpdateProductDataListenerAdapter.LOG_INSTANCE);

            eshopTaskManager.markTaskAsFinished(eshopUuid, OK.equals(updateProcessResult));
        });
    }

    @Override
    public void updateProductDataForEachProductInEachEshop(@NonNull UpdateProductDataListener listener) {
        Arrays.stream(EshopUuid.values()).forEach(eshopUuid -> {
            updateProductDataForEachProductInEshop(eshopUuid, listener);
            // kazdy dalsi spusti s 1 sekundovym oneskorenim
            sleepSafe(1);
        });
    }

    @Override
    public void updateProductDataForEachProductInEshop(@NonNull EshopUuid eshopUuid, @NonNull UpdateProductDataListener listener) {

        eshopTaskManager.submitTask(eshopUuid, () -> {
            // ak je to volane hned po sebe tak sleepnem
            eshopTaskManager.sleepIfNeeded(eshopUuid);
            eshopTaskManager.markTaskAsRunning(eshopUuid);

            Optional<ProductDetailInfo> productForUpdateOpt = getProductForUpdate(eshopUuid);
            while (productForUpdateOpt.isPresent()) {

                notifyUpdateListener(eshopUuid, listener);

                UpdateProcessResult updateProcessResult = internalParseAndUpdate(productForUpdateOpt.get(), UpdateProductDataListenerAdapter.EMPTY_INSTANCE);

                if (shouldContinueWithNexProduct(updateProcessResult)) {
                    sleepRandomSafe();

                    productForUpdateOpt = getProductForUpdate(eshopUuid);

                } else {
                    eshopTaskManager.markTaskAsFinished(eshopUuid, true);
                    return;
                }

                if (eshopTaskManager.isTaskShouldStopped(eshopUuid)) {
                    eshopTaskManager.markTaskAsStopped(eshopUuid);
                    break;
                }
            }

            eshopTaskManager.markTaskAsFinished(eshopUuid, false);
            log.debug("none product found for update for eshop {}", eshopUuid);
        });
    }

    @Override
    public void updateProductDataForEachProductInGroup(Long groupId, @NonNull UpdateProductDataListener listener) {
        //TODO bug !!!!!! nech nevracia len tie,  ktore uz boli updatnute
        Map<EshopUuid, List<ProductDetailInfo>> productsInGroup = convert(internalTxService.findProductsInGroup(groupId, true));
        if (productsInGroup.isEmpty()) {
            log.debug("none product found for update in group with id {} ", groupId);
            return;
        }
        updateProductData(productsInGroup, listener);
    }

    private Map<EshopUuid, List<ProductDetailInfo>> convert(List<ProductFullDto> productsForUpdate) {
        Map<EshopUuid, List<ProductDetailInfo>> productsInEshop = new EnumMap<>(EshopUuid.class);
        productsForUpdate.forEach(productFullDto -> productsInEshop.put(productFullDto.getEshopUuid(), new ArrayList<>()));
        productsForUpdate.forEach(productFullDto -> productsInEshop.get(productFullDto.getEshopUuid()).add(
                new ProductDetailInfo(productFullDto.getId(), productFullDto.getUrl(), productFullDto.getEshopUuid())));
        return productsInEshop;
    }

    @Override
    public void updateProductDataForEachProductNotInAnyGroup(@NonNull UpdateProductDataListener listener) {

        Map<EshopUuid, List<ProductDetailInfo>> productsNotInAnyGroup = convert(internalTxService.findProductsNotInAnyGroup());

        if (productsNotInAnyGroup.isEmpty()) {
            log.debug("none product found for update which is not in any group");
            return;
        }
        updateProductData(productsNotInAnyGroup, listener);
    }

    private boolean shouldContinueWithNexProduct(UpdateProcessResult updateProcessResult) {
        if (OK.equals(updateProcessResult)) {
            return true;
        }

        if (ERR_UPDATE_ERROR_PRODUCT_IS_UNAVAILABLE.equals(updateProcessResult)) {
            return true;
        }

        //TODO impl pre ktore typy chyb sa ma process zastavit(teda dalsi product z daneho ehopu uz nebude spracovany)
        return false;
    }

    private UpdateProcessResult internalParseAndUpdate(ProductDetailInfo productDetailInfo, UpdateProductDataListener listener) {
        // parsing
        ParsingDataResponse parsingDataResponse = parseOneProductUpdateData(productDetailInfo);

        // response processing and updating if ok
        return processParsingDataResponse(parsingDataResponse, productDetailInfo, listener);
    }

    private ParsingDataResponse parseOneProductUpdateData(ProductDetailInfo productDetailInfo) {
        try {
            return new ParsingDataResponse(htmlParser.parseProductUpdateData(productDetailInfo.getUrl()));

        } catch (Exception e) {
            return new ParsingDataResponse(e);
        }
    }

    // FIXME lepsie nazvy zvolit pre vstupne parametre

    private UpdateProcessResult processParsingDataResponse(ParsingDataResponse parsingDataResponse,
                                                           ProductDetailInfo parsingDataRequest,
                                                           UpdateProductDataListener listener) {
        if (parsingDataResponse.isError()) {
            // spracovanie parsing chyby
            return errorHandler.processParsingError(parsingDataResponse.getError(), parsingDataRequest);

        }
        // spracovanie vyparsovanych dat
        return processParsedData(parsingDataResponse.getProductUpdateData(), parsingDataRequest, listener);
    }

    /**
     * @param updateData         vyparsovane data z eshopu
     * @param parsingDataRequest
     * @param listener
     */
    private UpdateProcessResult processParsedData(ProductUpdateData updateData, ProductDetailInfo parsingDataRequest, UpdateProductDataListener listener) {
        // if not available log error and finish
        if (!updateData.isProductAvailable()) {
            internalTxService.markProductAsUnavailable(parsingDataRequest.getId());
            return ERR_UPDATE_ERROR_PRODUCT_IS_UNAVAILABLE;
        }

        Optional<Long> existSameProductIdOpt = internalTxService.getProductWithUrl(updateData.getUrl(), parsingDataRequest.getId());
        if (existSameProductIdOpt.isPresent()) {
            log.debug("exist another product {} with url {}", existSameProductIdOpt.get(), updateData.getUrl());
            log.debug("product {} will be removed, url {} ", parsingDataRequest.getId(), parsingDataRequest.getUrl());
        }

        Long productIdToBeUpdated = existSameProductIdOpt.isPresent() ? existSameProductIdOpt.get() : parsingDataRequest.getId();


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
            internalTxService.removeProduct(parsingDataRequest.getId());
        }

        // po dokonceni nech vola:
        notifyUpdateListener(updateData.getEshopUuid(), listener);

        return OK;
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

    private void updateProductData(Map<EshopUuid, List<ProductDetailInfo>> productsForUpdate, UpdateProductDataListener listener) {


        for (Map.Entry<EshopUuid, List<ProductDetailInfo>> eshopUuidListEntry : productsForUpdate.entrySet()) {

            final EshopUuid eshopUuid = eshopUuidListEntry.getKey();
            List<ProductDetailInfo> productForUpdateList = eshopUuidListEntry.getValue();

            eshopTaskManager.submitTask(eshopUuid, () -> {
                // ak je to volane hned po sebe tak sleepnem
                eshopTaskManager.sleepIfNeeded(eshopUuid);
                eshopTaskManager.markTaskAsRunning(eshopUuid);

                long countOfProductsAlreadyUpdated = 0;
                long countOfProductsWaitingToBeUpdated = productForUpdateList.size();

                for (ProductDetailInfo productForUpdate : productForUpdateList) {

                    listener.onUpdateStatus(new UpdateStatusInfo(eshopUuid, countOfProductsWaitingToBeUpdated, countOfProductsAlreadyUpdated));

                    UpdateProcessResult updateProcessResult = internalParseAndUpdate(productForUpdate, UpdateProductDataListenerAdapter.EMPTY_INSTANCE);

                    countOfProductsAlreadyUpdated++;
                    countOfProductsWaitingToBeUpdated--;

                    if (shouldContinueWithNexProduct(updateProcessResult)) {
                        if (eshopTaskManager.isTaskShouldStopped(eshopUuid)) {
                            eshopTaskManager.markTaskAsStopped(eshopUuid);
                            break;
                        }

                        sleepRandomSafe();
                        continue;
                    }
                    eshopTaskManager.markTaskAsFinished(eshopUuid, true);
                    break;
                }

                eshopTaskManager.markTaskAsFinished(eshopUuid, false);

            });

            // kazdy dalsi task pre eshop spusti s 2 sekundovym oneskorenim
            sleepSafe(2);
        }
    }

    private void notifyUpdateListener(EshopUuid eshopUuid, UpdateProductDataListener listener) {
        listener.onUpdateStatus(
                mapper.map(internalTxService.getStatisticForUpdateForEshop(eshopUuid, eshopUuid.getOlderThanInHours()),
                        UpdateStatusInfo.class)
        );
    }
}
