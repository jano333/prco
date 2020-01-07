package sk.hudak.prco.ui

object MvcConstants {

    // view names
    const val  VIEW_NEW_PRODUCTS = "newProducts"
    const val  VIEW_NEW_PRODUCT_UNIT_DATA_EDIT = "newProductUnitDataEdit"
    const val  VIEW_PRODUCTS_NOT_IN_ANY_GROUP = "productsNotIntAnyGroup"
    const val  VIEW_PRODUCT_ADD_TO_GROUP = "productAddToGroup"
    const val  VIEW_PRODUCTS_IN_GROUP = "productsInGroup"
    const val  VIEW_ERRORS = "errors"
    const val  VIEW_EXECUTOR_STATISTICS = "executorStatistics"
    const val  VIEW_ESHOPS_ADMIN = "eshopsAdmin"

    const val  ATTRIBUTE_COUNT_OF_PRODUCTS_NOT_IN_ANY_GROUP = "countOfProductsNotInAnyGroup"

    // redirect to view names
    const val  REDIRECT = "redirect:/"
    const val  REDIRECT_TO_VIEW_NEW_PRODUCTS = REDIRECT + VIEW_NEW_PRODUCTS
    const val  REDIRECT_TO_VIEW_PRODUCTS_NOT_IN_ANY_GROUP = REDIRECT + VIEW_PRODUCTS_NOT_IN_ANY_GROUP


}//no instace



