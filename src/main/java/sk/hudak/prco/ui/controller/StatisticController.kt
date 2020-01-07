package sk.hudak.prco.ui.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import sk.hudak.prco.service.UIService
import sk.hudak.prco.ui.MvcConstants
import javax.servlet.http.HttpServletResponse

@Controller
class StatisticController(uiService: UIService) : BasicController(uiService) {

    @RequestMapping("/executorStatistics")
    fun listNewProducts(response: HttpServletResponse): ModelAndView {
        val executorStatistic = uiService.getExecutorStatistic()

        val modelAndView = ModelAndView(MvcConstants.VIEW_EXECUTOR_STATISTICS,
                "executorStatistics",
                executorStatistic)

        // nastavim aby sa stranka refresovala automaticky kazdych X sekund...
        response.addHeader("Refresh", "3")
        return modelAndView;
    }
}