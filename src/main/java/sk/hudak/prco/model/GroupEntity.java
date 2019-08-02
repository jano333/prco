package sk.hudak.prco.model;

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

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ProductEntity> getProducts() {
        return products;
    }

    public void setProducts(List<ProductEntity> products) {
        this.products = products;
    }
}
