package sk.hudak.prco.ui;

public final class MvcConstants {

    private MvcConstants() {
        //no instace
    }

    // view names
    public static final String VIEW_NEW_PRODUCTS = "newProducts";
    public static final String VIEW_NEW_PRODUCT_UNIT_DATA_EDIT = "newProductUnitDataEdit";
    public static final String VIEW_PRODUCTS_NOT_IN_ANY_GROUP = "productsNotIntAnyGroup";
    public static final String VIEW_PRODUCT_ADD_TO_GROUP = "productAddToGroup";
    public static final String VIEW_PRODUCTS_IN_GROUP = "productsInGroup";

    public static final String ATTRIBUTE_COUNT_OF_PRODUCTS_NOT_IN_ANY_GROUP = "countOfProductsNotInAnyGroup";

    // redirect to view names
    public static final String REDIRECT = "redirect:/";
    public static final String REDIRECT_TO_VIEW_NEW_PRODUCTS = REDIRECT + VIEW_NEW_PRODUCTS;
    public static final String REDIRECT_TO_VIEW_PRODUCTS_NOT_IN_ANY_GROUP = REDIRECT + VIEW_PRODUCTS_NOT_IN_ANY_GROUP;


}



