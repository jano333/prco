package sk.hudak.prco.manager.export

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.ProductAction
import sk.hudak.prco.api.Unit
import sk.hudak.prco.dto.product.NewProductFullDto
import sk.hudak.prco.dto.product.NotInterestedProductFindDto
import sk.hudak.prco.dto.product.NotInterestedProductFullDto
import sk.hudak.prco.dto.product.ProductFullDto
import sk.hudak.prco.exception.PrcoRuntimeException
import java.io.*
import java.math.BigDecimal
import java.nio.file.Files
import java.nio.file.Paths
import java.text.ParseException
import java.util.*
import java.util.function.BiPredicate
import javax.annotation.PostConstruct
import kotlin.collections.ArrayList


@Component
class DbExportImportManagerImpl : AbstractExportImportManagerImpl(), DbExportImportManager {

    companion object {
        val log = LoggerFactory.getLogger(DbExportImportManagerImpl::class.java)!!

        private val DELIMITER = ';'

        private val NEW_PRODUCT_FILE_NAME_PREFIX = "new_product"
        private val PRODUCT_FILE_NAME_PREFIX = "product"
        private val NOT_INTERESTED_PRODUCT_FILE_NAME_PREFIX = "not_interested_product"

        private val ESHOP_UUID = "eshopUuid"
        private val UNIT = "unit"
        private val UNIT_VALUE = "unitValue"
        private val CREATED = "created"
        private val UPDATED = "updated"
        private val URL = "url"
        private val NAME = "name"
        private val PRICE_FOR_PACKAGE = "priceForPackage"
        private val UNIT_PACKAGE_COUNT = "unitPackageCount"
        private val PRICE_FOR_ONE_ITEM_IN_PACKAGE = "priceForOneItemInPackage"
        private val VALID = "valid"
        private val CONFIRM_VALIDITY = "confirmValidity"
        private val PRICE_FOR_UNIT = "priceForUnit"
        private val LAST_TIME_DATA_UPDATED = "lastTimeDataUpdated"
        private val PRODUCT_ACTION = "productAction"
        private val ACTION_VALID_TO = "actionValidTo"
        private val PRODUCT_PICTURE_URL = "productPictureUrl"

        private val NEW_PRODUCTS_HEADERS = arrayOf(CREATED, UPDATED, URL, NAME, ESHOP_UUID, UNIT, UNIT_VALUE, UNIT_PACKAGE_COUNT, VALID, CONFIRM_VALIDITY)

        private val PRODUCTS_HEADERS = arrayOf(CREATED, UPDATED, URL, NAME, ESHOP_UUID, UNIT, UNIT_VALUE, UNIT_PACKAGE_COUNT, PRICE_FOR_PACKAGE, PRICE_FOR_ONE_ITEM_IN_PACKAGE, PRICE_FOR_UNIT, LAST_TIME_DATA_UPDATED, PRODUCT_ACTION, ACTION_VALID_TO, PRODUCT_PICTURE_URL)

        private val NOT_ITERESTED_PRODUCTS_HEADERS = arrayOf(CREATED, UPDATED, URL, NAME, ESHOP_UUID, UNIT, UNIT_VALUE, UNIT_PACKAGE_COUNT, VALID, CONFIRM_VALIDITY)

    }

    @PostConstruct
    protected fun init() {
        log.debug("export/import root dir: {}", sourceFolder)
    }

    override fun exportAllTablesToCsvFiles() {
        exportNewProducts()
        exportProducts()
        exportNotInterestedProducts()
    }

    override fun exportNewProducts() {
        var exportPath: String? = null
        var printWriter: PrintWriter? = null
        var csvFilePrinter: CSVPrinter? = null

        try {
            exportPath = createExportPath(NEW_PRODUCT_FILE_NAME_PREFIX)
            val newProductsForExport = internalTxService!!.findNewProductsForExport()
            printWriter = createPrintWriter(exportPath)
            csvFilePrinter = createCSVPrinter(printWriter)

            // create CSV file header
            csvFilePrinter.printRecord(*NEW_PRODUCTS_HEADERS)

            //Write a new student object list to the CSV file
            for ((_, created, updated, url, name, eshopUuid, unit, unitValue, unitPackageCount, valid, confirmValidity) in newProductsForExport) {
                val csvLine = ArrayList<String>()
                csvLine.add(safeToString(created)!!)
                csvLine.add(safeToString(updated)!!)
                csvLine.add(safeToString(url)!!)
                csvLine.add(safeToString(name)!!)
                csvLine.add(safeToString(eshopUuid)!!)
                csvLine.add(safeToString(unit)!!)
                csvLine.add(safeToString(unitValue)!!)
                csvLine.add(safeToString(unitPackageCount)!!)
                csvLine.add(safeToString(valid)!!)
                csvLine.add(safeToString(confirmValidity)!!)
                csvFilePrinter.printRecord(csvLine)
            }

            log.debug("CSV file was created successfully on path {}", exportPath)

        } catch (e: Exception) {
            log.error("error while exporting " + exportPath!!, e)

        } finally {
            closeStreams(printWriter!!, csvFilePrinter!!)
        }
    }

