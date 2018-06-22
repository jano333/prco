package sk.hudak.prco.ui.vadin;

import com.vaadin.navigator.View;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import sk.hudak.prco.dto.group.GroupCreateDto;
import sk.hudak.prco.service.UIService;

public class CreateGroupUI extends VerticalLayout implements View {

    private transient UIService service;

    private final TextField tfName;
    private final Button btSave;

    public CreateGroupUI(UIService service) {
        this.service = service;
        setSizeFull();

        tfName = new TextField();
        HorizontalLayout hlName = new HorizontalLayout(new Label("Name"), tfName);

        btSave = new Button("Save");
        btSave.addClickListener(event -> {
            GroupCreateDto createDto = new GroupCreateDto();
            createDto.setName(tfName.getValue());
            service.createGroup(createDto);
        });

        HorizontalLayout hlActions = new HorizontalLayout(btSave);

        VerticalLayout mainLayout = new VerticalLayout(hlName, hlActions);
        addComponent(mainLayout);
    }
}
