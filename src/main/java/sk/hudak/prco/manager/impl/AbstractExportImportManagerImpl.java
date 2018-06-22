package sk.hudak.prco.manager.impl;

import org.springframework.beans.factory.annotation.Value;
import sk.hudak.prco.service.InternalTxService;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class AbstractExportImportManagerImpl {

    protected static final String NEW_LINE_SEPARATOR = System.lineSeparator();

    @Inject
    protected InternalTxService internalTxService;

    @Value("${prco.server.export.import.root.dir}")
    protected String sourceFolder;

    protected SimpleDateFormat sdfForFileName;
    protected SimpleDateFormat sdf;


    public AbstractExportImportManagerImpl() {
        this.sdfForFileName = new SimpleDateFormat("dd-MM-yyyy_HH_mm_ss");
        this.sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    }

    protected String safeToString(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Date) {
            return sdf.format((Date) obj);
        }
        return obj.toString();
    }
}
