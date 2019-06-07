package sk.hudak.prco.task.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.task.EshopTaskManager;
import sk.hudak.prco.task.TaskContext;
import sk.hudak.prco.task.TaskStatus;
import sk.hudak.prco.utils.ThreadUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static sk.hudak.prco.utils.ThreadUtils.generateRandomSecondInInterval;

@Slf4j
@Component
public class EshopTaskManagerImpl implements EshopTaskManager {

    private Map<EshopUuid, ExecutorService> executors = new EnumMap<>(EshopUuid.class);

    @Getter
    private Map<EshopUuid, TaskContext> tasks = new ConcurrentHashMap<>(EshopUuid.values().length);

    @PostConstruct
    public void init() {
        Arrays.stream(EshopUuid.values()).forEach(eshopUuid -> {
            executors.put(eshopUuid, createExecutorServiceForEshop(eshopUuid));
            tasks.put(eshopUuid, new TaskContext(TaskStatus.STOPPED));
        });
    }

    @PreDestroy
    public void tearDown() {
        for (EshopUuid eshopUuid : executors.keySet()) {
            executors.get(eshopUuid).shutdownNow();
        }
    }

    @Override
    public Future<?> submitTask(@NonNull EshopUuid eshopUuid, @NonNull Runnable task) {
        log.debug("submitting new task for eshop {}", eshopUuid);
        return executors.get(eshopUuid).submit(task);
    }

    private ExecutorService createExecutorServiceForEshop(EshopUuid value) {
        return Executors.newSingleThreadExecutor(r -> new Thread(r, value.name()));
    }

    @Override
    public boolean isTaskRunning(EshopUuid eshopUuid) {
        return TaskStatus.RUNNING.equals(tasks.get(eshopUuid).getStatus());
    }

    @Override
    public boolean isTaskStopped(EshopUuid eshopUuid) {
        return TaskStatus.STOPPED.equals(tasks.get(eshopUuid));
    }

    @Override
    public boolean isTaskFinished(EshopUuid eshopUuid) {
        TaskStatus other = tasks.get(eshopUuid).getStatus();
        return TaskStatus.FINISHED_OK.equals(other) || TaskStatus.FINISHED_WITH_ERROR.equals(other);
    }

    @Override
    public void markTaskAsShouldStopped(EshopUuid eshopUuid) {
        tasks.put(eshopUuid, new TaskContext(TaskStatus.SHOUD_STOP));
    }

    @Override
    public boolean isTaskShouldStopped(EshopUuid eshopUuid) {
        return TaskStatus.SHOUD_STOP.equals(tasks.get(eshopUuid));
    }

    @Override
    public void markTaskAsRunning(EshopUuid eshopUuid) {
        log.debug("marking task for eshop {} as {}", eshopUuid, TaskStatus.RUNNING);
        tasks.put(eshopUuid, new TaskContext(TaskStatus.RUNNING));
    }

    @Override
    public void markTaskAsFinished(EshopUuid eshopUuid, boolean finishedWithError) {
        log.debug("marking task for eshop {} as {}", eshopUuid, finishedWithError ? TaskStatus.FINISHED_WITH_ERROR : TaskStatus.FINISHED_OK);

        if (finishedWithError) {
            tasks.put(eshopUuid, new TaskContext(TaskStatus.FINISHED_WITH_ERROR));
        } else {
            tasks.put(eshopUuid, new TaskContext(TaskStatus.FINISHED_OK));
        }
    }

    @Override
    public void markTaskAsStopped(EshopUuid eshopUuid) {
        log.debug("marking task for eshop {} as {}", eshopUuid, TaskStatus.STOPPED);
        tasks.put(eshopUuid, new TaskContext(TaskStatus.STOPPED));
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

    @Override
    public  void sleepIfNeeded(@NonNull EshopUuid eshopUuid) {
        TaskContext currentContext = tasks.get(eshopUuid);
        TaskStatus currentStatus = currentContext.getStatus();

        log.debug("task status {}", currentContext);
        if (TaskStatus.STOPPED.equals(currentStatus)) {
            return;
        }
        if (isTaskFinished(eshopUuid)) {
            LocalDateTime lastChanged = currentContext.getLastChanged()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            LocalDateTime now = LocalDateTime.now();

            long secondsBetween = ChronoUnit.SECONDS.between(lastChanged, now);

            int secondInInterval = generateRandomSecondInInterval();

            if (secondsBetween < secondInInterval) {
                ThreadUtils.sleepSafe(secondInInterval);
            }


        }


//        if (isTaskFinished(eshopUuid) && currentContext.getLastChanged().before()) {
//
//        }


    }
}
