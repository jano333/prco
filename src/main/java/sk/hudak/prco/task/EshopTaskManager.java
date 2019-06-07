package sk.hudak.prco.task;

import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.dto.internal.ParsingDataResponse;
import sk.hudak.prco.dto.product.ProductDetailInfo;
import sk.hudak.prco.parser.HtmlParser;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface EshopTaskManager {

    Map<EshopUuid, TaskContext> getTasks();

    Future<?> submitTask(EshopUuid eshopUuid, Runnable task);

    <T> Future<T> submitTask(EshopUuid eshopUuid, Callable<T> task);

    boolean isAnyTaskRunning();

    boolean isTaskRunning(EshopUuid eshopUuid);

    void markTaskAsRunning(EshopUuid eshopUuid);

    boolean isTaskStopped(EshopUuid eshopUuid);

    void markTaskAsStopped(EshopUuid eshopUuid);


    boolean isTaskFinished(EshopUuid eshopUuid);

    void markTaskAsFinished(EshopUuid eshopUuid, boolean finishedWithError);

    boolean isTaskShouldStopped(EshopUuid eshopUuid);

    void markTaskAsShouldStopped(EshopUuid eshopUuid);

    // ------------- NG --------------

    /**
     * synchronne to urobi
     *
     * @param productDetailInfo
     * @param htmlParser
     * @return
     */
    ParsingDataResponse parseOneProductUpdateTask(ProductDetailInfo productDetailInfo, HtmlParser htmlParser);
}
