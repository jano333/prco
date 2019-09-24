package sk.hudak.prco.reporter

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.Unit
import sk.hudak.prco.dto.GroupIdNameDto
import sk.hudak.prco.dto.product.ProductFullDto
import sk.hudak.prco.service.InternalTxService
import java.math.BigDecimal
import java.text.DecimalFormat


@Component
class FacebookReporterImpl(private val internalTxService: InternalTxService)
    : FacebookReporter {

    companion object {
        val log = LoggerFactory.getLogger(FacebookReporterImpl::class.java)!!
    }

    override fun showReportForGroup(groupId: Long): String {
        val group = internalTxService.getGroupById(groupId)

        //TODO iba updejtnute a dostupne !!!!
        val productsInGroup = internalTxService.findProductsInGroup(groupId, true, EshopUuid.METRO)

        return generateReportForGroup(group, productsInGroup)
    }

    override fun doFullReport(): String {
        val sb = StringBuilder()

        sb.append("For FB(top 5): ").append("\n")
        // pampers biele 0
        sb.append(showReportForGroup(449L)).append("\n")

        // pampers zelene 1
        sb.append(showReportForGroup(545L)).append("\n")
        // pampers biele 1
        sb.append(showReportForGroup(450L)).append("\n")

        // pampers zelene 2
        sb.append(showReportForGroup(546L)).append("\n")
        // pampers biele 2
        sb.append(showReportForGroup(451L)).append("\n")

        // pampers zelene 3
        sb.append(showReportForGroup(547L)).append("\n")
        // pampers biele 3
        sb.append(showReportForGroup(452L)).append("\n")

        // pampers zelene 4
        sb.append(showReportForGroup(1L)).append("\n")
        // pampers biele 4
        sb.append(showReportForGroup(453L)).append("\n")

        // pampers zelene 5
        sb.append(showReportForGroup(321L)).append("\n")
        // pampers biele 5
        sb.append(showReportForGroup(481L)).append("\n")

        // pampers zelene 6
        sb.append(showReportForGroup(513L)).append("\n")


        // nutrilon 1
        sb.append(showReportForGroup(577L)).append("\n")
        // nutrilon 2
        sb.append(showReportForGroup(578L)).append("\n")
        // nutrilon 3
        sb.append(showReportForGroup(579L)).append("\n")
        // nutrilon 4
        sb.append(showReportForGroup(33L)).append("\n")
        // nutrilon 5
        sb.append(showReportForGroup(257L)).append("\n")

        return sb.toString()
    }

    private fun generateReportForGroup(group: GroupIdNameDto, productsInGroup: List<ProductFullDto>): String {
        val sb = StringBuilder()

        sb.append(formatGroupName(group)).append(" (").append(productsInGroup.size).append(" produktov, ${getCountOfEshops(productsInGroup)} eshopov):").append("\n")

        //FIXME co ak nebudem mat az 5 produktov? -> spadne to :-)
        for (i in 0..4) {
            val product = productsInGroup[i]
            sb.append(i + 1).append(". ")

            sb.append(formatPriceForPackage(product.priceForPackage!!)).append("€ ")

            sb.append(formatPriceForUnit(product.priceForUnit!!)).append("€/").append(formatUnitName(product.unit!!)).append(" ")
            sb.append(product.url)
            sb.append("\n")
        }
        return sb.toString()
    }

    private fun getCountOfEshops(productsInGroup: List<ProductFullDto>): String {
        val eshops: MutableSet<EshopUuid> = mutableSetOf()
        productsInGroup.forEach {
            eshops.add(it.eshopUuid!!)
        }
        return eshops.size.toString()
    }

    private fun formatGroupName(group: GroupIdNameDto): String {
        return group.name!!.firstCharacterToUpperCase()
    }

    private fun formatUnitName(unit: Unit): String {
        return when (unit) {
            Unit.KILOGRAM -> "kg"
            Unit.KUS -> "kus"
            Unit.LITER -> "l"
            Unit.METER -> "m"
            else -> ""
        }
    }


    private fun formatPriceForUnit(bigDecimal: BigDecimal): String {
        val replace = DecimalFormat("00.000").format(bigDecimal).replace(".", ",")

        return if (replace.first() == '0') {
            " ${replace.substring(1)}"
        } else {
            replace
        }
    }

    private fun formatPriceForPackage(bigDecimal: BigDecimal): String {
        val replace =  DecimalFormat("00.00").format(bigDecimal).replace(".", ",")
        return if (replace.first() == '0') {
            " ${replace.substring(1)}"
        } else {
            replace
        }
    }

}

interface FacebookReporter {
    fun showReportForGroup(groupId: Long): String

    fun doFullReport(): String
}

fun String.firstCharacterToUpperCase(): String {
    return if (this.length >= 1) {
        val firstCharacter = this.substring(0, 1)
        val rest = this.substring(1);
        firstCharacter.toUpperCase() + rest
    } else {
        this
    }
}
