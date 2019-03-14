package sk.hudak.prco.api;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

public enum GroupProductKeywords {

//    TODO pridat zoznam ktore nesmie mat, data aby do nutrilon 4 nam nedava nutrilon 4 profutura

    PAMPERS_ZELENE_4(
            Arrays.asList("pampers", "active", "baby", "4"),
            Arrays.asList("pampers", "active", "baby", "4,"),
            Arrays.asList("pampers", "active", "baby", "4+"),
            Arrays.asList("pampers", "active", "baby", "4+,"),
            Arrays.asList("pampers", "active", "baby", "s4"),
            Arrays.asList("pampers", "active", "baby", "s4+"),
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
            Arrays.asList("pampers", "active", "baby", "5"),
            Arrays.asList("pampers", "active", "baby", "5,"),
            Arrays.asList("pampers", "active", "baby", "5+"),
            Arrays.asList("pampers", "active", "baby", "5+,"),
            Arrays.asList("pampers", "active", "baby", "s5"),
            Arrays.asList("pampers", "active", "baby", "s5+"),
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
            Arrays.asList("nutrilon", "4")),
    NUTRILON_5(
            Arrays.asList("nutrilon", "5")),;

    @Getter
    private List<List<String>> choises;

    GroupProductKeywords(List<String>... choises) {
        this.choises = Arrays.asList(choises);
    }
}
