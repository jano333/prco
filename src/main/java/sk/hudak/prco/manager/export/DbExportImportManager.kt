package sk.hudak.prco.manager;

public interface DbExportImportManager {

    void exportAllTablesToCsvFiles();

    void exportNewProducts();

    void exportProducts();

    void exportNotInterestedProducts();

    void importAllTables();

    void importNewProductsFromCsv();

    void importProductsFromCsv();

    void importNotIterestedProductsFromCsv();

}
