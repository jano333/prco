package sk.hudak.prco.model

import sk.hudak.prco.model.core.DbEntity
import java.util.*
import javax.persistence.*

@Entity(name = "GROUP_OF_PRODUCT")
class GroupEntity : DbEntity() {

    @Id
    @GeneratedValue(generator = "GROUP_SEC", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "GROUP_SEC", sequenceName = "GROUP_SEC", allocationSize = 1)
    override var id: Long? = null

    @Column(name = "NAME", nullable = false, unique = true)
    var name: String? = null

    // zoznam produktov v danej grupe
    @ManyToMany
    @JoinTable(name = "GROUP_PRODUCT", joinColumns = [JoinColumn(name = "GROUP_ID", referencedColumnName = "ID")], inverseJoinColumns = [JoinColumn(name = "PRODUCT_ID", referencedColumnName = "ID")], uniqueConstraints = [UniqueConstraint(columnNames = ["GROUP_ID", "PRODUCT_ID"])])
    var products: List<ProductEntity> = ArrayList()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GroupEntity

        if (id != other.id) return false
        if (name != other.name) return false
        if (products != other.products) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + products.hashCode()
        return result
    }

}
