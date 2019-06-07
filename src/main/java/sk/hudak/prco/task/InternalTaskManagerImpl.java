package sk.hudak.prco.task;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import sk.hudak.prco.api.EshopUuid;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@Component
public class InternalTaskManagerImpl implements InternalTaskManager {

    private static final String THREAD_NAME_SUFFIX = "internalTask";

    //TODO pozriet exception handler pre vlakna....

    private Map<EshopUuid, ExecutorService> executors = new EnumMap<>(EshopUuid.class);

    @PostConstruct
    public void init() {
        Arrays.stream(EshopUuid.values()).forEach(eshopUuid ->
                executors.put(eshopUuid, createExecutorServiceForEshop(eshopUuid)));
    }

    @Override
    public Future<Void> processAsync(@NonNull EshopUuid eshopUuid, @NonNull VoidTask task) {
        return internalSubmit(eshopUuid, task);
    }

    @Override
    public <T> Future<T> processAsync(@NonNull EshopUuid eshopUuid, @NonNull Callable<T> task) {
        return internalSubmit(eshopUuid, task);
    }

    private ExecutorService createExecutorServiceForEshop(EshopUuid value) {
        return Executors.newSingleThreadExecutor(runnable -> new Thread(runnable, value.name() + " " + THREAD_NAME_SUFFIX));
    }

    private <T> Future<T> internalSubmit(EshopUuid eshopUuid, Callable<T> task) {
        return executors.get(eshopUuid).submit(task);
    }
}
