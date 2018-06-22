package sk.hudak.prco.model;

import lombok.Getter;
import lombok.Setter;
import sk.hudak.prco.model.core.DbEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity(name = "GROUP_OF_PRODUCT")
public class GroupEntity extends DbEntity {

    @Id
    @GeneratedValue(generator = "GROUP_SEC", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "GROUP_SEC", sequenceName = "GROUP_SEC", allocationSize = 1)
    private Long id;

    @Column(name = "NAME", nullable = false, unique = true)
    private String name;

    // zoznam produktov v danej grupe
    @ManyToMany
    @JoinTable(
            name = "GROUP_PRODUCT",
            joinColumns = @JoinColumn(name = "GROUP_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "PRODUCT_ID", referencedColumnName = "ID"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"GROUP_ID", "PRODUCT_ID"})
    )
    private List<ProductEntity> products = new ArrayList<>();

}
