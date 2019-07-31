package sk.hudak.prco.service;

import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.dto.WatchDogAddCustomDto;
import sk.hudak.prco.dto.WatchDogDto;
import sk.hudak.prco.dto.WatchDogNotifyUpdateDto;

import java.util.List;
import java.util.Map;

public interface WatchDogService {

    Long addNewProductToWatch(WatchDogAddCustomDto addDto);

    Map<EshopUuid,List<WatchDogDto>> findProductsForWatchDog();

    void notifyByEmail(List<WatchDogNotifyUpdateDto> toBeNotified);
}
