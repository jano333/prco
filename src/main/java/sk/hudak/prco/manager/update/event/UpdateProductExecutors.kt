package sk.hudak.prco.manager.update.event

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.events.InternalThreadFactory
import java.util.concurrent.ExecutorService
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import javax.annotation.PreDestroy

@Component
class UpdateProductExecutors {

    val handlerTaskExecutor: ExecutorService = createInternalThreadExecutor("update-handler-task", 10)
    val internalServiceExecutor: ExecutorService = createInternalThreadExecutor("update-internal-service", 20)
    val htmlParserExecutor: ExecutorService = createInternalThreadExecutor("update-html-parser", 10)

    companion object {
        private val LOG = LoggerFactory.getLogger(UpdateProductExecutors::class.java)!!
    }

    @PreDestroy
    fun shutdownNowAllExecutors() {
        LOG.trace("start shutting down of all executors")
        handlerTaskExecutor.shutdownNow()
        internalServiceExecutor.shutdownNow()
        htmlParserExecutor.shutdownNow()
        LOG.debug("shutting down of all executors completed")
    }

    //FIXME spolocna metoda aj pre add executor, dat do bazovej a nech add a update executor extenduju, pozri aj inicializaciu...
    private fun createInternalThreadExecutor(prefix: String, nThreads: Int): ExecutorService {
        return ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
                LinkedBlockingQueue(),
                InternalThreadFactory(prefix))
    }
}
