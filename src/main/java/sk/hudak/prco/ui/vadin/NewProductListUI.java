package sk.hudak.prco.ui.vadin;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.data.HasValue;
import com.vaadin.navigator.View;
import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import org.vaadin.dialogs.ConfirmDialog;
import sk.hudak.prco.dto.newproduct.NewProductFilterUIDto;
import sk.hudak.prco.dto.newproduct.NewProductFullDto;
import sk.hudak.prco.model.NewProductEntity;
import sk.hudak.prco.service.UIService;

import java.util.List;
import java.util.Optional;

@DesignRoot
public class NewProductListUI extends VerticalLayout implements View/*extends UI*/ {

    private UIService service;

    private Grid<NewProductFullDto> grid;

    public NewProductListUI(UIService service) {
//        setSizeFull();
        this.service = service;
        this.grid = new Grid<>(NewProductFullDto.class);

        // build layout

        Button productListBt = new Button("ProductEntity");
        productListBt.addClickListener(clickEvent -> {
            getUI().getNavigator().navigateTo(ProductListUI.class.getSimpleName());
        });

        Button groupUi = new Button("CreateGroup");
        groupUi.addClickListener(clickEvent -> {
            getUI().getNavigator().navigateTo(CreateGroupUI.class.getSimpleName());
        });

        HorizontalLayout entityNameLayout = new HorizontalLayout(new Label(NewProductEntity.class.getSimpleName()),
                productListBt,
                groupUi);


        HorizontalLayout buttonBar = new HorizontalLayout();

        VerticalLayout mainLayout = new VerticalLayout(entityNameLayout, buttonBar, grid);
        addComponent(mainLayout);


        grid.setHeight(300, Unit.PIXELS);
        grid.setWidth(100, Unit.PERCENTAGE);

        grid.setColumns("eshopUuid");

        grid.addColumn(fullDto -> "<a href='" + fullDto.getUrl() + "' target='_blank'>" + fullDto.getName() + "</a>", new HtmlRenderer());

        grid.addColumn("valid");
        grid.addColumn("confirmValidity");

        grid.addColumn(fullDto -> "Confirm",
                new ButtonRenderer<>(clickEvent -> {
                    NewProductFullDto dto = clickEvent.getItem();
                    service.confirmUnitDataForNewProduct(dto.getId());
                    internalSetData(loadData((null)));

                    Notification notification = new Notification("Product " + dto.getId() + " mark as confirm.");
                    notification.setDelayMsec(2 * 1000);
                    notification.show(Page.getCurrent());
                }));

        grid.addColumn(fullDto -> "try reprocess unit data",
                new ButtonRenderer<>(clickEvent -> {
                    NewProductFullDto dto = clickEvent.getItem();
                    service.tryToRepairInvalidUnitForNewProductByReprocessing(dto.getId());
                    internalSetData(loadData((null)));
                }));

        grid.addColumn(fullDto -> "Interested",
                new ButtonRenderer<>(clickEvent -> {
                    NewProductFullDto dto = clickEvent.getItem();
                    service.markNewProductAsInterested(dto.getId());
                    internalSetData(loadData((null)));
                }));

        grid.addColumn(fullDto -> "Not interested",
                new ButtonRenderer<>(clickEvent -> {
                    NewProductFullDto dto = clickEvent.getItem();
                    service.markNewProductAsNotInterested(dto.getId());
                    internalSetData(loadData((null)));
                }));


        grid.addColumn("unit");
        grid.addColumn("unitValue");
        grid.addColumn("unitPackageCount");
        grid.addColumn("url");
        grid.addColumn("id");

        grid.asSingleSelect().addValueChangeListener(e -> {
            onSingleSelection(e);
        });


        // Listen changes made by the editor, refresh data from backend
//        editor.setChangeHandler(() -> {
//            editor.setVisible(false);
//            listCustomers(filter.getValue());
//        });

        // Initialize listing
        internalSetData(loadData(null));
    }


    private void onBtAddNewClicked(Button.ClickEvent e) {

    }

    private List<NewProductFullDto> loadData(String filterValue) {
//        System.out.println("load data filter: " + filterValue);

//        NewProductFullDto dto1 = NewProductFullDto.builder().id(1l).name("pampers").url("http://google.sk").build();
//        NewProductFullDto dto2 = NewProductFullDto.builder().id(2l).name("nutrilon").url("http://feedo.sk").build();
//
//        return Arrays.asList(dto1, dto2);

        return service.findNewProducts(new NewProductFilterUIDto());
    }

    private void onSingleSelection(HasValue.ValueChangeEvent<NewProductFullDto> e) {
        NewProductFullDto value = e.getValue();
        System.out.println("on singleSelection click");
    }

    private void onFilterValueChange(String value) {
//        System.out.println("onFilterValueChange " + value);
        internalSetData(loadData((value)));
    }

    private void internalSetData(List<NewProductFullDto> data) {
        grid.setItems(data);

        //FIXME pagging
//        grid.setDataProvider(
//                new Grid.FetchItemsCallback<NewProductFullDto>() {
//                    @Override
//                    public Stream<NewProductFullDto> fetchItems(List<QuerySortOrder> sortOrder, int offset, int limit) {
//                        return null;
//                    }
//                },
//                new SerializableSupplier<Integer>() {
//                    @Override
//                    public Integer get() {
//                        return null;
//                    }
//                }
//        );
        //grid.getDataProvider().refreshAll();
    }

    private abstract class MyClickButtonListener implements Button.ClickListener {
        @Override
        public void buttonClick(Button.ClickEvent clickEvent) {
            Optional<NewProductFullDto> firstSelectedItem = grid.getSelectionModel().getFirstSelectedItem();
            if (!firstSelectedItem.isPresent()) {
                Notification.show("Pls select any product first!");
                return;
            }
            NewProductFullDto dto = firstSelectedItem.get();
            // The quickest way to confirm
            ConfirmDialog.show(getUI(), windowCaption(dto), message(dto), okCaption(dto), cancelCaption(dto), dialog -> {
                if (dialog.isConfirmed()) {
                    onYes(dto);
                }
            });
        }

        protected String okCaption(NewProductFullDto dto) {
            return "Ok";
        }

        protected String cancelCaption(NewProductFullDto dto) {
            return "Zrusit";
        }

        protected String windowCaption(NewProductFullDto dto) {
            return "Naozaj?";
        }

        abstract String message(NewProductFullDto dto);

        abstract void onYes(NewProductFullDto dto);
    }
}
