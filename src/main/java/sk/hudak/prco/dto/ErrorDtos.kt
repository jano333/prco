package sk.hudak.prco.dto

import sk.hudak.prco.api.ErrorType
import sk.hudak.prco.api.EshopUuid
import java.text.SimpleDateFormat
import java.util.*

data class ErrorCreateDto(val eshopUuid: EshopUuid,
                          val errorType: ErrorType,
                          val statusCode: String? = null,
                          var message: String? = null,
                          var fullMsg: String? = null,
                          val url: String? = null,
                          val additionalInfo: String? = null) : DtoAble

//TODO prorobit na data class
class ErrorFindFilterDto : DtoAble {

    companion object {
        const val DEFAULT_COUNT_LIMIT = 50
    }

    var limit = DEFAULT_COUNT_LIMIT
    var errorTypes: Array<ErrorType>? = null
    var errorTypesToSkip: Array<ErrorType>? = null
    var statusCodes: Array<String>? = null
    var statusCodesToSkip: Array<String>? = null

    override fun toString(): String {
        return "ErrorFindFilterDto(limit=$limit, " +
                "errorTypes=${Arrays.toString(errorTypes)}, " +
                "errorTypesToSkip=${Arrays.toString(errorTypesToSkip)}," +
                " statusCodes=${Arrays.toString(statusCodes)}, " +
                "statusCodesToSkip=${Arrays.toString(statusCodesToSkip)})"
    }
}

//TODO prorobit na data class
class ErrorListDto : DtoAble {

    var id: Long? = null
    var eshopUuid: EshopUuid? = null
    var errorType: ErrorType? = null
    var statusCode: String? = null
    var message: String? = null
    var fullMsg: String? = null
    var url: String? = null
    var additionalInfo: String? = null
    var updated: Date? = null

    fun customToString(): String {
        return StringBuilder().append(eshopUuid).append(" ")
                .append("[").append(id).append("] ")
                .append(formatDate(updated)).append(" ")
                .append(errorType).append(" ")
                .append("status: ").append(statusCode).append(" ")
                .append("message: ").append(message).append(" ")
                //                    "fullMessage " + sb.append(getFullMsg() + " " +
                .append("url ").append(url)
                .toString()
    }

    private fun formatDate(date: Date?): String {
        return if (date == null) {
            ""
        } else SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(date)
    }

    override fun toString(): String {
        return "ErrorListDto(id=$id, " +
                "eshopUuid=$eshopUuid, " +
                "errorType=$errorType, " +
                "statusCode=$statusCode, " +
                "message=$message, " +
                "fullMsg=$fullMsg, " +
                "url=$url, " +
                "additionalInfo=$additionalInfo, " +
                "updated=$updated)"
    }


}

