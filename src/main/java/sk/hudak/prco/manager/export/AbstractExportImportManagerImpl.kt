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

    protected val sdfForFileName: SimpleDateFormat = SimpleDateFormat("dd-MM-yyyy_HH_mm_ss")
    protected val sdf: SimpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")

    companion object {
        @JvmStatic
        protected val NEW_LINE_SEPARATOR: String = System.lineSeparator()
    }

    protected fun safeToString(obj: Any?): String? {
        if (obj == null) {
            return null
        }
        return if (obj is Date) {
            sdf.format(obj as Date?)
        } else obj.toString()
    }
}
