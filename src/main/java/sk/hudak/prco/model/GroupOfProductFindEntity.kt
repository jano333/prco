package sk.hudak.prco.model

import lombok.EqualsAndHashCode
import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "GROUP_PRODUCT")
class GroupOfProductFindEntity {

    @EmbeddedId
    private var id: GroupOfProductFindEntityId? = null

    @Column(name = "GROUP_ID", insertable = false, updatable = false)
    var groupId: Long? = null

    @Column(name = "PRODUCT_ID", insertable = false, updatable = false)
    var productId: Long? = null

    @EqualsAndHashCode
    @Embeddable
    internal inner class GroupOfProductFindEntityId : Serializable {

        @Column(name = "GROUP_ID")
        var groupId: Long? = null

        @Column(name = "PRODUCT_ID")
        var productId: Long? = null
    }
}
