package sk.hudak.prco.ui.controller;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import sk.hudak.prco.service.UIService;

public abstract class BasicController {

    @Autowired
    @Getter
    private UIService uiService;


}
