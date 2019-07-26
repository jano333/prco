package sk.hudak.prco.task;

import lombok.NonNull;
import sk.hudak.prco.api.EshopUuid;

import java.util.Map;
import java.util.concurrent.Future;

public interface EshopTaskManager {

    Map<EshopUuid, TaskContext> getTasks();

    Future<?> submitTask(EshopUuid eshopUuid, Runnable task);

    boolean isAnyTaskRunning();

    boolean isTaskRunning(EshopUuid eshopUuid);

    void markTaskAsRunning(EshopUuid eshopUuid);

    boolean isTaskStopped(EshopUuid eshopUuid);

    void markTaskAsStopped(EshopUuid eshopUuid);


    boolean isTaskFinished(EshopUuid eshopUuid);

    void markTaskAsFinished(EshopUuid eshopUuid, boolean finishedWithError);

    boolean isTaskShouldStopped(EshopUuid eshopUuid);

    void markTaskAsShouldStopped(EshopUuid eshopUuid);

    void sleepIfNeeded(@NonNull EshopUuid eshopUuid);
}
