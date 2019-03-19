package sk.hudak.prco.api;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

public enum GroupProductKeywords {

//    TODO pridat zoznam ktore nesmie mat, data aby do nutrilon 4 nam nedava nutrilon 4 profutura


    LOVELA_GEL_WHITE(
            353L,
            Arrays.asList("lovela", "gel", "biele")
    ),

    LOVELA_GEL_COLOR(
            417L,
            Arrays.asList("lovela", "gel", "color")
    ),

    LOVELA_PRASOK_BIELE(
            386L,
            Arrays.asList("lovela", "prasok", "biele")
    ),

    PAMPERS_PREMIUM_0(
            449L,
            Arrays.asList("pampers", "premium", "care", "newborn", "0")
    ),

    PAMPERS_PREMIUM_1(
            450L,
            Arrays.asList("pampers", "premium", "care", "newborn", "1"),
            Arrays.asList("pampers", "premium", "care", "newborn", "(1)")
    ),

    PAMPERS_PREMIUM_2(
            451L,
            Arrays.asList("pampers", "premium", "care", "mini", "2"),
            Arrays.asList("pampers", "premium", "care", "mini", "(2)")
    ),

    PAMPERS_PREMIUM_3(
            452L,
            Arrays.asList("pampers", "premium", "care", "midi", "3"),
            Arrays.asList("pampers", "premium", "care", "midi", "(3)")
    ),

    PAMPERS_PREMIUM_4(
            453L,
            Arrays.asList("pampers", "premium", "care", "maxi", "4"),
            Arrays.asList("pampers", "premium", "care", "maxi", "(4)")
    ),

    PAMPERS_PREMIUM_5(
            481L,
            Arrays.asList("pampers", "premium", "care", "junior", "5"),
            Arrays.asList("pampers", "premium", "care", "junior", "(5)")
    ),


    PAMPERS_ZELENE_4(
            1L,
            Arrays.asList("pampers", "active", "baby", "4"),
            Arrays.asList("pampers", "active", "baby", "4,"),
            Arrays.asList("pampers", "active", "baby", "4+"),
            Arrays.asList("pampers", "active", "baby", "4+,"),
            Arrays.asList("pampers", "active", "baby", "s4"),
            Arrays.asList("pampers", "active", "baby", "s4+"),
            Arrays.asList("pampers", "active", "baby", "s4p"),
            Arrays.asList("pampers", "active", "baby-dry", "4+"),
            Arrays.asList("pampers", "activebaby", "4+"),
            Arrays.asList("pampers", "new", "baby-dry", "4"),
            Arrays.asList("pampers", "active", "baby-dry", "4"),
            Arrays.asList("pampers", "pure", "protection", "4"),
            Arrays.asList("pampers", "pure", "protection", "s4"),
            Arrays.asList("pampers", "pure", "protection", "s4,"),
            Arrays.asList("pampers", "giant", "pack", "maxi", "4")
    ),
    PAMPERS_ZELENE_5(
            321L,
            Arrays.asList("pampers", "active", "baby", "5"),
            Arrays.asList("pampers", "active", "baby", "5,"),
            Arrays.asList("pampers", "active", "baby", "5+"),
            Arrays.asList("pampers", "active", "baby", "5+,"),
            Arrays.asList("pampers", "active", "baby", "s5"),
            Arrays.asList("pampers", "active", "baby", "s5+"),
            Arrays.asList("pampers", "active", "baby", "s5p"),
            Arrays.asList("pampers", "active", "baby-dry", "5+"),
            Arrays.asList("pampers", "activebaby", "5+"),
            Arrays.asList("pampers", "new", "baby-dry", "5"),
            Arrays.asList("pampers", "active", "baby-dry", "5"),
            Arrays.asList("pampers", "pure", "protection", "5"),
            Arrays.asList("pampers", "pure", "protection", "s5"),
            Arrays.asList("pampers", "pure", "protection", "s5,"),
            Arrays.asList("pampers", "giant", "pack", "maxi", "5")
    ),
    NUTRILON_4(
            33L,
            Arrays.asList("nutrilon", "4")),
    NUTRILON_5(
            257L,
            Arrays.asList("nutrilon", "5")),;

    @Getter
    private Long groupId;
    @Getter
    private List<List<String>> choises;

    GroupProductKeywords(Long groupId, List<String>... choises) {
        this.groupId = groupId;
        this.choises = Arrays.asList(choises);
    }
}
