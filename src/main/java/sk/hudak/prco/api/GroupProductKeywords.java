package sk.hudak.prco.api;

import java.util.List;

import static java.util.Arrays.asList;

public enum GroupProductKeywords {

    //    TODO pridat zoznam ktore nesmie mat, data aby do nutrilon 4 nam nedava nutrilon 4 profutura

    LOVELA_GEL_WHITE(
            353L,
            asList("lovela", "gel", "biele"),
            asList("lovela", "gel", "biela"),
            asList("lovela", "gel", "white"),
            asList("lovela", "biela", "tekuty")
    ),
    LOVELA_GEL_COLOR(
            417L,
            asList("lovela", "gel", "color"),
            asList("lovela", "gel", "color,"),
            asList("lovela", "gel", "farebne"),
            asList("lovela", "tekuty", "prasok", "color"),
            asList("lovela", "tekuty", "farebna")
    ),
    LOVELA_POWDER_WHITE(
            386L,
            asList("lovela", "prasok", "biele"),
            asList("lovela", "prasok", "biela"),
            asList("lovela", "prasok", "biela,"),
            asList("lovela", "prasek", "bile")
    ),
    LOVELA_POWDER_COLOR(
            385L,
            asList("lovela", "prasok", "color"),
            asList("lovela", "prasok", "color,"),
            asList("lovela", "prasok", "farebna"),
            asList("lovela", "prasek", "farebna"),
            asList("lovela", "prasek", "barevne")
    ),
    NUTRILON_1(
            577L,
            asList("nutrilon", "1")
    ),
    NUTRILON_2(
            578L,
            asList("nutrilon", "2")
    ),
    NUTRILON_3(
            579L,
            asList("nutrilon", "3")
    ),
    NUTRILON_4(
            33L,
            asList("nutrilon", "4")),
    NUTRILON_5(
            257L,
            asList("nutrilon", "5")),
    ;


    private Long groupId;

    private List<List<String>> choices;

    GroupProductKeywords(Long groupId, List<String>... choices) {
        this.groupId = groupId;
        this.choices = asList(choices);
    }

    public Long getGroupId() {
        return groupId;
    }

    public List<List<String>> getChoices() {
        return choices;
    }
}