    override fun exportProducts() {
        var exportPath: String? = null
        var printWriter: PrintWriter? = null
        var csvFilePrinter: CSVPrinter? = null

        try {
            exportPath = createExportPath(PRODUCT_FILE_NAME_PREFIX)
            val products = internalTxService!!.findProductsForExport()
            printWriter = createPrintWriter(exportPath)
            csvFilePrinter = createCSVPrinter(printWriter)

            //Create CSV file header
            csvFilePrinter.printRecord(*PRODUCTS_HEADERS)

            //Write a new student object list to the CSV file
            for (product in products) {
                val csvLine = ArrayList<String>()
                csvLine.add(safeToString(product.created)!!)
                csvLine.add(safeToString(product.updated)!!)
                csvLine.add(safeToString(product.url)!!)
                csvLine.add(safeToString(product.name)!!)
                csvLine.add(safeToString(product.eshopUuid)!!)
                csvLine.add(safeToString(product.unit)!!)
                csvLine.add(safeToString(product.unitValue)!!)
                csvLine.add(safeToString(product.unitPackageCount)!!)
                csvLine.add(safeToString(product.priceForPackage)!!)
                csvLine.add(safeToString(product.priceForOneItemInPackage)!!)
                csvLine.add(safeToString(product.priceForUnit)!!)
                csvLine.add(safeToString(product.lastTimeDataUpdated)!!)
                csvLine.add(safeToString(product.productAction)!!)
                csvLine.add(safeToString(product.actionValidTo)!!)
                csvLine.add(safeToString(product.productPictureUrl)!!)
                csvFilePrinter.printRecord(csvLine)
            }

            log.debug("CSV file was created successfully on path {}", exportPath)

        } catch (e: Exception) {
            log.error("error while exporting " + exportPath!!, e)

        } finally {
            closeStreams(printWriter!!, csvFilePrinter!!)
        }
    }

    override fun exportNotInterestedProducts() {
        var exportPath: String? = null
        var printWriter: PrintWriter? = null
        var csvFilePrinter: CSVPrinter? = null
        try {
            exportPath = createExportPath(NOT_INTERESTED_PRODUCT_FILE_NAME_PREFIX)
            val notInterestedProductsForExport = internalTxService!!.findNotInterestedProducts(NotInterestedProductFindDto())
            printWriter = createPrintWriter(exportPath)
            csvFilePrinter = createCSVPrinter(printWriter)

            //Create CSV file header
            csvFilePrinter.printRecord(*NOT_ITERESTED_PRODUCTS_HEADERS as Array<Any>)

            //Write a new student object list to the CSV file
            for (product in notInterestedProductsForExport) {
                val csvLine = ArrayList<String>()
                csvLine.add(safeToString(product.created)!!)
                csvLine.add(safeToString(product.updated)!!)
                csvLine.add(safeToString(product.url)!!)
                csvLine.add(safeToString(product.name)!!)
                csvLine.add(safeToString(product.eshopUuid)!!)
                csvLine.add(safeToString(product.unit)!!)
                csvLine.add(safeToString(product.unitValue)!!)
                csvLine.add(safeToString(product.unitPackageCount)!!)

                csvFilePrinter.printRecord(csvLine)
            }

            log.debug("CSV file was created successfully on path {}", exportPath)

        } catch (e: Exception) {
            log.error("error while exporting " + exportPath!!, e)

        } finally {
            closeStreams(printWriter!!, csvFilePrinter!!)
        }
    }

    override fun importAllTables() {
        log.debug(">> importAllTables")
        importNewProductsFromCsv()
        importProductsFromCsv()
        importNotIterestedProductsFromCsv()
        log.debug("<< importAllTables")
    }

