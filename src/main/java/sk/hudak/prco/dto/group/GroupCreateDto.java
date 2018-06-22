package sk.hudak.prco.dto.group;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import sk.hudak.prco.dto.DtoAble;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class GroupCreateDto implements DtoAble {

    // nazov (povinne)
    @NonNull
    private String name;

    // zoznam produktov v danej grupe (nepovinne)
    private List<Long> productIds = new ArrayList<>();
}
