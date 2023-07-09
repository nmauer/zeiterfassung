package de.nmauer.views.workerOverview;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import de.nmauer.data.entity.Worker;

import java.util.ArrayList;
import java.util.List;

public class ColumnVisibilityDialog extends Dialog {

    private MultiSelectListBox<Grid.Column<Worker>> columnSelect;

    public ColumnVisibilityDialog(WorkerOverview workerOverview) {

        columnSelect = new MultiSelectListBox<>();
        columnSelect.setItems(workerOverview.grid.getColumns());

        columnSelect.setItemLabelGenerator(Grid.Column::getHeaderText);

        List<Grid.Column<Worker>> selected = new ArrayList<>();
        workerOverview.grid.getColumns().forEach(workerColumn -> {
            if(workerColumn.isVisible())
                selected.add(workerColumn);
        });
        columnSelect.select(selected);

        columnSelect.addValueChangeListener(selectColumnComponentValueChangeEvent -> {
            workerOverview.grid.getColumns().forEach(workerColumn -> {
                workerColumn.setVisible(columnSelect.isSelected(workerColumn));
            });
        });

        add(columnSelect);
    }
}
