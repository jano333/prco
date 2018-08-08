package sk.hudak.prco.task;

import sk.hudak.prco.api.EshopUuid;

@FunctionalInterface
public interface SubmitTask<T, K> {

    void doInTask(EshopUuid eshopUuid, T param1, K param2) throws Exception;
}
