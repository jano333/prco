package sk.hudak.prco.task;

import sk.hudak.prco.api.EshopUuid;

import java.util.Map;
import java.util.concurrent.Future;

public interface TaskManager {

    Map<EshopUuid, TaskStatus> getTasks();

    Future<?> submitTask(EshopUuid eshopUuid, Runnable task);

    <T, K> void submitTask(SubmitTask<T, K> internalTask, EshopUuid eshopUuid, T param1, K param2);

    boolean isAnyTaskRunning();

    boolean isTaskRunning(EshopUuid eshopUuid);

    void markTaskAsRunning(EshopUuid eshopUuid);

    boolean isTaskStopped(EshopUuid eshopUuid);

    void markTaskAsStopped(EshopUuid eshopUuid);


    boolean isTaskFinished(EshopUuid eshopUuid);

    void markTaskAsFinished(EshopUuid eshopUuid, boolean finishedWithError);

    boolean isTaskShouldStopped(EshopUuid eshopUuid);

    void markTaskAsShouldStopped(EshopUuid eshopUuid);
}
