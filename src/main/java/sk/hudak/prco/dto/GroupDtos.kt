package sk.hudak.prco.dto

import sk.hudak.prco.api.EshopUuid
import java.util.*

class GroupCreateDto : DtoAble {

    // nazov (povinne)
    lateinit var name: String

    // zoznam produktov v danej grupe (nepovinne)
    var productIds: List<Long> = ArrayList()

    constructor() {}

    constructor(name: String) {
        this.name = name
    }

    constructor(name: String, productIds: List<Long>) {
        this.name = name
        this.productIds = productIds
    }
}

class GroupIdNameDto : IdNameDto()

class GroupListDto : DtoAble {
    var id: Long? = null
    var name: String? = null

    override fun toString(): String {
        return "GroupListDto(id=$id, name=$name)"
    }
}

class GroupListExtendedDto : DtoAble {
    var id: Long? = null
    var name: String? = null
    var countOfProduct: Long? = null
    var countOfProductInEshop: Map<EshopUuid, Long> = EnumMap(EshopUuid::class.java)

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as GroupListExtendedDto?
        return id == that!!.id &&
                name == that.name &&
                countOfProduct == that.countOfProduct &&
                countOfProductInEshop == that.countOfProductInEshop
    }

    override fun hashCode(): Int {
        return Objects.hash(id, name, countOfProduct, countOfProductInEshop)
    }

    override fun toString(): String {
        return "GroupListExtendedDto(id=$id, name=$name, countOfProduct=$countOfProduct, countOfProductInEshop=$countOfProductInEshop)"
    }
}

data class GroupProductKeywordsCreateDto(var groupId: Long,
                                         var keyWords: List<String>) : DtoAble

data class GroupProductKeywordsFullDto(var groupIdNameDto: GroupIdNameDto,
                                       var keyWords: List<Array<String>>) : DtoAble {

    override fun toString(): String {
        val sb = StringBuilder()
                .append(this.javaClass.name).append("[\n")
                .append(" groupIdNameDto=[").append(groupIdNameDto).append("]\n")
                .append(" keyWords=[").append("\n")
        keyWords!!.stream().forEach { value -> sb.append("   " + Arrays.asList(*value)).append("\n") }
        sb.append(" ]").append("\n")
        sb.append("]")
        return sb.toString()
    }
}

class GroupUpdateDto : DtoAble {
    var id: Long? = null
    var name: String? = null

    constructor() {}

    constructor(id: Long?, name: String) {
        this.id = id
        this.name = name
    }
}

class GroupFilterDto : DtoAble {

    var ids: Array<Long>? = null
        private set

    var name: String? = null
        private set

    var eshopOnly: EshopUuid? = null
        private set

    var eshopsToSkip: Array<EshopUuid>? = null
        private set

    constructor()

    constructor(id: Long) {
        this.ids = arrayOf(id)
    }

    constructor(name: String) {
        this.name = name
    }

    constructor(name: String, ids: Array<Long>) {
        this.name = name
        this.ids = ids
    }

    constructor(eshopOnly: EshopUuid, ids: Array<Long>) {
        this.eshopOnly = eshopOnly
        this.ids = ids
    }

    constructor(id: Long, eshopsToSkip: Array<EshopUuid>) {
        this.ids = arrayOf(id)
        this.eshopsToSkip = eshopsToSkip
    }
}