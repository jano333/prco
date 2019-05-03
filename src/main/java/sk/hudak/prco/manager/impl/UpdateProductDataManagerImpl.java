package sk.hudak.prco.manager.impl;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sk.hudak.prco.api.ErrorType;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.dto.ProductUpdateDataDto;
import sk.hudak.prco.dto.error.ErrorCreateDto;
import sk.hudak.prco.dto.internal.ProductUpdateData;
import sk.hudak.prco.dto.product.ProductDetailInfo;
import sk.hudak.prco.dto.product.ProductFullDto;
import sk.hudak.prco.exception.HttpErrorPrcoRuntimeException;
import sk.hudak.prco.exception.HttpSocketTimeoutPrcoRuntimeException;
import sk.hudak.prco.manager.UpdateProductDataManager;
import sk.hudak.prco.manager.UpdateProductInfoListener;
import sk.hudak.prco.manager.UpdateStatusInfo;
import sk.hudak.prco.mapper.PrcoOrikaMapper;
import sk.hudak.prco.parser.HtmlParser;
import sk.hudak.prco.service.InternalTxService;
import sk.hudak.prco.task.TaskManager;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static sk.hudak.prco.manager.impl.UpdateProductDataManagerImpl.UpdateProcessResult.ERR_HTML_PARSING_FAILED_404_ERROR;
import static sk.hudak.prco.manager.impl.UpdateProductDataManagerImpl.UpdateProcessResult.ERR_PRODUCT_IS_UNAVAILABLE;
import static sk.hudak.prco.manager.impl.UpdateProductDataManagerImpl.UpdateProcessResult.OK;
import static sk.hudak.prco.utils.ThreadUtils.sleepRandomSafe;
import static sk.hudak.prco.utils.ThreadUtils.sleepSafe;

@Slf4j
@Component
public class UpdateProductDataManagerImpl implements UpdateProductDataManager {

    @Autowired
    private InternalTxService internalTxService;

    @Autowired
    private HtmlParser htmlParser;

    @Autowired
    private TaskManager taskManager;

    @Autowired
    private PrcoOrikaMapper mapper;

    @Override
    public void updateAllProductsDataForAllEshops(UpdateProductInfoListener listener) {
        for (EshopUuid eshopUuid : EshopUuid.values()) {

            updateAllProductsDataForEshop(eshopUuid, listener);
            // kazdy dalsi spusti s 1 sekundovym oneskorenim
            //TODO dat do configu dany parameter
            sleepSafe(3);
        }
    }

    @Override
    public void updateAllProductDataNotInAnyGroup(UpdateProductInfoListener listener) {
        //TODO process listener

        List<ProductFullDto> productsNotInAnyGroup = internalTxService.findProductsNotInAnyGroup();
        if (productsNotInAnyGroup.isEmpty()) {
            log.debug("nothing found for update");
            return;
        }
        updateProductData(productsNotInAnyGroup, listener);
    }

    @Override
    public void updateAllProductsDataInGroup(Long groupId) {
        //TODO bug !!!!!! nech nevracia len tie,  ktore uz boli updatnute

        // ziskam zoznam produktov v danej skupine
        List<ProductFullDto> productsInGroup = internalTxService.findProductsInGroup(groupId, true);
        if (productsInGroup.isEmpty()) {
            log.debug("none products founds in group with id {}", groupId);
            return;
        }
        updateProductData(productsInGroup, UpdateProductInfoListenerAdapter.INSTANCE);
    }

