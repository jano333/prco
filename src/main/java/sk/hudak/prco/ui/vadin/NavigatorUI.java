package sk.hudak.prco.ui.vadin;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import sk.hudak.prco.service.UIService;

@SpringUI
public class NavigatorUI extends UI {

    @Autowired
    private UIService service;

    @Getter
    private Navigator navigator;

    @Override
    protected void init(VaadinRequest request) {
        getPage().setTitle("Navigation Example");

        // Create a navigator to control the views
        navigator = new Navigator(this, this);

        // Create and register the views
        navigator.addView("", new NewProductListUI(service));
        navigator.addView(ProductListUI.class.getSimpleName(), new ProductListUI(service));
        navigator.addView(CreateGroupUI.class.getSimpleName(), new CreateGroupUI(service));
    }
}
