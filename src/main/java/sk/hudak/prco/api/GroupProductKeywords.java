package sk.hudak.prco.api;

import lombok.Getter;

import java.util.List;

import static java.util.Arrays.asList;

public enum GroupProductKeywords {

    //    TODO pridat zoznam ktore nesmie mat, data aby do nutrilon 4 nam nedava nutrilon 4 profutura

    PAMPERS_PREMIUM_0(
            449L,
            asList("pampers", "premium", "care", "newborn", "0")
    ),
    PAMPERS_PREMIUM_1(
            450L,
            asList("pampers", "premium", "care", "newborn", "1"),
            asList("pampers", "premium", "care", "newborn", "(1)")
    ),
    PAMPERS_PREMIUM_2(
            451L,
            asList("pampers", "premium", "care", "mini", "2"),
            asList("pampers", "premium", "care", "mini", "(2)")
    ),
    PAMPERS_PREMIUM_3(
            452L,
            asList("pampers", "premium", "care", "midi", "3"),
            asList("pampers", "premium", "care", "midi", "(3)")
    ),
    PAMPERS_PREMIUM_4(
            453L,
            asList("pampers", "premium", "care", "maxi", "4"),
            asList("pampers", "premium", "care", "maxi", "(4)")
    ),
    PAMPERS_PREMIUM_5(
            481L,
            asList("pampers", "premium", "care", "junior", "5"),
            asList("pampers", "premium", "care", "junior", "(5)")
    ),
    PAMPERS_ZELENE_4(
            1L,
            asList("pampers", "active", "baby", "4"),
            asList("pampers", "active", "baby", "4,"),
            asList("pampers", "active", "baby", "4+"),
            asList("pampers", "active", "baby", "4+,"),
            asList("pampers", "active", "baby", "s4"),
            asList("pampers", "active", "baby", "s4+"),
            asList("pampers", "active", "baby", "s4p"),
            asList("pampers", "active", "baby-dry", "4+"),
            asList("pampers", "activebaby", "4+"),
            asList("pampers", "new", "baby-dry", "4"),
            asList("pampers", "active", "baby-dry", "4"),
            asList("pampers", "pure", "protection", "4"),
            asList("pampers", "pure", "protection", "s4"),
            asList("pampers", "pure", "protection", "s4,"),
            asList("pampers", "giant", "pack", "maxi", "4")
    ),
    PAMPERS_ZELENE_5(
            321L,
            asList("pampers", "active", "baby", "5"),
            asList("pampers", "active", "baby", "5,"),
            asList("pampers", "active", "baby", "5+"),
            asList("pampers", "active", "baby", "5+,"),
            asList("pampers", "active", "baby", "s5"),
            asList("pampers", "active", "baby", "s5+"),
            asList("pampers", "active", "baby", "s5p"),
            asList("pampers", "active", "baby-dry", "5+"),
            asList("pampers", "activebaby", "5+"),
            asList("pampers", "new", "baby-dry", "5"),
            asList("pampers", "active", "baby-dry", "5"),
            asList("pampers", "pure", "protection", "5"),
            asList("pampers", "pure", "protection", "s5"),
            asList("pampers", "pure", "protection", "s5,"),
            asList("pampers", "giant", "pack", "maxi", "5")
    ),
    LOVELA_GEL_WHITE(
            353L,
            asList("lovela", "gel", "biele"),
            asList("lovela", "gel", "biela"),
            asList("lovela", "gel", "white")

    ),
    LOVELA_GEL_COLOR(
            417L,
            asList("lovela", "gel", "color"),
            asList("lovela", "gel", "farebne"),
            asList("lovela", "tekuty", "prasok", "color")
    ),

    LOVELA_POWDER_WHITE(
            386L,
            asList("lovela", "prasok", "biele"),
            asList("lovela", "prasok", "biela"),
            asList("lovela", "prasek", "bile")
    ),

    LOVELA_POWDER_COLOR(
            385L,
            asList("lovela", "prasok", "color")
    ),
    NUTRILON_4(
            33L,
            asList("nutrilon", "4")),
    NUTRILON_5(
            257L,
            asList("nutrilon", "5")),;

    @Getter
    private Long groupId;

    @Getter
    private List<List<String>> choices;

    GroupProductKeywords(Long groupId, List<String>... choices) {
        this.groupId = groupId;
        this.choices = asList(choices);
    }
}
