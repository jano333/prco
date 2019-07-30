package sk.hudak.prco.ui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import sk.hudak.prco.service.UIService;

public abstract class BasicController {

    @Autowired
    private UIService uiService;

    public UIService getUiService() {
        return uiService;
    }
}
