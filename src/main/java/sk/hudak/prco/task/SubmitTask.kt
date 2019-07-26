package sk.hudak.prco.task

import sk.hudak.prco.api.EshopUuid

@FunctionalInterface
interface SubmitTask<T, K> {

    @Throws(Exception::class)
    fun doInTask(eshopUuid: EshopUuid, param1: T, param2: K)
}
