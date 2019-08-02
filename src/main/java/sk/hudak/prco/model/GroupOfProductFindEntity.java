package sk.hudak.prco.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "GROUP_PRODUCT")
public class GroupOfProductFindEntity {

    @EmbeddedId
    private GroupOfProductFindEntityId id;

    @Column(name = "GROUP_ID", insertable = false, updatable = false)
    private Long groupId;

    @Column(name = "PRODUCT_ID", insertable = false, updatable = false)
    private Long productId;

    public GroupOfProductFindEntityId getId() {
        return id;
    }

    public void setId(GroupOfProductFindEntityId id) {
        this.id = id;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    @Embeddable
    class GroupOfProductFindEntityId implements Serializable {

        @Column(name = "GROUP_ID")
        private Long groupId;

        @Column(name = "PRODUCT_ID")
        private Long productId;

        public Long getGroupId() {
            return groupId;
        }

        public void setGroupId(Long groupId) {
            this.groupId = groupId;
        }

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GroupOfProductFindEntityId that = (GroupOfProductFindEntityId) o;
            return Objects.equals(groupId, that.groupId) &&
                    Objects.equals(productId, that.productId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(groupId, productId);
        }
    }
}
