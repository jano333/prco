package sk.hudak.prco.ui.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import sk.hudak.prco.service.UIService
import sk.hudak.prco.ui.MvcConstants
import javax.servlet.http.HttpServletResponse

@Controller
class EshopAdminController(uiService: UIService) : BasicController(uiService) {

    @RequestMapping("/eshopsAdmin")
    fun listEshopAdmin(response: HttpServletResponse): ModelAndView {
        val eshopAdminDataList = uiService.getEshopsAdminData()

        val modelAndView = ModelAndView(MvcConstants.VIEW_ESHOPS_ADMIN,
                "eshopsAdmin",
                eshopAdminDataList)

        // nastavim aby sa stranka refresovala automaticky kazdych X sekund...
//        response.addHeader("Refresh", "3")
        return modelAndView;
    }
}