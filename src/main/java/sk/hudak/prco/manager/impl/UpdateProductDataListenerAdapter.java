package sk.hudak.prco.manager.impl;

import lombok.extern.slf4j.Slf4j;
import sk.hudak.prco.manager.UpdateProductDataListener;
import sk.hudak.prco.manager.UpdateStatusInfo;

@Slf4j
public class UpdateProductDataListenerAdapter implements UpdateProductDataListener {

    public static UpdateProductDataListener INSTANCE = new UpdateProductDataListenerAdapter();

    public static UpdateProductDataListener LOG_INSTANCE = updateStatusInfo -> log.debug(updateStatusInfo.toString());

    @Override
    public void onUpdateStatus(UpdateStatusInfo updateStatusInfo) {
        // do nothing
    }
}
