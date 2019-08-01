package sk.hudak.prco.service

import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.WatchDogAddCustomDto
import sk.hudak.prco.dto.WatchDogDto
import sk.hudak.prco.dto.WatchDogNotifyUpdateDto

interface WatchDogService {

    fun addNewProductToWatch(addDto: WatchDogAddCustomDto): Long?

    fun findProductsForWatchDog(): Map<EshopUuid, List<WatchDogDto>>

    fun notifyByEmail(toBeNotified: List<WatchDogNotifyUpdateDto>)
}
