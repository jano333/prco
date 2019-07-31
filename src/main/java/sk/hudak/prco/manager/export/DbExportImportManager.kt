package sk.hudak.prco.manager.export

interface DbExportImportManager {

    fun exportAllTablesToCsvFiles()

    fun exportNewProducts()

    fun exportProducts()

    fun exportNotInterestedProducts()

    fun importAllTables()

    fun importNewProductsFromCsv()

    fun importProductsFromCsv()

    fun importNotIterestedProductsFromCsv()

}
