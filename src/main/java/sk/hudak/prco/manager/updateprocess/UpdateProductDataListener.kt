package sk.hudak.prco.manager;

@FunctionalInterface
public interface UpdateProductDataListener {

    void onUpdateStatus(UpdateStatusInfo updateStatusInfo);
}
