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
            asList("pampers", "premium", "newborn", "1"),
            asList("pampers", "premium", "care", "newborn", "1"),
            asList("pampers", "premium", "care", "newborn", "(1)")
    ),
    PAMPERS_PREMIUM_2(
            451L,
            asList("pampers", "premium", "care", "mini", "2"),
            asList("pampers", "premium", "care", "mini", "(2)"),
            asList("pampers", "premium", "pack", "s2")
    ),
    PAMPERS_PREMIUM_3(
            452L,
            asList("pampers", "premium", "care", "midi", "3"),
            asList("pampers", "premium", "care", "midi", "(3)")
    ),
    PAMPERS_PREMIUM_4(
            453L,
            asList("pampers", "premium", "maxi", "4"),
            asList("pampers", "premium", "care", "maxi", "4"),
            asList("pampers", "premium", "care", "maxi", "(4)")
    ),
    PAMPERS_PREMIUM_5(
            481L,
            asList("pampers", "premium", "junior", "5"),
            asList("pampers", "premium", "care", "junior", "5"),
            asList("pampers", "premium", "care", "junior", "(5)")
    ),

    PAMPERS_ZELENE_1(
            545L,
            asList("pampers", "active", "baby", "1"),
            asList("pampers", "active", "baby", "1,"),
            asList("pampers", "active", "baby", "1+"),
            asList("pampers", "active", "baby", "1+,"),
            asList("pampers", "active", "baby", "s1"),
            asList("pampers", "active", "baby", "s1+"),
            asList("pampers", "active", "baby", "s1p"),
            asList("pampers", "active", "baby-dry", "1+"),
            asList("pampers", "activebaby", "1+"),
            asList("pampers", "new", "baby-dry", "1"),
            asList("pampers", "active", "baby-dry", "1"),
            asList("pampers", "pure", "protection", "1"),
            asList("pampers", "pure", "protection", "s1"),
            asList("pampers", "pure", "protection", "s1,"),
            asList("pampers", "giant", "pack", "maxi", "1")
    ),
    PAMPERS_ZELENE_2(
            546L,
            asList("pampers", "active", "baby", "2"),
            asList("pampers", "active", "baby", "2,"),
            asList("pampers", "active", "baby", "2+"),
            asList("pampers", "active", "baby", "2+,"),
            asList("pampers", "active", "baby", "s2"),
            asList("pampers", "active", "baby", "s2+"),
            asList("pampers", "active", "baby", "s2p"),
            asList("pampers", "active", "baby-dry", "2+"),
            asList("pampers", "activebaby", "2+"),
            asList("pampers", "new", "baby", "2"),
            asList("pampers", "new", "baby", "s2"),
            asList("pampers", "new", "baby-dry", "2"),
            asList("pampers", "active", "baby-dry", "2"),
            asList("pampers", "pure", "protection", "2"),
            asList("pampers", "pure", "protection", "s2"),
            asList("pampers", "pure", "protection", "s2,"),
            asList("pampers", "giant", "pack", "maxi", "2")
    ),
    PAMPERS_ZELENE_3(
            547L,
            asList("pampers", "active", "baby", "3"),
            asList("pampers", "active", "baby", "3,"),
            asList("pampers", "active", "baby", "3+"),
            asList("pampers", "active", "baby", "3+,"),
            asList("pampers", "active", "baby", "s3"),
            asList("pampers", "active", "baby", "s3+"),
            asList("pampers", "active", "baby", "s3p"),
            asList("pampers", "active", "baby-dry", "3+"),
            asList("pampers", "activebaby", "3+"),
            asList("pampers", "new", "baby-dry", "3"),
            asList("pampers", "active", "baby-dry", "3"),
            asList("pampers", "pure", "protection", "3"),
            asList("pampers", "pure", "protection", "s3"),
            asList("pampers", "pure", "protection", "s3,"),
            asList("pampers", "giant", "pack", "maxi", "3")
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
    PAMPERS_ZELENE_6(
            513L,
            asList("pampers", "active", "baby", "6"),
            asList("pampers", "active", "baby", "6,"),
            asList("pampers", "active", "baby", "6+"),
            asList("pampers", "active", "baby", "6+,"),
            asList("pampers", "active", "baby", "s6"),
            asList("pampers", "active", "baby", "s6+"),
            asList("pampers", "active", "baby", "s6p"),
            asList("pampers", "active", "baby-dry", "6+"),
            asList("pampers", "activebaby", "6+"),
            asList("pampers", "new", "baby-dry", "6"),
            asList("pampers", "active", "baby-dry", "6"),
            asList("pampers", "pure", "protection", "6"),
            asList("pampers", "pure", "protection", "s6"),
            asList("pampers", "pure", "protection", "s6,"),
            asList("pampers", "giant", "pack", "maxi", "6")
    ),
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
