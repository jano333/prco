package sk.hudak.prco.model;

import lombok.EqualsAndHashCode;
import sk.hudak.prco.model.core.DbEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity(name = "SEARCH_KEYWORD")
@EqualsAndHashCode
public class SearchKeywordEntity extends DbEntity {

    // generovane databazov pri prvom vlozeni do DB
    @Id
    @GeneratedValue(generator = "SEARCH_KEYWORD_SEC", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "SEARCH_KEYWORD_SEC", sequenceName = "SEARCH_KEYWORD_SEC", allocationSize = 1)
    private Long id;

    private String name;

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
}
