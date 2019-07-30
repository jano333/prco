package sk.hudak.prco.dto

class EshopProductInfoDto(
        val countOfAllProduct: Long,
        val countOfAlreadyUpdated: Long) : DtoAble {

    override fun toString(): String {
        return "EshopProductInfoDto{" +
                "countOfAllProduct=" + countOfAllProduct +
                ", countOfAlreadyUpdated=" + countOfAlreadyUpdated +
                '}'.toString()
    }
}
