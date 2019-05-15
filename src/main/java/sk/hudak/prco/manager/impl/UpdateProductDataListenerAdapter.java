package sk.hudak.prco.manager.impl;

import sk.hudak.prco.manager.UpdateProductDataListener;
import sk.hudak.prco.manager.UpdateStatusInfo;

public class UpdateProductDataListenerAdapter implements UpdateProductDataListener {

    public static UpdateProductDataListener INSTANCE = new UpdateProductDataListenerAdapter();

    @Override
    public void onUpdateStatus(UpdateStatusInfo updateStatusInfo) {
        // do nothing
    }
}