    override fun importNewProductsFromCsv() {
        log.debug(">> importNewProductsFromCsv")
        try {
            FileReader(findPathFor(NEW_PRODUCT_FILE_NAME_PREFIX)).use { `in` ->

                val result = ArrayList<NewProductFullDto>()

                CSVFormat.DEFAULT
                        .withRecordSeparator(AbstractExportImportManagerImpl.Companion.NEW_LINE_SEPARATOR)
                        .withDelimiter(DELIMITER)
                        .withHeader(*NEW_PRODUCTS_HEADERS)
                        .withFirstRecordAsHeader().parse(`in`)
                        .forEach { record ->
                            val dto = NewProductFullDto()
                            dto.created = readDateNullSafe(record.get(CREATED))
                            dto.updated = readDateNullSafe(record.get(UPDATED))
                            dto.url = record.get(URL)
                            dto.name = record.get(NAME)
                            dto.eshopUuid = readEshopUuidNullSafe(record.get(ESHOP_UUID))
                            dto.unit = readUnitNullSafe(record.get(UNIT))
                            dto.unitValue = readBigDecimalNullSafe(record.get(UNIT_VALUE))
                            dto.unitPackageCount = readIntegerNullSafe(record.get(UNIT_PACKAGE_COUNT))
                            dto.valid = readBooleanNullSafe(record.get(VALID))
                            dto.confirmValidity = readBooleanNullSafe(record.get(CONFIRM_VALIDITY))
                            result.add(dto)
                        }

                if (!result.isEmpty()) {
                    log.debug("count of new products that will be inserted: {}", result.size)
                    val realCount = internalTxService!!.importNewProducts(result)
                    log.debug("real count of new products that was inserted: {}", realCount)

                } else {
                    log.info("none new product will be inserted")
                }


            }
        } catch (e: Exception) {
            if (e is PrcoRuntimeException) {
                throw e
            }
            val msg = "error while import from file with prefix $NEW_PRODUCT_FILE_NAME_PREFIX"
            log.error(msg, e)
            throw PrcoRuntimeException(msg, e)
        }

        log.debug("<< importNewProductsFromCsv")
    }

    override fun importProductsFromCsv() {
        log.debug(">> importProductsFromCsv")
        try {
            FileReader(findPathFor(PRODUCT_FILE_NAME_PREFIX)).use { `in` ->

                val result = ArrayList<ProductFullDto>()

                CSVFormat.DEFAULT
                        .withRecordSeparator(NEW_LINE_SEPARATOR)
                        .withDelimiter(DELIMITER)
                        .withHeader(*PRODUCTS_HEADERS)
                        .withFirstRecordAsHeader().parse(`in`)
                        .forEach { record ->
                            val dto = ProductFullDto()
                            dto.created = readDateNullSafe(record.get(CREATED))
                            dto.updated = readDateNullSafe(record.get(UPDATED))
                            dto.url = record.get(URL)
                            dto.name = record.get(NAME)
                            dto.eshopUuid = readEshopUuidNullSafe(record.get(ESHOP_UUID))
                            dto.unit = readUnitNullSafe(record.get(UNIT))
                            dto.unitValue = readBigDecimalNullSafe(record.get(UNIT_VALUE))
                            dto.unitPackageCount = readIntegerNullSafe(record.get(UNIT_PACKAGE_COUNT))
                            dto.priceForOneItemInPackage = readBigDecimalNullSafe(record.get(PRICE_FOR_ONE_ITEM_IN_PACKAGE))
                            dto.priceForUnit = readBigDecimalNullSafe(record.get(PRICE_FOR_UNIT))
                            dto.lastTimeDataUpdated = readDateNullSafe(record.get(LAST_TIME_DATA_UPDATED))
                            dto.productAction = readProductActionNullSafe(record.get(PRODUCT_ACTION))
                            dto.actionValidTo = readDateNullSafe(record.get(ACTION_VALID_TO))
                            dto.productPictureUrl = record.get(PRODUCT_PICTURE_URL)
                            result.add(dto)
                        }

                if (!result.isEmpty()) {
                    log.debug("count of products that will be inserted: {}", result.size)
                    val realCount = internalTxService!!.importProducts(result)
                    log.info("real count of products that was inserted: {}", realCount)

                } else {
                    log.info("none product will be inserted")
                }

            }
        } catch (e: Exception) {
            if (e is PrcoRuntimeException) {
                throw e
            }
            val msg = "error while import from file with prefix $PRODUCT_FILE_NAME_PREFIX"
            log.error(msg, e)
            throw PrcoRuntimeException(msg, e)
        }

        log.debug("<< importProductsFromCsv")
    }

