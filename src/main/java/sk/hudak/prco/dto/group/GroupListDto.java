package sk.hudak.prco.dto.group;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sk.hudak.prco.dto.DtoAble;

@Getter
@Setter
@ToString
public class GroupListDto implements DtoAble {
    private Long id;
    private String name;
}
