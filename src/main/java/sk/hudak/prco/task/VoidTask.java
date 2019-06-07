package sk.hudak.prco.task;

import java.util.concurrent.Callable;

public abstract class VoidTask implements Callable<Void> {

    @Override
    public final Void call() throws Exception {
        doInTask();
        return null;
    }

    protected abstract void doInTask() throws Exception;


}
