package sk.hudak.prco.task.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.task.SubmitTask;
import sk.hudak.prco.task.TaskManager;
import sk.hudak.prco.task.TaskStatus;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@Component
public class TaskManagerImpl implements TaskManager {

    private Map<EshopUuid, ExecutorService> executors = new EnumMap<>(EshopUuid.class);

    @Getter
    private Map<EshopUuid, TaskStatus> tasks = new ConcurrentHashMap<>(EshopUuid.values().length);

    @PostConstruct
    public void init() {
        for (EshopUuid eshopUuid : EshopUuid.values()) {
            executors.put(eshopUuid, createThreadForEshop(eshopUuid));
            tasks.put(eshopUuid, TaskStatus.STOPPED);
        }
    }

    @PreDestroy
    public void tearDown() {
        for (EshopUuid eshopUuid : executors.keySet()) {
            executors.get(eshopUuid).shutdownNow();
        }
    }

    @Override
    public Future<?> submitTask(@NonNull EshopUuid eshopUuid, @NonNull Runnable task) {
        return executors.get(eshopUuid).submit(task);
    }

    @Override
    public <T, K> void submitTask(SubmitTask<T, K> internalTask, EshopUuid eshopUuid, T param1, K param2) {

        submitTask(eshopUuid, () -> {

            markTaskAsRunning(eshopUuid);

            boolean finishedWithError = false;
            try {
                internalTask.doInTask(eshopUuid, param1, param2);

            } catch (Exception e) {
                log.error("error while executing task", e);
                finishedWithError = true;

            } finally {
                markTaskAsFinished(eshopUuid, finishedWithError);
            }
        });
    }

    private ExecutorService createThreadForEshop(EshopUuid value) {
        return Executors.newSingleThreadExecutor(r -> new Thread(r, value.name()));
    }

    @Override
    public boolean isTaskRunning(EshopUuid eshopUuid) {
        return TaskStatus.RUNNING.equals(tasks.get(eshopUuid));
    }

    @Override
    public boolean isTaskStopped(EshopUuid eshopUuid) {
        return TaskStatus.STOPPED.equals(tasks.get(eshopUuid));
    }

    @Override
    public boolean isTaskFinished(EshopUuid eshopUuid) {
        return TaskStatus.FINISHED.equals(tasks.get(eshopUuid));
    }

    @Override
    public void markTaskAsShouldStopped(EshopUuid eshopUuid) {
        tasks.put(eshopUuid, TaskStatus.SHOUD_STOP);
    }

    @Override
    public boolean isTaskShouldStopped(EshopUuid eshopUuid) {
        return TaskStatus.SHOUD_STOP.equals(tasks.get(eshopUuid));
    }

    @Override
    public void markTaskAsRunning(EshopUuid eshopUuid) {
        log.debug("marking task for eshop {} as {}", eshopUuid, TaskStatus.RUNNING);
        tasks.put(eshopUuid, TaskStatus.RUNNING);
    }

    @Override
    public void markTaskAsFinished(EshopUuid eshopUuid, boolean finishedWithError) {
        log.debug("marking task for eshop {} as {}, finished with error: {}", eshopUuid, TaskStatus.FINISHED, finishedWithError);
        tasks.put(eshopUuid, TaskStatus.FINISHED);
    }

    @Override
    public void markTaskAsStopped(EshopUuid eshopUuid) {
        log.debug("marking task for eshop {} as {}", eshopUuid, TaskStatus.STOPPED);
        tasks.put(eshopUuid, TaskStatus.STOPPED);
    }

    @Override
    public boolean isAnyTaskRunning() {
        for (EshopUuid eshopUuid : EshopUuid.values()) {
            if (isTaskRunning(eshopUuid)) {
                return true;
            }
        }
        return false;
    }
}
