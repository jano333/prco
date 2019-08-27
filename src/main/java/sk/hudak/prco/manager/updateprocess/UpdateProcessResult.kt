package sk.hudak.prco.manager.updateprocess

@Deprecated("remove")
enum class UpdateProcessResult {

    // parsing errors
    ERR_PARSING_ERROR_GENERIC,
    ERR_PARSING_ERROR_HTTP_TIMEOUT,
    ERR_PARSING_ERROR_HTTP_STATUS_INVALID,
    ERR_PARSING_ERROR_HTTP_STATUS_404,

    // updating errors
    ERR_UPDATE_ERROR_PRODUCT_IS_UNAVAILABLE,

    //TODO overit kde je pouzita
    ERR_HTML_PARSING_FAILED_404_ERROR,

    OK
}
