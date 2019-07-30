package sk.hudak.prco.dto.error

import sk.hudak.prco.api.ErrorType
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.DtoAble

data class ErrorCreateDto(val eshopUuid: EshopUuid? = null,
                          val errorType: ErrorType? = null,
                          val statusCode: String? = null,
                          var message: String? = null,
                          var fullMsg: String? = null,
                          val url: String? = null,
                          val additionalInfo: String? = null) : DtoAble
