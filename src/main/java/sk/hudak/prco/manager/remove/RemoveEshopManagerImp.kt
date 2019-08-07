package sk.hudak.prco.manager.remove

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.service.InternalTxService
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

@Component
class RemoveEshopManagerImp(private val internalTxService: InternalTxService) : RemoveEshopManager {

    companion object {
        val log = LoggerFactory.getLogger(RemoveEshopManagerImp::class.java)!!

        private const val MAX_COUNT_TO_DELETE: Long = 50
    }

    private lateinit var executor: ThreadPoolExecutor

    private fun start() {
        Executors.defaultThreadFactory()

        executor = ThreadPoolExecutor(3, 3, 0L,
                TimeUnit.MILLISECONDS, LinkedBlockingQueue(1), CustomThreadFactory())

        log.debug("Started thread pool")
    }


    override fun removeAllProductsForEshop(eshopUuid: EshopUuid) {
        start()

        val countOfProdutFuture: Future<Long> = executor.submit(Callable {
            var finalCount = 0L;
            var countOfProducts = internalTxService.removeProductsByCount(eshopUuid, MAX_COUNT_TO_DELETE)
            finalCount += countOfProducts

            while (countOfProducts > 0) {
                log.debug("current count of 'product': $countOfProducts")
                countOfProducts = internalTxService.removeProductsByCount(eshopUuid, MAX_COUNT_TO_DELETE)
                finalCount += countOfProducts
            }
            finalCount
        })
        log.debug("submit product deleting")

        val countOfNewProdutFuture: Future<Long> = executor.submit(Callable {
            var finalCount = 0L;
            var countOfProducts = internalTxService.removeNewProductsByCount(eshopUuid, MAX_COUNT_TO_DELETE)
            finalCount += countOfProducts

            while (countOfProducts > 0) {
                log.debug("current count of 'new product': $countOfProducts")
                countOfProducts = internalTxService.removeNewProductsByCount(eshopUuid, MAX_COUNT_TO_DELETE)
                finalCount += countOfProducts
            }
            finalCount
        })
        log.debug("submit new product deleting")

        val countOfNotInteredtedProductFuture: Future<Long> = executor.submit(Callable {
            var finalCount = 0L;

            var countOfProducts = internalTxService.removeNotInterestedProductsByCount(eshopUuid, MAX_COUNT_TO_DELETE)
            finalCount += countOfProducts

            while (countOfProducts > 0) {
                log.debug("current count of 'not interested product': $countOfProducts")
                countOfProducts = internalTxService.removeNotInterestedProductsByCount(eshopUuid, MAX_COUNT_TO_DELETE)
                finalCount += countOfProducts
            }
            finalCount
        })
        log.debug("submit not interested product deleting")

        log.debug("start waiting for all")
        // cakam na ukoncenie komplet odmazania
        val resultProduct = countOfProdutFuture.get()
        val resultNewProduct = countOfNewProdutFuture.get()
        val resultNotInterested = countOfNotInteredtedProductFuture.get()

        log.info("delete products: $resultProduct, deleted new products: $resultNewProduct, deleted not interested products: $resultNotInterested")
        log.info("all data for eshop $eshopUuid have been deleted")

        shutdown(1_000)
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