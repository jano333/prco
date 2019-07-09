package sk.hudak.prco.dto.group;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import sk.hudak.prco.dto.DtoAble;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class GroupProductKeywordsCreateDto implements DtoAble {

    @NonNull
    private Long groupId;

    @NonNull
    private List<String> keyWords;
}
