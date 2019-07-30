package sk.hudak.prco.model.core

import java.util.*
import javax.persistence.Column
import javax.persistence.MappedSuperclass

@MappedSuperclass
abstract class DbEntity {

    abstract var id: Long?

    /**
     * Kedy bola entita vlozena do databazy.
     */
    @Column(nullable = false)
    var created: Date? = null

    /**
     * Kedy naposledy nastal updata danej entity
     */
    @Column(nullable = false)
    var updated: Date? = null
}