    private void updateProductData(List<ProductFullDto> productsForUpdate, UpdateProductInfoListener listener) {

        // vytvorim mapu, ktore produkty patrie ktoremu eshopu(FIXME urobit na to servis osobiny nech rovno navratova hodnota je mapa)
        Map<EshopUuid, List<Long>> productsInEshop = new EnumMap<>(EshopUuid.class);
        productsForUpdate.forEach(productFullDto -> productsInEshop.put(productFullDto.getEshopUuid(), new ArrayList<>()));
        productsForUpdate.forEach(productFullDto -> productsInEshop.get(productFullDto.getEshopUuid()).add(productFullDto.getId()));


        // iterujem cez jednotlive eshopy a spustam jeden task per eshop
        for (Map.Entry<EshopUuid, List<Long>> eshopUuidListEntry : productsInEshop.entrySet()) {
            final EshopUuid eshopUuid = eshopUuidListEntry.getKey();

            taskManager.submitTask(eshopUuid, () -> {

                taskManager.markTaskAsRunning(eshopUuid);

                boolean finishedWithError = false;

                long countOfProductsAlreadyUpdated = 0;
                long countOfProductsWaitingToBeUpdated = eshopUuidListEntry.getValue().size();

                for (Long productId : eshopUuidListEntry.getValue()) {
                    ProductDetailInfo productDetailInfo = null;

                    try {
                        // nacitam detaily produktu
                        Optional<ProductDetailInfo> productForUpdateOpt = internalTxService.getProductForUpdate(productId);
                        if (!productForUpdateOpt.isPresent()) {

                            countOfProductsAlreadyUpdated++;
                            countOfProductsWaitingToBeUpdated--;
                            listener.onUpdateStatus(new UpdateStatusInfo(countOfProductsWaitingToBeUpdated, countOfProductsAlreadyUpdated, eshopUuid));

                            continue;
                        }
                        productDetailInfo = productForUpdateOpt.get();

                        // updatnem zaznam
                        processUpdate(productDetailInfo);

                        countOfProductsAlreadyUpdated++;
                        countOfProductsWaitingToBeUpdated--;
                        listener.onUpdateStatus(new UpdateStatusInfo(countOfProductsWaitingToBeUpdated, countOfProductsAlreadyUpdated, eshopUuid));

                        if (taskManager.isTaskShouldStopped(eshopUuid)) {
                            taskManager.markTaskAsStopped(eshopUuid);
                            break;
                        }

                        sleepRandomSafe();

                    } catch (Exception e) {
                        log.error("error while updating product data " +
                                "Id: " + productDetailInfo.getId() + " " +
                                "URL: " + productDetailInfo.getUrl(), e);
                        log.debug("marking task for {} to finished with error", eshopUuid);
                        finishedWithError = true;
                        continue;
                    }
                }
                // po prejdeni vsetkych produktov z daneho eshopu oznacim dany task za dokonceny
                taskManager.markTaskAsFinished(eshopUuid, finishedWithError);
            });

            // kazdy dalsi task pre eshop spusti s 5 sekundovym oneskorenim
            sleepSafe(5);
        }
    }

    @Override
    public void updateAllProductsDataForEshop(@NonNull EshopUuid eshopUuid, @NonNull UpdateProductInfoListener listener) {

        taskManager.submitTask(eshopUuid, () -> {

            taskManager.markTaskAsRunning(eshopUuid);

            boolean finishedWithError = false;
            try {
                notifyUpdateListener(eshopUuid, listener);

                Optional<ProductDetailInfo> productForUpdateOpt = internalTxService.getProductForUpdate(eshopUuid, eshopUuid.getOlderThanInHours());
                while (productForUpdateOpt.isPresent()) {
                    ProductDetailInfo productDetailInfo = productForUpdateOpt.get();

                    UpdateProcessResult updateProcessResult = processUpdate(productDetailInfo);

                    if (UpdateProcessResult.ERR_HTML_PARSING_FAILED_404_ERROR.equals(updateProcessResult)) {
//                        finishedWithError = true;
//                        internalTxService.markProductAsUnavailable(productDetailInfo.getId());
                        internalTxService.removeProduct(productDetailInfo.getId());

                    }

                    if (taskManager.isTaskShouldStopped(eshopUuid)) {
                        taskManager.markTaskAsStopped(eshopUuid);
                        break;
                    }
                    sleepRandomSafe();

                    notifyUpdateListener(eshopUuid, listener);
                    productForUpdateOpt = internalTxService.getProductForUpdate(eshopUuid, eshopUuid.getOlderThanInHours());
                }
                log.debug("none product found for update");

            } catch (Exception e) {
                log.error("error while updating product data", e);
                finishedWithError = true;

            } finally {
                taskManager.markTaskAsFinished(eshopUuid, finishedWithError);
            }
        });
    }

