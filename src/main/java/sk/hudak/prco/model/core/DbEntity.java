package sk.hudak.prco.model.core;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@MappedSuperclass
public abstract class DbEntity {

    public abstract Long getId();

    public abstract void setId(Long id);

    /**
     * Kedy bola entita vlozena do databazy.
     */
    @Column(nullable = false)
    private Date created;

    /**
     * Kedy naposledy nastal updata danej entity
     */
    @Column(nullable = false)
    private Date updated;

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
}
