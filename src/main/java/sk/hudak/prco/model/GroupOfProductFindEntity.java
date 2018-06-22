package sk.hudak.prco.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "GROUP_PRODUCT")
public class GroupOfProductFindEntity {

    @EmbeddedId
    private GroupOfProductFindEntityId id;

    @Column(name = "GROUP_ID", insertable = false, updatable = false)
    private Long groupId;

    @Column(name = "PRODUCT_ID", insertable = false, updatable = false)
    private Long productId;

    @Getter
    @Setter
    @EqualsAndHashCode
    @Embeddable
    class GroupOfProductFindEntityId implements Serializable {

        @Column(name = "GROUP_ID")
        private Long groupId;

        @Column(name = "PRODUCT_ID")
        private Long productId;
    }
}
