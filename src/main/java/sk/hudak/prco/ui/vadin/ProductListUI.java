package sk.hudak.prco.ui.vadin;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.contextmenu.GridContextMenu;
import com.vaadin.navigator.View;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.HtmlRenderer;
import org.vaadin.dialogs.ConfirmDialog;
import sk.hudak.prco.dto.product.ProductFilterUIDto;
import sk.hudak.prco.dto.product.ProductFullDto;
import sk.hudak.prco.model.ProductEntity;
import sk.hudak.prco.service.UIService;

import java.util.List;
import java.util.Optional;

@DesignRoot
public class ProductListUI extends VerticalLayout implements View {

    private final Grid<ProductFullDto> grid;

    private UIService service;

    public ProductListUI(UIService service) {
        this.service = service;
        setSizeFull();

        this.grid = new Grid<>(ProductFullDto.class);

        Button btDelete = new Button("Delete", new MyClickButtonListener() {
            @Override
            String message(ProductFullDto dto) {
                return "Do you really want to remove product: " + dto.getName() + "?";
            }

            @Override
            protected String okCaption(ProductFullDto dto) {
                return "Vymazat!";
            }

            @Override
            void onYes(ProductFullDto dto) {
                service.removeProduct(dto.getId());
                internalSetData(loadData((null)));
            }
        });

        // build layout
        HorizontalLayout entityNameLayout = new HorizontalLayout(new Label(ProductEntity.class.getSimpleName()));
        HorizontalLayout buttonBar = new HorizontalLayout(btDelete);

        VerticalLayout mainLayout = new VerticalLayout(entityNameLayout, buttonBar, grid);
        addComponent(mainLayout);

        grid.setHeight(300, Unit.PIXELS);
        grid.setWidth(1300, Unit.PIXELS);

        grid.setColumns("eshopUuid");

        grid.addColumn(fullDto -> "<a href='" + fullDto.getUrl() + "' target='_blank'>" + fullDto.getName() + "</a>", new HtmlRenderer());

        grid.addColumn("unit");
        grid.addColumn("unitValue");
        grid.addColumn("unitPackageCount");
        grid.addColumn("url");
        grid.addColumn("id");

//        grid.addComponentColumn(fullDto -> {
//            ComboBox<GroupListDto> select = new ComboBox<>("Select Group");
//            select.setItems(service.getGroupsWithoutProduct(fullDto.getId()));
//            select.setItemCaptionGenerator(GroupListDto::getName);
//
//            select.addValueChangeListener(event -> {
//                GroupListDto group = event.getValue();
//                // TODO
//                System.out.println(group.getId() + " - " + group.getName());
//            });
//            //TODO
////            select.setSelectedItem();
//
//            return select;
//        });

        // Initialize listing
        internalSetData(loadData(null));
    }

    private String getText(GridContextMenu.GridContextMenuOpenListener.GridContextMenuOpenEvent<ProductFullDto> e) {
        ProductFullDto item = (ProductFullDto) e.getItem();

        System.out.println("iterm id " + item.getId());

        return "haha" + item.getId();
    }

    private void internalSetData(List<ProductFullDto> data) {
        grid.setItems(data);
    }

    private List<ProductFullDto> loadData(String filterValue) {
        return service.findProducts(new ProductFilterUIDto());
    }

    private abstract class MyClickButtonListener implements Button.ClickListener {
        @Override
        public void buttonClick(Button.ClickEvent clickEvent) {
            Optional<ProductFullDto> firstSelectedItem = grid.getSelectionModel().getFirstSelectedItem();
            if (!firstSelectedItem.isPresent()) {
                Notification.show("Pls select any product first!");
                return;
            }
            ProductFullDto dto = firstSelectedItem.get();
            // The quickest way to confirm
            ConfirmDialog.show(getUI(), windowCaption(dto), message(dto), okCaption(dto), cancelCaption(dto), dialog -> {
                if (dialog.isConfirmed()) {
                    onYes(dto);
                }
            });
        }

        protected String okCaption(ProductFullDto dto) {
            return "Ok";
        }

        protected String cancelCaption(ProductFullDto dto) {
            return "Zrusit";
        }

        protected String windowCaption(ProductFullDto dto) {
            return "Naozaj?";
        }

        abstract String message(ProductFullDto dto);

        abstract void onYes(ProductFullDto dto);
    }
}
