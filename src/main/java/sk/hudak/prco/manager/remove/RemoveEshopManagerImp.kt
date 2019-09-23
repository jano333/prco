package sk.hudak.prco.manager.remove

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.service.InternalTxService
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

@Component
class RemoveEshopManagerImp(private val internalTxService: InternalTxService)
    : RemoveEshopManager {

    companion object {
        val log = LoggerFactory.getLogger(RemoveEshopManagerImp::class.java)!!

        private const val MAX_COUNT_TO_DELETE: Long = 50
    }

    private lateinit var executor: ThreadPoolExecutor

    private fun start() {
        executor = ThreadPoolExecutor(5, 5, 0L,
                TimeUnit.MILLISECONDS, LinkedBlockingQueue(1), CustomThreadFactory())

        log.debug("Started thread pool")
    }

    override fun removeAllForEshop(eshopUuid: EshopUuid) {
        start()

        val countOfProductFuture: Future<Long> = executor.submit(removeFromProduct(eshopUuid))
        log.debug("submit product deleting")

        val countOfNewProductFuture: Future<Long> = executor.submit(removeFromNewProduct(eshopUuid))
        log.debug("submit new product deleting")

        val countOfNotInterestedProductFuture: Future<Long> = executor.submit(removeFromNotInterestedProduct(eshopUuid))
        log.debug("submit not interested product deleting")

        val countOfErrorFuture: Future<Long> = executor.submit(removeFromError(eshopUuid))
        log.debug("submit error deleting")

        val countOfWatchDogFuture: Future<Long> = executor.submit(removeWatchDog(eshopUuid))
        log.debug("submit error deleting")

        log.debug("start waiting for all")
        // cakam na ukoncenie komplet odmazania
        val resultProduct = countOfProductFuture.get()
        val resultNewProduct = countOfNewProductFuture.get()
        val resultNotInterested = countOfNotInterestedProductFuture.get()
        val resultError = countOfErrorFuture.get()
        val resultWatchDog = countOfWatchDogFuture.get()

        log.info("delete products: $resultProduct, " +
                "deleted new products: $resultNewProduct, " +
                "deleted not interested products: $resultNotInterested, " +
                "errors: $resultError, " +
                "watch dog: $resultWatchDog")

        log.info("all data(${resultProduct + resultNewProduct + resultNotInterested + resultError + resultWatchDog} )" +
                " for eshop $eshopUuid have been deleted")

        shutdown(1_000)
    }

    private fun removeWatchDog(eshopUuid: EshopUuid): Callable<Long> {
        return Callable {
            var finalCount = 0L

            var countOfProducts = internalTxService.removeWatchDog(eshopUuid, MAX_COUNT_TO_DELETE)
            finalCount += countOfProducts

            while (countOfProducts > 0) {
                log.debug("current count of 'not interested product': $countOfProducts")
                countOfProducts = internalTxService.removeWatchDog(eshopUuid, MAX_COUNT_TO_DELETE)
                finalCount += countOfProducts
            }
            finalCount
        }

    }

    private fun removeFromNotInterestedProduct(eshopUuid: EshopUuid): Callable<Long> {
        return Callable {
            var finalCount = 0L

            var countOfProducts = internalTxService.removeNotInterestedProductsByCount(eshopUuid, MAX_COUNT_TO_DELETE)
            finalCount += countOfProducts

            while (countOfProducts > 0) {
                log.debug("current count of 'not interested product': $countOfProducts")
                countOfProducts = internalTxService.removeNotInterestedProductsByCount(eshopUuid, MAX_COUNT_TO_DELETE)
                finalCount += countOfProducts
            }
            finalCount
        }
    }

    private fun removeFromNewProduct(eshopUuid: EshopUuid): Callable<Long> {
        return Callable {
            var finalCount = 0L
            var countOfProducts = internalTxService.removeNewProductsByCount(eshopUuid, MAX_COUNT_TO_DELETE)
            finalCount += countOfProducts

            while (countOfProducts > 0) {
                log.debug("current count of 'new product': $countOfProducts")
                countOfProducts = internalTxService.removeNewProductsByCount(eshopUuid, MAX_COUNT_TO_DELETE)
                finalCount += countOfProducts
            }
            finalCount
        }
    }

    private fun removeFromError(eshopUuid: EshopUuid): Callable<Long> {
        return Callable {
            var finalCount = 0L
            var countOfProducts = internalTxService.removeErrorsByCount(eshopUuid, MAX_COUNT_TO_DELETE)
            finalCount += countOfProducts

            while (countOfProducts > 0) {
                log.debug("current count of 'error': $countOfProducts")
                countOfProducts = internalTxService.removeErrorsByCount(eshopUuid, MAX_COUNT_TO_DELETE)
                finalCount += countOfProducts
            }
            finalCount
        }
    }

    private fun removeFromProduct(eshopUuid: EshopUuid): Callable<Long> {
        return Callable {
            var finalCount = 0L
            var countOfProducts = internalTxService.removeProductsByCount(eshopUuid, MAX_COUNT_TO_DELETE)
            finalCount += countOfProducts

            while (countOfProducts > 0) {
                log.debug("current count of 'product': $countOfProducts")
                countOfProducts = internalTxService.removeProductsByCount(eshopUuid, MAX_COUNT_TO_DELETE)
                finalCount += countOfProducts
            }
            finalCount
        }
    }

    private fun shutdown(waitInMiliseconds: Long) {
        executor.let {
            it.shutdown()
            try {
                it.awaitTermination(waitInMiliseconds, TimeUnit.MILLISECONDS)
                it.shutdownNow()
                log.debug("actor shut down")
            } catch (e: InterruptedException) {
                it.shutdownNow()
                log.debug("actor shut down")
            }
        }
    }

    internal class CustomThreadFactory : ThreadFactory {

        companion object {
            private val poolNumber = AtomicInteger(1)
        }

        private val group: ThreadGroup
        private val threadNumber = AtomicInteger(1)
        private val namePrefix: String

        init {
            val s = System.getSecurityManager()
            group = if (s != null) {
                s.threadGroup
            } else {
                Thread.currentThread().threadGroup
            }
            namePrefix = "remove-eshop-pool-${poolNumber.getAndIncrement()}-thread-"
        }

        override fun newThread(r: Runnable): Thread {
            val t = Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0)
            if (t.isDaemon)
                t.isDaemon = false
            if (t.priority != Thread.NORM_PRIORITY)
                t.priority = Thread.NORM_PRIORITY
            return t
        }
    }

}