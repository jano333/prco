package sk.hudak.prco.model

import sk.hudak.prco.model.core.DbEntity
import javax.persistence.*

@Entity(name = "GROUP_KEYWORDS")
class GroupProductKeywordsEntity : DbEntity() {

    @Id
    @GeneratedValue(generator = "GROUP_KEYWORDS_SEC", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "GROUP_KEYWORDS_SEC", sequenceName = "GROUP_KEYWORDS_SEC", allocationSize = 1)
    override var id: Long? = null

    @ManyToOne(optional = false)
    @JoinColumn(name = "GROUP_ID", nullable = false, updatable = false)
    var group: GroupEntity? = null

    @Column(nullable = false)
    var keyWords: String? = null
}
