package sk.hudak.prco.dto.group;

import sk.hudak.prco.dto.DtoAble;

public class GroupListDto implements DtoAble {
    private Long id;
    private String name;

    @Override
    public String toString() {
        return "GroupListDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

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
