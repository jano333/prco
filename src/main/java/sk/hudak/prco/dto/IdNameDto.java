package sk.hudak.prco.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public abstract class IdNameDto {
    private Long id;
    private String name;
}