    override fun importNotIterestedProductsFromCsv() {
        log.debug(">> importNotIterestedProductsFromCsv")
        try {
            FileReader(findPathFor(NOT_INTERESTED_PRODUCT_FILE_NAME_PREFIX)).use { `in` ->

                val result = ArrayList<NotInterestedProductFullDto>()

                CSVFormat.DEFAULT
                        .withRecordSeparator(AbstractExportImportManagerImpl.Companion.NEW_LINE_SEPARATOR)
                        .withDelimiter(DELIMITER)
                        .withHeader(*NOT_ITERESTED_PRODUCTS_HEADERS)
                        .withFirstRecordAsHeader().parse(`in`)
                        .forEach { record ->
                            val dto = NotInterestedProductFullDto()
                            dto.created = readDateNullSafe(record.get(CREATED))
                            dto.updated = readDateNullSafe(record.get(UPDATED))
                            dto.url = record.get(URL)
                            dto.name = record.get(NAME)
                            dto.eshopUuid = readEshopUuidNullSafe(record.get(ESHOP_UUID))
                            dto.unit = readUnitNullSafe(record.get(UNIT))
                            dto.unitValue = readBigDecimalNullSafe(record.get(UNIT_VALUE))
                            dto.unitPackageCount = readIntegerNullSafe(record.get(UNIT_PACKAGE_COUNT))
                            result.add(dto)
                        }

                if (!result.isEmpty()) {
                    log.debug("count of not interested products that will be inserted: {}", result.size)
                    val realCount = internalTxService!!.importNotInterestedProducts(result)
                    log.info("real count of not interested products that was inserted: {}", realCount)

                } else {
                    log.info("none not interested product will be inserted")
                }


            }
        } catch (e: Exception) {
            if (e is PrcoRuntimeException) {
                throw e
            }
            val msg = "error while import from file with prefix $NOT_INTERESTED_PRODUCT_FILE_NAME_PREFIX"
            log.error(msg, e)
            throw PrcoRuntimeException(msg, e)
        }

        log.debug("<< importNotIterestedProductsFromCsv")
    }

    private fun readIntegerNullSafe(value: String?): Int? {
        return if (value == null || value.isEmpty()) {
            null
        } else Integer.valueOf(value)
    }

    private fun readBooleanNullSafe(value: String?): Boolean? {
        return if (value == null || value.isEmpty()) {
            null
        } else java.lang.Boolean.valueOf(value)
    }

    private fun readDateNullSafe(value: String?): Date? {
        if (value == null || value.isEmpty()) {
            return null
        }
        try {
            return sdf.parse(value)
        } catch (e: ParseException) {
            throw PrcoRuntimeException("error parsing date", e)
        }

    }

    private fun readBigDecimalNullSafe(value: String?): BigDecimal? {
        return if (value == null || value.isEmpty()) {
            null
        } else BigDecimal(value)
    }

    private fun readUnitNullSafe(value: String?): Unit? {
        return if (value == null || value.isEmpty()) {
            null
        } else Unit.valueOf(value)
    }

    private fun readEshopUuidNullSafe(value: String?): EshopUuid? {
        return if (value == null || value.isEmpty()) {
            null
        } else EshopUuid.valueOf(value)
    }

    private fun readProductActionNullSafe(value: String?): ProductAction? {
        return if (value == null || value.isEmpty()) {
            null
        } else ProductAction.valueOf(value)
    }


    private fun closeStreams(printWriter: PrintWriter, csvFilePrinter: CSVPrinter) {
        try {
            printWriter.flush()
            printWriter.close()
            csvFilePrinter.close()
        } catch (e: IOException) {
            log.error("Error while flushing/closing printWriter/csvPrinter !!!", e)
        }

    }

    @Throws(IOException::class)
    private fun createCSVPrinter(printWriter: PrintWriter): CSVPrinter {
        return CSVPrinter(printWriter, CSVFormat.DEFAULT
                .withRecordSeparator(AbstractExportImportManagerImpl.Companion.NEW_LINE_SEPARATOR)
                .withDelimiter(DELIMITER))
    }

    @Throws(UnsupportedEncodingException::class, FileNotFoundException::class)
    private fun createPrintWriter(exportPath: String): PrintWriter {
        return PrintWriter(OutputStreamWriter(FileOutputStream(exportPath), "UTF-8"))
    }

    private fun createExportPath(productFileNamePrefix: String): String {
        return sourceFolder + productFileNamePrefix + "_" +
                this.sdfForFileName.format(Date()) +
                ".csv"
    }

    @Throws(IOException::class)
    private fun findPathFor(fileNamePrefix: String): String {
        return Files.find(
                Paths.get(sourceFolder),
                1,
                BiPredicate { path, basicFileAttributes ->
                    basicFileAttributes.isRegularFile && path.toFile().name.startsWith(fileNamePrefix)
                })
                .findFirst()
                .orElseThrow { PrcoRuntimeException("file with prefix: '$fileNamePrefix' not found in dir: $sourceFolder") }
                .toFile().absolutePath
    }


}
