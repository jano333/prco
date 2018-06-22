package sk.hudak.prco.dto.group;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import sk.hudak.prco.dto.DtoAble;

@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class GroupUpdateDto implements DtoAble {
    @NonNull
    private Long id;
    @NonNull
    private String name;
}
