package sk.hudak.prco.dto.group;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sk.hudak.prco.dto.DtoAble;

import java.util.List;

@Getter
@Setter
@ToString
public class GroupProductKeywordsFullDto implements DtoAble {

    private GroupIdNameDto groupIdNameDto;

    private List<String[]> keyWords;

}
