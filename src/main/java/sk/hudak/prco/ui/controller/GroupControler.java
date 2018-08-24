package sk.hudak.prco.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import sk.hudak.prco.dto.group.GroupListDto;

import java.util.List;

@Controller
public class GroupControler extends BasicController {

    //TODO zatial sa nepouziva

    @RequestMapping("/groups/withoutProduct/{id}")
    public void groupsWithoutProducts(@PathVariable Long id, Model model) {
        List<GroupListDto> groupsWithoutProduct = getUiService().getGroupsWithoutProduct(id);
        model.addAttribute("groupsWithoutProduct", groupsWithoutProduct);
    }
}
