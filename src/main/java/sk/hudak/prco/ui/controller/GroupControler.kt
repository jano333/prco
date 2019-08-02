package sk.hudak.prco.ui.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import sk.hudak.prco.service.UIService

@Controller
class GroupControler(uiService: UIService) : BasicController(uiService) {

    //TODO zatial sa nepouziva

    @RequestMapping("/groups/withoutProduct/{id}")
    fun groupsWithoutProducts(@PathVariable id: Long?, model: Model) {
        val groupsWithoutProduct = uiService.getGroupsWithoutProduct(id)
        model.addAttribute("groupsWithoutProduct", groupsWithoutProduct)
    }
}
