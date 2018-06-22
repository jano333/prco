package sk.hudak.prco.manager.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.dto.WatchDogAddDto;
import sk.hudak.prco.dto.WatchDogDto;
import sk.hudak.prco.dto.WatchDogNotifyUpdateDto;
import sk.hudak.prco.dto.product.ProductForUpdateData;
import sk.hudak.prco.manager.WatchDogManager;
import sk.hudak.prco.parser.HtmlParser;
import sk.hudak.prco.service.InternalTxService;
import sk.hudak.prco.task.TaskManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static sk.hudak.prco.utils.ThreadUtils.sleepRandomSafe;
import static sk.hudak.prco.utils.ThreadUtils.sleepSafe;

@Slf4j
@Component
public class WatchDogManagerImpl implements WatchDogManager {

    @Autowired
    private InternalTxService internalTxService;

    @Autowired
    private TaskManager taskManager;

    @Autowired
    private HtmlParser parser;

    @Override
    public void startWatching(String productUrl, BigDecimal maxPriceToBeInterestedIn) {
        internalTxService.addNewProductToWatch(new WatchDogAddDto(productUrl, maxPriceToBeInterestedIn));
    }

    @Override
    public void collectAllUpdateAndSendEmail() {
        Map<EshopUuid, List<WatchDogDto>> notificationList = internalTxService.findProductsForWatchDog();
        if (notificationList.isEmpty()) {
            log.debug("nothing to found ");
            return;
        }

        List<WatchDogNotifyUpdateDto> productIdToBeNotified = new ArrayList<>();
        for (Map.Entry<EshopUuid, List<WatchDogDto>> eshopUuidListEntry : notificationList.entrySet()) {
            EshopUuid eshopUuid = eshopUuidListEntry.getKey();
            List<WatchDogDto> watchDogProductsInEshop = eshopUuidListEntry.getValue();

            collect(productIdToBeNotified, eshopUuid, watchDogProductsInEshop);
        }

        // wait util all task are finished
        boolean result = taskManager.isAnyTaskRunning();
        log.debug("is any task running: {}", result);

        while (result) {
            sleepSafe(5);
            result = taskManager.isAnyTaskRunning();
            log.debug("is any task running: {}", result);
        }

        if (!productIdToBeNotified.isEmpty()) {
            log.debug("count of product {}", productIdToBeNotified.size());
            internalTxService.notifyByEmail(productIdToBeNotified);
        } else {
            log.debug("none product to be notified");
        }
    }

    private void collect(List<WatchDogNotifyUpdateDto> productIdToBeNotified, EshopUuid eshopUuid, List<WatchDogDto> products) {
        taskManager.markTaskAsRunning(eshopUuid);

        taskManager.submitTask(eshopUuid, () -> {

            boolean finishedWithError = false;
            try {
                for (WatchDogDto watchDogDto : products) {
                    Optional<ProductForUpdateData> result = Optional.of(parser.parseProductUpdateData(watchDogDto.getProductUrl()));
                    sleepRandomSafe();
                    if (!result.isPresent()) {
                        continue;
                    }
                    ProductForUpdateData productForUpdateData = result.get();
                    // compare price
                    BigDecimal currentPrice = productForUpdateData.getPriceForPackage();
                    BigDecimal watchDogDtoMaxPriceToBeInterestedIn = watchDogDto.getMaxPriceToBeInterestedIn();
                    int i = currentPrice.compareTo(watchDogDtoMaxPriceToBeInterestedIn);
                    if (i < 0) {
                        productIdToBeNotified.add(createWatchDogNotifyUpdateDto(watchDogDto, productForUpdateData));
                        //TODO msg
                        log.debug("adding product ");
                    } else {
                        log.debug("product is not needed to be notify, current/watchdog: {}/{}", currentPrice, watchDogDtoMaxPriceToBeInterestedIn);
                    }
                }


            } catch (Exception e) {
                //TODO error
                log.error("error while updating product data", e);
                finishedWithError = true;

            } finally {
                taskManager.markTaskAsFinished(eshopUuid, finishedWithError);
            }
        });
    }

    private WatchDogNotifyUpdateDto createWatchDogNotifyUpdateDto(WatchDogDto watchDogDto, ProductForUpdateData productForUpdateData) {
        WatchDogNotifyUpdateDto result = new WatchDogNotifyUpdateDto();
        result.setId(watchDogDto.getId());
        result.setEshopUuid(watchDogDto.getEshopUuid());
        result.setProductUrl(watchDogDto.getProductUrl());
        result.setMaxPriceToBeInterestedIn(watchDogDto.getMaxPriceToBeInterestedIn());
        result.setProductName(productForUpdateData.getName());
        result.setCurrentPrice(productForUpdateData.getPriceForPackage());
        return result;
    }
}
