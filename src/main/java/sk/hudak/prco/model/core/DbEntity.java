package sk.hudak.prco.model.core;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@Getter
@Setter
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

}
