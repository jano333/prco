package sk.hudak.prco.manager;

@FunctionalInterface
public interface UpdateProductInfoListener {

    void onUpdateStatus(UpdateStatusInfo updateStatusInfo);
}
