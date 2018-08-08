package sk.hudak.prco.manager.impl;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.dto.ProductUpdateData;
import sk.hudak.prco.dto.internal.ProductForUpdateData;
import sk.hudak.prco.dto.product.ProductDetailInfo;
import sk.hudak.prco.dto.product.ProductFullDto;
import sk.hudak.prco.exception.HttpErrorPrcoRuntimeException;
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
            sleepSafe(5);
        }
    }

    @Override
    public void updateAllProductsDataInGroup(Long groupId) {
        //TODO bug !!!!!! nech nevracia tie ktore uz boli updatnute

        // ziskam zoznam produktov v danej skupine
        List<ProductFullDto> productsInGroup = internalTxService.findProductsInGroup(groupId/*, eshopsToSkip*/);
//        List<ProductFullDto> productsInGroup = internalTxService.findProductsInGroupForUpdateOnly(groupId);
        if (productsInGroup.isEmpty()) {
            log.debug("none products founds in group with id {}", groupId);
            return;
        }

        // vytvorim mapu, ktore produkty patrie ktoremu eshopu(FIXME urobit na to servis osobiny nech rovno navratova hodnota je mapa)
        Map<EshopUuid, List<Long>> productsInEshop = new EnumMap<>(EshopUuid.class);
        productsInGroup.forEach(productFullDto -> productsInEshop.put(productFullDto.getEshopUuid(), new ArrayList<>()));
        productsInGroup.forEach(productFullDto -> productsInEshop.get(productFullDto.getEshopUuid()).add(productFullDto.getId()));

        // iterujem cez jednotlive eshopy a spustam jeden task per eshop
        for (Map.Entry<EshopUuid, List<Long>> eshopUuidListEntry : productsInEshop.entrySet()) {
            final EshopUuid eshopUuid = eshopUuidListEntry.getKey();

            taskManager.submitTask(eshopUuid, () -> {

                taskManager.markTaskAsRunning(eshopUuid);

                boolean finishedWithError = false;

                for (Long productId : eshopUuidListEntry.getValue()) {
                    ProductDetailInfo productDetailInfo = null;
                    try {
                        // nacitam detaily produktu
                        Optional<ProductDetailInfo> productForUpdateOpt = internalTxService.findProductForUpdate(productId);
                        if (!productForUpdateOpt.isPresent()) {
                            continue;
                        }
                        productDetailInfo = productForUpdateOpt.get();

                        // updatnem zaznamy
                        processUpdate(productDetailInfo);

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
    public void updateAllProductsDataForEshop(EshopUuid eshopUuid) {
        updateAllProductsDataForEshop(eshopUuid, UpdateProductInfoListenerAdapter.INSTANCE);
    }

    @Override
    public void updateAllProductsDataForEshop(@NonNull EshopUuid eshopUuid, @NonNull UpdateProductInfoListener listener) {

        taskManager.submitTask(eshopUuid, () -> {

            taskManager.markTaskAsRunning(eshopUuid);

            boolean finishedWithError = false;
            try {
                notifyUpdateListener(eshopUuid, listener);

                Optional<ProductDetailInfo> productForUpdateOpt = internalTxService.findProductForUpdate(eshopUuid, eshopUuid.getOlderThanInHours());
                while (productForUpdateOpt.isPresent()) {
                    ProductDetailInfo productDetailInfo = productForUpdateOpt.get();

                    UpdateProcessResult updateProcessResult = processUpdate(productDetailInfo);

                    if (UpdateProcessResult.ERR_HTML_PARSING_FAILED_404_ERROR.equals(updateProcessResult)) {
                        if (EshopUuid.TESCO.equals(eshopUuid)) {
                            finishedWithError = true;
                            internalTxService.markProductAsUnavailable(productDetailInfo.getId());
                        } else {
                            //FIXME inak vymysliet
//                            throw new HttpErrorPrcoRuntimeException(404, "http 404");
                            finishedWithError = true;
                            internalTxService.markProductAsUnavailable(productDetailInfo.getId());
                        }

                    }

                    if (taskManager.isTaskShouldStopped(eshopUuid)) {
                        taskManager.markTaskAsStopped(eshopUuid);
                        break;
                    }
                    sleepRandomSafe();

                    notifyUpdateListener(eshopUuid, listener);
                    productForUpdateOpt = internalTxService.findProductForUpdate(eshopUuid, eshopUuid.getOlderThanInHours());
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
        listener.updateStatusInfo(mapper.map(
                internalTxService.getStatisticForUpdateForEshop(eshopUuid, eshopUuid.getOlderThanInHours()),
                UpdateStatusInfo.class));
    }

    @Override
    public void updateProductData(Long productId) {
        EshopUuid eshopUuid = internalTxService.findEshopForProductId(productId);

        taskManager.submitTask(eshopUuid, () -> {

            taskManager.markTaskAsRunning(eshopUuid);

            boolean finishedWithError = false;
            try {
                Optional<ProductDetailInfo> productForUpdate = internalTxService.findProductForUpdate(productId);
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
        ProductForUpdateData updateData;
        try {
            updateData = htmlParser.parseProductUpdateData(productDetailInfo.getUrl());

        } catch (HttpErrorPrcoRuntimeException e) {
            log.error("error while updating", e);
            if (404 == e.getHttpStatus()) {
                return ERR_HTML_PARSING_FAILED_404_ERROR;
            }
            throw e;
        }
        if (updateData.isProductAvailable()) {
            //FIXME premapovanie cez sk.hudak.prco mapper nie takto rucne, nech mam na jednom mieste tie preklapacky...
            internalTxService.updateProductData(ProductUpdateData.builder()
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

    enum UpdateProcessResult {
        ERR_HTML_PARSING_FAILED_404_ERROR,
        ERR_PRODUCT_IS_UNAVAILABLE,
        OK,;
    }
}