    private void notifyUpdateListener(@NonNull EshopUuid eshopUuid, @NonNull UpdateProductInfoListener listener) {
        listener.onUpdateStatus(mapper.map(
                internalTxService.getStatisticForUpdateForEshop(eshopUuid, eshopUuid.getOlderThanInHours()),
                UpdateStatusInfo.class));
    }

    @Override
    public void updateProductData(Long productId) {
        EshopUuid eshopUuid = internalTxService.getEshopForProductId(productId);

        taskManager.submitTask(eshopUuid, () -> {

            taskManager.markTaskAsRunning(eshopUuid);

            boolean finishedWithError = false;
            try {
                Optional<ProductDetailInfo> productForUpdate = internalTxService.getProductForUpdate(productId);
                if (productForUpdate.isPresent()) {

                    processUpdate(productForUpdate.get());

                    if (taskManager.isTaskShouldStopped(eshopUuid)) {
                        taskManager.markTaskAsStopped(eshopUuid);
                    }
                }
                log.debug("none product found for update");

            } catch (Exception e) {
                log.error("error while updating product data", e);
                finishedWithError = true;

            } finally {
                taskManager.markTaskAsFinished(eshopUuid, finishedWithError);
            }
        });

    }

    private UpdateProcessResult processUpdate(ProductDetailInfo productDetailInfo) {
        log.debug("start updating data for product {}", productDetailInfo.getUrl());
        ProductUpdateData updateData;
        try {
            updateData = htmlParser.parseProductUpdateData(productDetailInfo.getUrl());

        } catch (HttpErrorPrcoRuntimeException e) {
            log.error("error while updating product, http status: " + e.getHttpStatus(), e);
            if (404 == e.getHttpStatus()) {
                save404Error(productDetailInfo.getEshopUuid(), productDetailInfo.getUrl(), e.getMessage(), e);
                return ERR_HTML_PARSING_FAILED_404_ERROR;
            }
            throw e;
        } catch (HttpSocketTimeoutPrcoRuntimeException e) {
            saveTimeout4Error(productDetailInfo.getEshopUuid(), productDetailInfo.getUrl(), e.getMessage(), e);
            throw e;
        }
        if (updateData.isProductAvailable()) {
            //FIXME premapovanie cez sk.hudak.prco mapper nie takto rucne, nech mam na jednom mieste tie preklapacky...
            internalTxService.updateProduct(ProductUpdateDataDto.builder()
                    .id(productDetailInfo.getId())
                    .name(updateData.getName())
                    .priceForPackage(updateData.getPriceForPackage())
                    .productAction(updateData.getProductAction())
                    .actionValidity(updateData.getActionValidity())
                    .pictureUrl(updateData.getPictureUrl())
                    .build());
            return OK;
        }

        internalTxService.markProductAsUnavailable(productDetailInfo.getId());
        return ERR_PRODUCT_IS_UNAVAILABLE;
    }

    private void save404Error(EshopUuid eshopUuid, String url, String message, HttpErrorPrcoRuntimeException e) {
        ErrorCreateDto build = ErrorCreateDto.builder()
                .errorType(ErrorType.HTTP_STATUS_ERR)
                .eshopUuid(eshopUuid)
                .url(url)
                .message(message)
                .statusCode("" + 404)
                .fullMsg(ExceptionUtils.getStackTrace(e))
                .build();

        internalTxService.createError(build);
    }

    private void saveTimeout4Error(EshopUuid eshopUuid, String url, String message, HttpSocketTimeoutPrcoRuntimeException e) {
        ErrorCreateDto build = ErrorCreateDto.builder()
                .errorType(ErrorType.TIME_OUT_ERR)
                .eshopUuid(eshopUuid)
                .url(url)
                .message(message)
                .fullMsg(ExceptionUtils.getStackTrace(e))
                .build();

        internalTxService.createError(build);
    }

    enum UpdateProcessResult {
        ERR_HTML_PARSING_FAILED_404_ERROR,
        ERR_PRODUCT_IS_UNAVAILABLE,
        OK,;
    }
}
