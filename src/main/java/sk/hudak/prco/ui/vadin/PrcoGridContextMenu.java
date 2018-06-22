package sk.hudak.prco.ui.vadin;

import com.vaadin.contextmenu.GridContextMenu;
import com.vaadin.ui.Grid;
import lombok.Getter;
import lombok.Setter;

public class PrcoGridContextMenu<T> extends GridContextMenu<T> {

    @Getter
    @Setter
    private T value;

    public PrcoGridContextMenu(Grid parentComponent) {
        super(parentComponent);
    }
}
