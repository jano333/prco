package sk.hudak.prco.manager.export

import org.springframework.beans.factory.annotation.Value
import sk.hudak.prco.service.InternalTxService
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

abstract class AbstractExportImportManagerImpl {

    @Inject
    protected var internalTxService: InternalTxService? = null

    @Value("\${prco.server.export.import.root.dir}")
    protected var sourceFolder: String? = null

    protected var sdfForFileName: SimpleDateFormat
    protected var sdf: SimpleDateFormat

    init {
        this.sdfForFileName = SimpleDateFormat("dd-MM-yyyy_HH_mm_ss")
        this.sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
    }

    protected fun safeToString(obj: Any?): String? {
        if (obj == null) {
            return null
        }
        return if (obj is Date) {
            sdf.format(obj as Date?)
        } else obj.toString()
    }

    companion object {

        protected val NEW_LINE_SEPARATOR = System.lineSeparator()
    }
}
