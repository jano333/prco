package sk.hudak.prco.manager.impl;

import sk.hudak.prco.manager.UpdateProductInfoListener;
import sk.hudak.prco.manager.UpdateStatusInfo;

public class UpdateProductInfoListenerAdapter implements UpdateProductInfoListener {

    public static UpdateProductInfoListener INSTANCE = new UpdateProductInfoListenerAdapter();

    @Override
    public void onUpdateStatus(UpdateStatusInfo updateStatusInfo) {
        // do nothing
    }
}
