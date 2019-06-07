package sk.hudak.prco.task;

import sk.hudak.prco.api.EshopUuid;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface InternalTaskManager {

    Future<Void> processAsync(EshopUuid eshopUuid, VoidTask task);

    <T> Future<T> processAsync(EshopUuid eshopUuid, Callable<T> task);
}
