package sk.hudak.prco.task;

import lombok.Getter;
import sk.hudak.prco.api.EshopUuid;

@Getter
public abstract class SubmitTask<T, K> {

    public abstract void doInTask(EshopUuid eshopUuid, T param1, K param2) throws Exception;
}
