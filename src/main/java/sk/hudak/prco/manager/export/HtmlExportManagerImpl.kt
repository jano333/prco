package sk.hudak.prco.manager.export

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.dto.product.NewProductFullDto
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

@Component
class HtmlExportManagerImpl : AbstractExportImportManagerImpl(), HtmlExportManager {

    companion object {
        val log = LoggerFactory.getLogger(HtmlExportManagerImpl::class.java)!!

        private val TD_START_TAG = "<td>"
        private val TD_END_TAG = "</td>"
    }

    override fun buildHtml() {
        val newProductFullDtoList = internalTxService!!.findNewProductsForExport()

        val html = StringBuilder()
        html.append("<!DOCTYPE html>").append(NEW_LINE_SEPARATOR)
        html.append("<html lang=\"en\">").append(NEW_LINE_SEPARATOR)

        html.append("<head>").append(NEW_LINE_SEPARATOR)
        html.append("<meta charset=\"UTF-8\">").append(NEW_LINE_SEPARATOR)
        html.append("<title>Title</title>").append(NEW_LINE_SEPARATOR)
        html.append("</head>").append(NEW_LINE_SEPARATOR)

        html.append("<body>").append(NEW_LINE_SEPARATOR)

        html.append("<table>").append(NEW_LINE_SEPARATOR)


        html.append("<thead>").append(NEW_LINE_SEPARATOR)
        html.append("<th>created</th>").append(NEW_LINE_SEPARATOR)
        html.append("<th>updated</th>").append(NEW_LINE_SEPARATOR)
        html.append("<th>url</th>").append(NEW_LINE_SEPARATOR)
        html.append("<th>id</th>").append(NEW_LINE_SEPARATOR)
        html.append("<th>name</th>").append(NEW_LINE_SEPARATOR)
        html.append("<th>eshopUuid</th>").append(NEW_LINE_SEPARATOR)
        html.append("<th>unit</th>").append(NEW_LINE_SEPARATOR)
        html.append("<th>unitValue</th>").append(NEW_LINE_SEPARATOR)
        html.append("<th>unitPackageCount</th>").append(NEW_LINE_SEPARATOR)
        html.append("<th>valid</th>").append(NEW_LINE_SEPARATOR)
        html.append("<th>confirmValidity</th>").append(NEW_LINE_SEPARATOR)
        html.append("</thead>").append(NEW_LINE_SEPARATOR)

        html.append("<tbody>").append(NEW_LINE_SEPARATOR)
        newProductFullDtoList.forEach { dto: NewProductFullDto -> html.append(buildRowFor(dto)).append(NEW_LINE_SEPARATOR) }
        html.append("</tbody>").append(NEW_LINE_SEPARATOR)

        html.append("</table>").append(NEW_LINE_SEPARATOR)
        html.append("</body>").append(NEW_LINE_SEPARATOR)
        html.append("</html>").append(NEW_LINE_SEPARATOR)


        try {
            Files.write(Paths.get(sourceFolder!!, "aloha.html"), html.toString().toByteArray(StandardCharsets.UTF_8))

        } catch (e: IOException) {
            //TOOD error while exporting
            e.printStackTrace()
        }

        log.debug("export done")
    }

    fun buildRowFor(fullDto: NewProductFullDto): String {
        val sb = StringBuilder()
        sb.append("<tr>").append(NEW_LINE_SEPARATOR)

        sb.append(TD_START_TAG).append(safeToString(fullDto.created)).append(TD_END_TAG).append(NEW_LINE_SEPARATOR)
        sb.append(TD_START_TAG).append(safeToString(fullDto.updated)).append(TD_END_TAG).append(NEW_LINE_SEPARATOR)
        sb.append(TD_START_TAG).append(safeToString(fullDto.url)).append(TD_END_TAG).append(NEW_LINE_SEPARATOR)
        sb.append(TD_START_TAG).append(safeToString(fullDto.id)).append(TD_END_TAG).append(NEW_LINE_SEPARATOR)
        sb.append(TD_START_TAG)
                .append("<a href ='")
                .append(safeToString(fullDto.url))
                .append("'>")
                .append(safeToString(fullDto.name))
                .append("</a")
                .append(TD_END_TAG).append(NEW_LINE_SEPARATOR)
        sb.append(TD_START_TAG).append(safeToString(fullDto.eshopUuid)).append(TD_END_TAG).append(NEW_LINE_SEPARATOR)
        sb.append(TD_START_TAG).append(safeToString(fullDto.unit)).append(TD_END_TAG).append(NEW_LINE_SEPARATOR)
        sb.append(TD_START_TAG).append(safeToString(fullDto.unitValue)).append(TD_END_TAG).append(NEW_LINE_SEPARATOR)
        sb.append(TD_START_TAG).append(safeToString(fullDto.unitPackageCount)).append(TD_END_TAG).append(NEW_LINE_SEPARATOR)
        sb.append(TD_START_TAG).append(safeToString(fullDto.valid)).append(TD_END_TAG).append(NEW_LINE_SEPARATOR)
        sb.append(TD_START_TAG).append(safeToString(fullDto.confirmValidity)).append(TD_END_TAG).append(NEW_LINE_SEPARATOR)
        sb.append("</tr>").append(NEW_LINE_SEPARATOR)
        return sb.toString()
    }
}
