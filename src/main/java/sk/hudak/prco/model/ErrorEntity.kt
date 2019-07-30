package sk.hudak.prco.model

import sk.hudak.prco.api.ErrorType
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.model.core.DbEntity
import java.util.*
import javax.persistence.*

@Entity(name = "ERROR")
class ErrorEntity : DbEntity() {

    @Id
    @GeneratedValue(generator = "ERROR_SEC", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "ERROR_SEC", sequenceName = "ERROR_SEC", allocationSize = 1)
    override var id: Long? = null

    @Enumerated(EnumType.STRING)
    var eshopUuid: EshopUuid? = null

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var errorType: ErrorType? = null

    var statusCode: String? = null

    var message: String? = null

    @Column(length = 4000)
    var fullMsg: String? = null

    var url: String? = null

    var additionalInfo: String? = null

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as ErrorEntity?
        return id == that!!.id &&
                eshopUuid == that.eshopUuid &&
                errorType === that.errorType &&
                statusCode == that.statusCode &&
                message == that.message &&
                fullMsg == that.fullMsg &&
                url == that.url &&
                additionalInfo == that.additionalInfo
    }

    override fun hashCode(): Int {
        return Objects.hash(id, eshopUuid, errorType, statusCode, message, fullMsg, url, additionalInfo)
    }
}
