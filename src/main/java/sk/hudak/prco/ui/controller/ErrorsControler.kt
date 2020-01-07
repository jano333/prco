package sk.hudak.prco.ui.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import sk.hudak.prco.dto.ErrorFindFilterDto
import sk.hudak.prco.service.UIService
import sk.hudak.prco.ui.MvcConstants.VIEW_ERRORS
import javax.servlet.http.HttpServletResponse

@Controller
class ErrorsControler(uiService: UIService) : BasicController(uiService) {

    @RequestMapping("/errors")
    fun listNewProducts(response: HttpServletResponse): ModelAndView {
        val errorFindFilterDto = ErrorFindFilterDto()
        errorFindFilterDto.statusCodesToSkip = arrayOf("404")
        errorFindFilterDto.maxCountPerEshop = 3
        val errors = uiService.findErrorsByFilter(errorFindFilterDto)

        val modelAndView = ModelAndView(VIEW_ERRORS, "errors", errors)

        // nastavim aby sa stranka refresovala automaticky kazdych X sekund...
        response.addHeader("Refresh", "10")
        return modelAndView
    }
}