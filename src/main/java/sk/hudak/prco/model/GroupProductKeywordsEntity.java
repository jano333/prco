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
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

@Getter
@Setter
@Entity(name = "GROUP_KEYWORDS")
public class GroupProductKeywordsEntity extends DbEntity {

    @Id
    @GeneratedValue(generator = "GROUP_KEYWORDS_SEC", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "GROUP_KEYWORDS_SEC", sequenceName = "GROUP_KEYWORDS_SEC", allocationSize = 1)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "GROUP_ID", nullable = false, updatable = false)
    private GroupEntity group;

    @Column(nullable = false)
    private String keyWords;

}
