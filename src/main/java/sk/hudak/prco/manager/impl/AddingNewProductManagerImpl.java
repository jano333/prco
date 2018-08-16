package sk.hudak.prco.manager.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sk.hudak.prco.api.ErrorType;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.dto.error.ErrorCreateDto;
import sk.hudak.prco.dto.internal.NewProductInfo;
import sk.hudak.prco.dto.newproduct.NewProductCreateDto;
import sk.hudak.prco.manager.AddingNewProductManager;
import sk.hudak.prco.mapper.PrcoOrikaMapper;
import sk.hudak.prco.parser.EshopProductsParser;
import sk.hudak.prco.parser.EshopUuidParser;
import sk.hudak.prco.parser.HtmlParser;
import sk.hudak.prco.service.InternalTxService;
import sk.hudak.prco.task.TaskManager;
import sk.hudak.prco.utils.ThreadUtils;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static sk.hudak.prco.utils.Validate.notNull;

@Slf4j
@Component
public class AddingNewProductManagerImpl implements AddingNewProductManager {

    private  static final String ERROR_WHILE_CREATING_NEW_PRODUCT = "error while creating new product";

    @Autowired
    private InternalTxService internalTxService;

    @Autowired
    private PrcoOrikaMapper mapper;

    @Autowired
    private HtmlParser htmlParser;

    @Autowired
    private TaskManager taskManager;

    @Autowired
    private EshopUuidParser eshopUuidParser;

    @Autowired
    private List<EshopProductsParser> productsParsers;

    @Override
    public void addNewProductsByKeywordForAllEshops(String searchKeyword) {
        notNull(searchKeyword, "searchKeyWord");

        for (EshopUuid eshopUuid : EshopUuid.values()) {

            // ak neexistuje parser pre dany eshop, tak len zaloguj a chod na dalsi
            if (!existParserFor(eshopUuid)) {
                log.warn("for eshop {} none parser found", eshopUuid);
                continue;
            }
            // spusti stahovanie pre dalsi
            addNewProductsByKeywordForEshop(eshopUuid, searchKeyword);

            // kazdy dalsi spusti s 1 sekundovym oneskorenim
            ThreadUtils.sleepSafe(1);
        }
    }

    @Override
    public void addNewProductsByKeywordForEshop(EshopUuid eshopUuid, String searchKeyWord) {
        log.debug(">> addNewProductsByKeywordForEshop eshop: {}, searchKeyWord: {}", eshopUuid, searchKeyWord);
        taskManager.submitTask(eshopUuid, () -> {

            taskManager.markTaskAsRunning(eshopUuid);
            boolean finishedWithError = false;

            try {
                // vyparsujem vsetky url-cky produktov, ktore sa najdu na strankach(prechadza aj pageovane stranky)
                List<String> urlList = htmlParser.searchProductUrls(eshopUuid, searchKeyWord);

                createNewProducts(eshopUuid, urlList);

            } catch (Exception e) {
                log.error(ERROR_WHILE_CREATING_NEW_PRODUCT, e);
                finishedWithError = true;

            } finally {
                taskManager.markTaskAsFinished(eshopUuid, finishedWithError);
            }
        });
        log.debug("<< addNewProductsByKeywordForEshop eshop {}, searchKeyWord {}", eshopUuid, searchKeyWord);
    }

    @Override
    public void addNewProductByUrl(String productUrl) {
        log.debug(">> addNewProductByUrl productUrl: {}", productUrl);

        final EshopUuid eshopUuid = eshopUuidParser.parseEshopUuid(productUrl);

        taskManager.submitTask(eshopUuid, () -> {

            taskManager.markTaskAsRunning(eshopUuid);
            boolean finishedWithError = false;
            try {
                // parsujem
                NewProductInfo newProductInfo = htmlParser.parseNewProductInfo(productUrl);
                // preklopim a pridavam do DB
                internalTxService.createNewProduct(mapper.map(newProductInfo, NewProductCreateDto.class));

            } catch (Exception e) {
                log.error(ERROR_WHILE_CREATING_NEW_PRODUCT, e);
                finishedWithError = true;

            } finally {
                taskManager.markTaskAsFinished(eshopUuid, finishedWithError);
            }
        });
        log.debug("<< addNewProductByUrl productUrl: {}", productUrl);

    }

    @Override
    public void addNewProductsByUrl(String... productsUrl) {
        //TODO spojit s predoslou metodou a urobit delegovanie... do tejto nech to je na jednom mieste

        notNull(productsUrl, "productsUrl");

        int countOfUrls = productsUrl.length;
        log.debug(">> addNewProductsByUrl count of URLs: {}", countOfUrls);

        // roztriedim URL podla typu eshopu
        Map<EshopUuid, List<String>> eshopUrls = new EnumMap<>(EshopUuid.class);
        for (String productUrl : productsUrl) {
            eshopUrls.computeIfAbsent(eshopUuidParser.parseEshopUuid(productUrl),
                    eshopUuid -> new ArrayList<>())
                    .add(productUrl);
        }

        for (EshopUuid eshopUuid : eshopUrls.keySet()) {

            taskManager.submitTask(eshopUuid, () -> {

                taskManager.markTaskAsRunning(eshopUuid);
                boolean finishedWithError = false;

                try {
                    List<String> urlList = eshopUrls.get(eshopUuid);

                    createNewProducts(eshopUuid, urlList);

                } catch (Exception e) {
                    log.error("error while creating new product", e);
                    finishedWithError = true;

                } finally {
                    taskManager.markTaskAsFinished(eshopUuid, finishedWithError);
                }
            });
        }
        log.debug("<< addNewProductsByUrl count of URLs: {}", countOfUrls);
    }

    private void createNewProducts(EshopUuid eshopUuid, List<String> urlList) {
        int allUrlCount = urlList.size();

        for (int currentUrlIndex = 0; currentUrlIndex < allUrlCount; currentUrlIndex++) {
            if (taskManager.isTaskShouldStopped(eshopUuid)) {
                taskManager.markTaskAsStopped(eshopUuid);
                break;
            }
            log.debug("starting {} of {}", currentUrlIndex + 1, allUrlCount);
            String productUrl = urlList.get(currentUrlIndex);

            // ak uz exituje, tak vynechavam
            log.debug("checking existence of product URL {}", productUrl);
            if (internalTxService.existProductWithURL(productUrl)) {
                log.debug("already added -> skipping");
                continue;
            }

            // parsujem
            NewProductInfo newProductInfo = htmlParser.parseNewProductInfo(productUrl);
            if (newProductInfo.getUnit() == null) {
                logErrorParsingUnit(productUrl, newProductInfo.getName());
            }

            // preklopim a pridavam do DB
            internalTxService.createNewProduct(mapper.map(newProductInfo, NewProductCreateDto.class));

            // sleep pre dalsou iteraciou
            //TODO fix na zaklade nastavenia daneho eshopu.... dave od to delay
            ThreadUtils.sleepRandomSafe();
        }
    }

    private void logErrorParsingUnit(String productUrl, String productName) {
        internalTxService.createError(ErrorCreateDto.builder()
                .errorType(ErrorType.PARSING_PRODUCT_INFO_ERR)
                .url(productUrl)
                .additionalInfo(productName)
                .build());

    }

    private boolean existParserFor(EshopUuid eshopUuid) {
        //FIXME cez lamba a findFirst
        for (EshopProductsParser productsParser : productsParsers) {
            if (eshopUuid.equals(productsParser.getEshopUuid())) {
                return true;
            }
        }
        return false;
    }

}
