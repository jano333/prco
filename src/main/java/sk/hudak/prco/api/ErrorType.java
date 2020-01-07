package sk.hudak.prco.api;

public enum ErrorType {

    PARSING_PRODUCT_UPDATE_DATA,
    HTTP_STATUS_ERR,


    // OLD ----,
    PARSING_PRODUCT_INFO_ERR,

    HTTP_STATUS_404_ERR,

    //FIXME overit ako je mozne ze tato konstanta nie je pouzivata pri update!!!,
    PARSING_PRODUCT_UNIT_ERR,
    PARSING_PRODUCT_URLS,
    PARSING_PRODUCT_NEW_DATA,
    PARSING_PRODUCT_NAME_FOR_NEW_PRODUCT,

    TIME_OUT_ERR,

    UNKNOWN
}
