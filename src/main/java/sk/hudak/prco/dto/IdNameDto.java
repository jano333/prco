package sk.hudak.prco.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public abstract class IdNameDto implements DtoAble{
    private Long id;
    private String name;
}
