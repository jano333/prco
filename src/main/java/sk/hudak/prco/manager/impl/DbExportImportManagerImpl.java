package sk.hudak.prco.manager.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Component;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.api.ProductAction;
import sk.hudak.prco.api.Unit;
import sk.hudak.prco.dto.NewProductFullDto;
import sk.hudak.prco.dto.NotInterestedProductFindDto;
import sk.hudak.prco.dto.NotInterestedProductFullDto;
import sk.hudak.prco.dto.product.ProductFullDto;
import sk.hudak.prco.exception.PrcoRuntimeException;
import sk.hudak.prco.manager.DbExportImportManager;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class DbExportImportManagerImpl extends AbstractExportImportManagerImpl implements DbExportImportManager {

    private static final char DELIMITER = ';';

    private static final String NEW_PRODUCT_FILE_NAME_PREFIX = "new_product";
    private static final String PRODUCT_FILE_NAME_PREFIX = "product";
    private static final String NOT_INTERESTED_PRODUCT_FILE_NAME_PREFIX = "not_interested_product";

    private static final String ESHOP_UUID = "eshopUuid";
    private static final String UNIT = "unit";
    private static final String UNIT_VALUE = "unitValue";
    private static final String CREATED = "created";
    private static final String UPDATED = "updated";
    private static final String URL = "url";
    private static final String NAME = "name";
    private static final String PRICE_FOR_PACKAGE = "priceForPackage";
    private static final String UNIT_PACKAGE_COUNT = "unitPackageCount";
    private static final String PRICE_FOR_ONE_ITEM_IN_PACKAGE = "priceForOneItemInPackage";
    private static final String VALID = "valid";
    private static final String CONFIRM_VALIDITY = "confirmValidity";
    private static final String PRICE_FOR_UNIT = "priceForUnit";
    private static final String LAST_TIME_DATA_UPDATED = "lastTimeDataUpdated";
    private static final String PRODUCT_ACTION = "productAction";
    private static final String ACTION_VALID_TO = "actionValidTo";
    private static final String PRODUCT_PICTURE_URL = "productPictureUrl";

    private static final String[] NEW_PRODUCTS_HEADERS = {CREATED, UPDATED,
            URL, NAME, ESHOP_UUID,
            UNIT, UNIT_VALUE, UNIT_PACKAGE_COUNT,
            VALID, CONFIRM_VALIDITY};

    private static final String[] PRODUCTS_HEADERS = {CREATED, UPDATED,
            URL, NAME, ESHOP_UUID,
            UNIT, UNIT_VALUE, UNIT_PACKAGE_COUNT,
            PRICE_FOR_PACKAGE, PRICE_FOR_ONE_ITEM_IN_PACKAGE, PRICE_FOR_UNIT, LAST_TIME_DATA_UPDATED,
            PRODUCT_ACTION, ACTION_VALID_TO,
            PRODUCT_PICTURE_URL};

    private static final String[] NOT_ITERESTED_PRODUCTS_HEADERS = {CREATED, UPDATED,
            URL, NAME, ESHOP_UUID,
            UNIT, UNIT_VALUE, UNIT_PACKAGE_COUNT,
            VALID, CONFIRM_VALIDITY};

    @PostConstruct
    protected void init() {
        log.debug("export/import root dir: {}", sourceFolder);
    }

    @Override
    public void exportAllTablesToCsvFiles() {
        exportNewProducts();
        exportProducts();
        exportNotInterestedProducts();
    }

    @Override
    public void exportNewProducts() {
        String exportPath = null;
        PrintWriter printWriter = null;
        CSVPrinter csvFilePrinter = null;

        try {
            exportPath = createExportPath(NEW_PRODUCT_FILE_NAME_PREFIX);
            List<NewProductFullDto> newProductsForExport = internalTxService.findNewProductsForExport();
            printWriter = createPrintWriter(exportPath);
            csvFilePrinter = createCSVPrinter(printWriter);

            // create CSV file header
            csvFilePrinter.printRecord(NEW_PRODUCTS_HEADERS);

            //Write a new student object list to the CSV file
            for (NewProductFullDto product : newProductsForExport) {
                List<String> csvLine = new ArrayList();
                csvLine.add(safeToString(product.getCreated()));
                csvLine.add(safeToString(product.getUpdated()));
                csvLine.add(safeToString(product.getUrl()));
                csvLine.add(safeToString(product.getName()));
                csvLine.add(safeToString(product.getEshopUuid()));
                csvLine.add(safeToString(product.getUnit()));
                csvLine.add(safeToString(product.getUnitValue()));
                csvLine.add(safeToString(product.getUnitPackageCount()));
                csvLine.add(safeToString(product.getValid()));
                csvLine.add(safeToString(product.getConfirmValidity()));
                csvFilePrinter.printRecord(csvLine);
            }

            log.debug("CSV file was created successfully on path {}", exportPath);

        } catch (Exception e) {
            log.error("error while exporting " + exportPath, e);

        } finally {
            closeStreams(printWriter, csvFilePrinter);
        }
    }

    @Override
    public void exportProducts() {
        String exportPath = null;
        PrintWriter printWriter = null;
        CSVPrinter csvFilePrinter = null;

        try {
            exportPath = createExportPath(PRODUCT_FILE_NAME_PREFIX);
            List<ProductFullDto> products = internalTxService.findProductsForExport();
            printWriter = createPrintWriter(exportPath);
            csvFilePrinter = createCSVPrinter(printWriter);

            //Create CSV file header
            csvFilePrinter.printRecord(PRODUCTS_HEADERS);

            //Write a new student object list to the CSV file
            for (ProductFullDto product : products) {
                List<String> csvLine = new ArrayList();
                csvLine.add(safeToString(product.getCreated()));
                csvLine.add(safeToString(product.getUpdated()));
                csvLine.add(safeToString(product.getUrl()));
                csvLine.add(safeToString(product.getName()));
                csvLine.add(safeToString(product.getEshopUuid()));
                csvLine.add(safeToString(product.getUnit()));
                csvLine.add(safeToString(product.getUnitValue()));
                csvLine.add(safeToString(product.getUnitPackageCount()));
                csvLine.add(safeToString(product.getPriceForPackage()));
                csvLine.add(safeToString(product.getPriceForOneItemInPackage()));
                csvLine.add(safeToString(product.getPriceForUnit()));
                csvLine.add(safeToString(product.getLastTimeDataUpdated()));
                csvLine.add(safeToString(product.getProductAction()));
                csvLine.add(safeToString(product.getActionValidTo()));
                csvLine.add(safeToString(product.getProductPictureUrl()));
                csvFilePrinter.printRecord(csvLine);
            }

            log.debug("CSV file was created successfully on path {}", exportPath);

        } catch (Exception e) {
            log.error("error while exporting " + exportPath, e);

        } finally {
            closeStreams(printWriter, csvFilePrinter);
        }
    }

    @Override
    public void exportNotInterestedProducts() {
        String exportPath = null;
        PrintWriter printWriter = null;
        CSVPrinter csvFilePrinter = null;
        try {
            exportPath = createExportPath(NOT_INTERESTED_PRODUCT_FILE_NAME_PREFIX);
            List<NotInterestedProductFullDto> notInterestedProductsForExport = internalTxService.findNotInterestedProducts(new NotInterestedProductFindDto());
            printWriter = createPrintWriter(exportPath);
            csvFilePrinter = createCSVPrinter(printWriter);

            //Create CSV file header
            csvFilePrinter.printRecord((Object[]) NOT_ITERESTED_PRODUCTS_HEADERS);

            //Write a new student object list to the CSV file
            for (NotInterestedProductFullDto product : notInterestedProductsForExport) {
                List<String> csvLine = new ArrayList<>();
                csvLine.add(safeToString(product.getCreated()));
                csvLine.add(safeToString(product.getUpdated()));
                csvLine.add(safeToString(product.getUrl()));
                csvLine.add(safeToString(product.getName()));
                csvLine.add(safeToString(product.getEshopUuid()));
                csvLine.add(safeToString(product.getUnit()));
                csvLine.add(safeToString(product.getUnitValue()));
                csvLine.add(safeToString(product.getUnitPackageCount()));

                csvFilePrinter.printRecord(csvLine);
            }

            log.debug("CSV file was created successfully on path {}", exportPath);

        } catch (Exception e) {
            log.error("error while exporting " + exportPath, e);

        } finally {
            closeStreams(printWriter, csvFilePrinter);
        }
    }

    @Override
    public void importAllTables() {
        log.debug(">> importAllTables");
        importNewProductsFromCsv();
        importProductsFromCsv();
        importNotIterestedProductsFromCsv();
        log.debug("<< importAllTables");
    }

    @Override
    public void importNewProductsFromCsv() {
        log.debug(">> importNewProductsFromCsv");
        try (Reader in = new FileReader(findPathFor(NEW_PRODUCT_FILE_NAME_PREFIX))) {

            List<NewProductFullDto> result = new ArrayList<>();

            CSVFormat.DEFAULT
                    .withRecordSeparator(NEW_LINE_SEPARATOR)
                    .withDelimiter(DELIMITER)
                    .withHeader(NEW_PRODUCTS_HEADERS)
                    .withFirstRecordAsHeader().parse(in)
                    .forEach(record -> {
                        NewProductFullDto dto = new NewProductFullDto();
                        dto.setCreated(readDateNullSafe(record.get(CREATED)));
                        dto.setUpdated(readDateNullSafe(record.get(UPDATED)));
                        dto.setUrl(record.get(URL));
                        dto.setName(record.get(NAME));
                        dto.setEshopUuid(readEshopUuidNullSafe(record.get(ESHOP_UUID)));
                        dto.setUnit(readUnitNullSafe(record.get(UNIT)));
                        dto.setUnitValue(readBigDecimalNullSafe(record.get(UNIT_VALUE)));
                        dto.setUnitPackageCount(readIntegerNullSafe(record.get(UNIT_PACKAGE_COUNT)));
                        dto.setValid(readBooleanNullSafe(record.get(VALID)));
                        dto.setConfirmValidity(readBooleanNullSafe(record.get(CONFIRM_VALIDITY)));
                        result.add(dto);
                    });

            if (!result.isEmpty()) {
                log.debug("count of new products that will be inserted: {}", result.size());
                long realCount = internalTxService.importNewProducts(result);
                log.debug("real count of new products that was inserted: {}", realCount);

            } else {
                log.info("none new product will be inserted");
            }


        } catch (Exception e) {
            if (e instanceof PrcoRuntimeException) {
                throw (PrcoRuntimeException) e;
            }
            String msg = "error while import from file with prefix " + NEW_PRODUCT_FILE_NAME_PREFIX;
            log.error(msg, e);
            throw new PrcoRuntimeException(msg, e);
        }
        log.debug("<< importNewProductsFromCsv");
    }

    @Override
    public void importProductsFromCsv() {
        log.debug(">> importProductsFromCsv");
        try (Reader in = new FileReader(findPathFor(PRODUCT_FILE_NAME_PREFIX))) {

            List<ProductFullDto> result = new ArrayList<>();

            CSVFormat.DEFAULT
                    .withRecordSeparator(NEW_LINE_SEPARATOR)
                    .withDelimiter(DELIMITER)
                    .withHeader(PRODUCTS_HEADERS)
                    .withFirstRecordAsHeader().parse(in)
                    .forEach(record -> {
                        ProductFullDto dto = ProductFullDto.builder()
                                .created(readDateNullSafe(record.get(CREATED)))
                                .updated(readDateNullSafe(record.get(UPDATED)))
                                .url(record.get(URL))
                                .name(record.get(NAME))
                                .eshopUuid(readEshopUuidNullSafe(record.get(ESHOP_UUID)))
                                .unit(readUnitNullSafe(record.get(UNIT)))
                                .unitValue(readBigDecimalNullSafe(record.get(UNIT_VALUE)))
                                .unitPackageCount(readIntegerNullSafe(record.get(UNIT_PACKAGE_COUNT)))
                                .priceForOneItemInPackage(readBigDecimalNullSafe(record.get(PRICE_FOR_ONE_ITEM_IN_PACKAGE)))
                                .priceForUnit(readBigDecimalNullSafe(record.get(PRICE_FOR_UNIT)))
                                .lastTimeDataUpdated(readDateNullSafe(record.get(LAST_TIME_DATA_UPDATED)))
                                .productAction(readProductActionNullSafe(record.get(PRODUCT_ACTION)))
                                .actionValidTo(readDateNullSafe(record.get(ACTION_VALID_TO)))
                                .productPictureUrl(record.get(PRODUCT_PICTURE_URL))
                                .build();

                        result.add(dto);
                    });

            if (!result.isEmpty()) {
                log.debug("count of products that will be inserted: {}", result.size());
                long realCount = internalTxService.importProducts(result);
                log.info("real count of products that was inserted: {}", realCount);

            } else {
                log.info("none product will be inserted");
            }

        } catch (Exception e) {
            if (e instanceof PrcoRuntimeException) {
                throw (PrcoRuntimeException) e;
            }
            String msg = "error while import from file with prefix " + PRODUCT_FILE_NAME_PREFIX;
            log.error(msg, e);
            throw new PrcoRuntimeException(msg, e);
        }
        log.debug("<< importProductsFromCsv");
    }

    @Override
    public void importNotIterestedProductsFromCsv() {
        log.debug(">> importNotIterestedProductsFromCsv");
        try (Reader in = new FileReader(findPathFor(NOT_INTERESTED_PRODUCT_FILE_NAME_PREFIX))) {

            List<NotInterestedProductFullDto> result = new ArrayList<>();

            CSVFormat.DEFAULT
                    .withRecordSeparator(NEW_LINE_SEPARATOR)
                    .withDelimiter(DELIMITER)
                    .withHeader(NOT_ITERESTED_PRODUCTS_HEADERS)
                    .withFirstRecordAsHeader().parse(in)
                    .forEach(record -> {
                        NotInterestedProductFullDto dto = new NotInterestedProductFullDto();
                        dto.setCreated(readDateNullSafe(record.get(CREATED)));
                        dto.setUpdated(readDateNullSafe(record.get(UPDATED)));
                        dto.setUrl(record.get(URL));
                        dto.setName(record.get(NAME));
                        dto.setEshopUuid(readEshopUuidNullSafe(record.get(ESHOP_UUID)));
                        dto.setUnit(readUnitNullSafe(record.get(UNIT)));
                        dto.setUnitValue(readBigDecimalNullSafe(record.get(UNIT_VALUE)));
                        dto.setUnitPackageCount(readIntegerNullSafe(record.get(UNIT_PACKAGE_COUNT)));
                        result.add(dto);
                    });

            if (!result.isEmpty()) {
                log.debug("count of not interested products that will be inserted: {}", result.size());
                long realCount = internalTxService.importNotInterestedProducts(result);
                log.info("real count of not interested products that was inserted: {}", realCount);

            } else {
                log.info("none not interested product will be inserted");
            }


        } catch (Exception e) {
            if (e instanceof PrcoRuntimeException) {
                throw (PrcoRuntimeException) e;
            }
            String msg = "error while import from file with prefix " + NOT_INTERESTED_PRODUCT_FILE_NAME_PREFIX;
            log.error(msg, e);
            throw new PrcoRuntimeException(msg, e);
        }
        log.debug("<< importNotIterestedProductsFromCsv");
    }

    private Integer readIntegerNullSafe(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return Integer.valueOf(value);
    }

    private Boolean readBooleanNullSafe(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return Boolean.valueOf(value);
    }

    private Date readDateNullSafe(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return sdf.parse(value);
        } catch (ParseException e) {
            throw new PrcoRuntimeException("error parsing date", e);
        }
    }

    private BigDecimal readBigDecimalNullSafe(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return new BigDecimal(value);
    }

    private Unit readUnitNullSafe(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return Unit.valueOf(value);
    }

    private EshopUuid readEshopUuidNullSafe(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return EshopUuid.valueOf(value);
    }

    private ProductAction readProductActionNullSafe(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return ProductAction.valueOf(value);
    }


    private void closeStreams(PrintWriter printWriter, CSVPrinter csvFilePrinter) {
        try {
            printWriter.flush();
            printWriter.close();
            csvFilePrinter.close();
        } catch (IOException e) {
            log.error("Error while flushing/closing printWriter/csvPrinter !!!", e);
        }
    }

    private CSVPrinter createCSVPrinter(PrintWriter printWriter) throws IOException {
        return new CSVPrinter(printWriter, CSVFormat.DEFAULT
                .withRecordSeparator(NEW_LINE_SEPARATOR)
                .withDelimiter(DELIMITER));
    }

    private PrintWriter createPrintWriter(String exportPath) throws UnsupportedEncodingException, FileNotFoundException {
        return new PrintWriter(new OutputStreamWriter(new FileOutputStream(exportPath), "UTF-8"));
    }

    private String createExportPath(String productFileNamePrefix) {
        return sourceFolder + productFileNamePrefix + "_" +
                this.sdfForFileName.format(new Date()) +
                ".csv";
    }

    private String findPathFor(String fileNamePrefix) throws IOException {
        return Files.find(
                Paths.get(sourceFolder),
                1,
                (path, basicFileAttributes) -> basicFileAttributes.isRegularFile() && path.toFile().getName().startsWith(fileNamePrefix)
        ).findFirst().orElseThrow(
                () -> new PrcoRuntimeException("file with prefix: '" + fileNamePrefix + "' not found in dir: " + sourceFolder)
        ).toFile().getAbsolutePath();
    }

}
