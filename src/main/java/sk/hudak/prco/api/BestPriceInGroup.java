package sk.hudak.prco.api;

public enum BestPriceInGroup {
    /**
     * V pripade ak je to cena daneho produktu najlepsie v skupine v ktorej sa nachadza.
     * Len za predpokladu ze sa nachadza len jednej skupine.
     */
    YES,

    /**
     * V pripade ak je to cena daneho produktu NIEje najlepsia v skupine v ktorej sa nachadza.
     * Len za predpokladu ze sa nachadza len jednej skupine.
     */
    NO,

    /**
     * V pripade ak sa dany produktu v ziadnej skupine nenachadza.
     */
    NO_GROUP, //TODO aj toto zohladnit

    /**
     * V pripade ak produkt nachadza vo viacerych skupinach.
     */
    MULTIPLY_GROUPS,

    /**
     * Ak to nespada do ziadnej inej kategorie.
     */
    UNKNOWN
}
