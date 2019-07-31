package sk.hudak.prco.manager.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import sk.hudak.prco.dto.product.NewProductFullDto;
import sk.hudak.prco.manager.HtmlExportManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Component
public class HtmlExportManagerImpl extends AbstractExportImportManagerImpl implements HtmlExportManager {

    private static final String TD_START_TAG = "<td>";
    private static final String TD_END_TAG = "</td>";

    @Override
    public void buildHtml() {
        List<NewProductFullDto> newProductFullDtoList = internalTxService.findNewProductsForExport();

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>").append(NEW_LINE_SEPARATOR);
        html.append("<html lang=\"en\">").append(NEW_LINE_SEPARATOR);

        html.append("<head>").append(NEW_LINE_SEPARATOR);
        html.append("<meta charset=\"UTF-8\">").append(NEW_LINE_SEPARATOR);
        html.append("<title>Title</title>").append(NEW_LINE_SEPARATOR);
        html.append("</head>").append(NEW_LINE_SEPARATOR);

        html.append("<body>").append(NEW_LINE_SEPARATOR);

        html.append("<table>").append(NEW_LINE_SEPARATOR);


        html.append("<thead>").append(NEW_LINE_SEPARATOR);
        html.append("<th>created</th>").append(NEW_LINE_SEPARATOR);
        html.append("<th>updated</th>").append(NEW_LINE_SEPARATOR);
        html.append("<th>url</th>").append(NEW_LINE_SEPARATOR);
        html.append("<th>id</th>").append(NEW_LINE_SEPARATOR);
        html.append("<th>name</th>").append(NEW_LINE_SEPARATOR);
        html.append("<th>eshopUuid</th>").append(NEW_LINE_SEPARATOR);
        html.append("<th>unit</th>").append(NEW_LINE_SEPARATOR);
        html.append("<th>unitValue</th>").append(NEW_LINE_SEPARATOR);
        html.append("<th>unitPackageCount</th>").append(NEW_LINE_SEPARATOR);
        html.append("<th>valid</th>").append(NEW_LINE_SEPARATOR);
        html.append("<th>confirmValidity</th>").append(NEW_LINE_SEPARATOR);
        html.append("</thead>").append(NEW_LINE_SEPARATOR);

        html.append("<tbody>").append(NEW_LINE_SEPARATOR);
        newProductFullDtoList.forEach((NewProductFullDto dto) -> html.append(buildRowFor(dto)).append(NEW_LINE_SEPARATOR));
        html.append("</tbody>").append(NEW_LINE_SEPARATOR);

        html.append("</table>").append(NEW_LINE_SEPARATOR);
        html.append("</body>").append(NEW_LINE_SEPARATOR);
        html.append("</html>").append(NEW_LINE_SEPARATOR);


        try {
            Files.write(Paths.get(sourceFolder, "aloha.html"), html.toString().getBytes(StandardCharsets.UTF_8));

        } catch (IOException e) {
            //TOOD error while exporting
            e.printStackTrace();
        }
        log.debug("export done");
    }

    public String buildRowFor(NewProductFullDto fullDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("<tr>").append(NEW_LINE_SEPARATOR);

        sb.append(TD_START_TAG).append(safeToString(fullDto.getCreated())).append(TD_END_TAG).append(NEW_LINE_SEPARATOR);
        sb.append(TD_START_TAG).append(safeToString(fullDto.getUpdated())).append(TD_END_TAG).append(NEW_LINE_SEPARATOR);
        sb.append(TD_START_TAG).append(safeToString(fullDto.getUrl())).append(TD_END_TAG).append(NEW_LINE_SEPARATOR);
        sb.append(TD_START_TAG).append(safeToString(fullDto.getId())).append(TD_END_TAG).append(NEW_LINE_SEPARATOR);
        sb.append(TD_START_TAG)
                .append("<a href ='")
                .append(safeToString(fullDto.getUrl()))
                .append("'>")
                .append(safeToString(fullDto.getName()))
                .append("</a")
                .append(TD_END_TAG).append(NEW_LINE_SEPARATOR);
        sb.append(TD_START_TAG).append(safeToString(fullDto.getEshopUuid())).append(TD_END_TAG).append(NEW_LINE_SEPARATOR);
        sb.append(TD_START_TAG).append(safeToString(fullDto.getUnit())).append(TD_END_TAG).append(NEW_LINE_SEPARATOR);
        sb.append(TD_START_TAG).append(safeToString(fullDto.getUnitValue())).append(TD_END_TAG).append(NEW_LINE_SEPARATOR);
        sb.append(TD_START_TAG).append(safeToString(fullDto.getUnitPackageCount())).append(TD_END_TAG).append(NEW_LINE_SEPARATOR);
        sb.append(TD_START_TAG).append(safeToString(fullDto.getValid())).append(TD_END_TAG).append(NEW_LINE_SEPARATOR);
        sb.append(TD_START_TAG).append(safeToString(fullDto.getConfirmValidity())).append(TD_END_TAG).append(NEW_LINE_SEPARATOR);
        sb.append("</tr>").append(NEW_LINE_SEPARATOR);
        return sb.toString();
    }
}
