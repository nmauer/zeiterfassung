package de.nmauer.views.workingHour;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.nmauer.data.entity.Worker;
import de.nmauer.data.entity.timeMapping.WorkingHour;
import de.nmauer.data.service.WorkerService;
import de.nmauer.data.service.timeTracking.WorkingHourService;
import jakarta.annotation.security.RolesAllowed;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.function.Consumer;
import java.util.stream.Stream;

@PageTitle("Stundenübersicht")
@Route(value = "working_time_view")
@RolesAllowed("USER")
public class WorkingHourView extends VerticalLayout {

    private final WorkingHourService workingHourService;
    private final WorkerService workerService;
    public Grid<WorkingHour> grid;
    public H2 header;
    public Grid.Column<WorkingHour> worker, year, month,
            day, minutes, start, end;

    public GridListDataView<WorkingHour> dataView;
    public Button editBtn, removeBtn, addBtn;

    public WorkingHourView(WorkingHourService workingHourService, WorkerService workerService) {
        this.workingHourService = workingHourService;
        this.workerService = workerService;
        grid = new Grid<>();
        header = new H2("Stundenübersicht");
        addBtn = new Button(VaadinIcon.PLUS.create());
        addBtn.addClickListener(event -> {
            WorkingHourDialog dialog = new WorkingHourDialog(workerService, workingHourService, this);
            dialog.open();
        });

        worker = grid.addColumn(createWorkerNameRender());
        minutes = grid.addColumn(WorkingHour::getWorkingTime);
        day = grid.addColumn(WorkingHour::getDay);
        month = grid.addColumn(createWorkerMonthRenderer());
        year = grid.addColumn(WorkingHour::getYear);
        start = grid.addColumn(createLoginTimeRender());
        end = grid.addColumn(createLogoutTimeRender());

        Stream.of(worker, minutes, day, month, year).forEach(column->{
            column.setResizable(true);
        });

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        dataView = grid.setItems(workingHourService.getAllWorkingHours());

        WorkingHourFilter hourFilter = new WorkingHourFilter(dataView);

        grid.getHeaderRows().clear();
        HeaderRow headerRow = grid.appendHeaderRow();
        WorkingHourContextMenu contextMenu = new WorkingHourContextMenu(grid);

        headerRow.getCell(worker).setComponent(createFilterHeader("Mitarbeiter", hourFilter::setFullName));
        headerRow.getCell(minutes).setComponent(createFilterHeader("Stunden", hourFilter::setMinutes));
        headerRow.getCell(day).setComponent(createFilterHeader("Tag", hourFilter::setDay));
        headerRow.getCell(month).setComponent(createFilterHeader("Monat", hourFilter::setMonth));
        headerRow.getCell(year).setComponent(createFilterHeader("Jahr", hourFilter::setYear));
        headerRow.getCell(start).setComponent(createFilterHeader("Begin", hourFilter::setLoginTime));
        headerRow.getCell(end).setComponent(createFilterHeader("Ende", hourFilter::setLogoutTime));

        add(new HorizontalLayout(header, addBtn), grid);
        setSizeFull();
    }
    public void refreshGrid(){
        dataView.removeItems(dataView.getItems().toList());
        dataView.addItems(workingHourService.getAllWorkingHours());
        dataView.refreshAll();
    }

    private ComponentRenderer<Span, WorkingHour> createWorkerNameRender() {
        return new ComponentRenderer<>(Span::new, (span, workingHour) -> {
            span.setText(String.format("%s", workerService.getWorkerById((int) workingHour.getUser_id()).getName()));
        });
    }

    private ComponentRenderer<Span, WorkingHour> createLoginTimeRender() {
        return new ComponentRenderer<>(Span::new, (span, workingHour) -> {
            span.setText(new SimpleDateFormat("HH:mm").format(workingHour.getLoginDate()));
        });
    }

    private ComponentRenderer<Span, WorkingHour> createLogoutTimeRender() {
        return new ComponentRenderer<>(Span::new, (span, workingHour) -> {
            span.setText(new SimpleDateFormat("HH:mm").format(workingHour.getLogoutDate()));
        });
    }

    private ComponentRenderer<Span, WorkingHour> createWorkerMonthRenderer() {
        return new ComponentRenderer<>(Span::new, (span, workingHour) -> {
            switch (workingHour.getMonth()){
                case 1:
                    span.setText(String.format("%s", "Januar"));
                    break;
                case 2:
                    span.setText(String.format("%s", "Februar"));
                    break;
                case 3:
                    span.setText(String.format("%s", "März"));
                    break;
                case 4:
                    span.setText(String.format("%s", "April"));
                    break;
                case 5:
                    span.setText(String.format("%s", "Mai"));
                    break;
                case 6:
                    span.setText(String.format("%s", "Juni"));
                    break;
                case 7:
                    span.setText(String.format("%s", "Juli"));
                    break;
                case 8:
                    span.setText(String.format("%s", "August"));
                    break;
                case 9:
                    span.setText(String.format("%s", "September"));
                    break;
                case 10:
                    span.setText(String.format("%s", "Oktober"));
                    break;
                case 11:
                    span.setText(String.format("%s", "November"));
                    break;
                case 12:
                    span.setText(String.format("%s", "Dezember"));
                    break;
            };
        });
    }
    public Component createFilterHeader(String labelText,
                                                Consumer<String> filterChangeConsumer) {
        NativeLabel label = new NativeLabel(labelText);
        label.getStyle().set("padding-top", "var(--lumo-space-m)")
                .set("font-size", "var(--lumo-font-size-xs)");
        TextField textField = new TextField();
        textField.setValueChangeMode(ValueChangeMode.EAGER);
        textField.setClearButtonVisible(true);
        textField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        textField.setWidthFull();
        textField.getStyle().set("max-width", "100%");
        textField.addValueChangeListener(
                e -> filterChangeConsumer.accept(e.getValue()));
        VerticalLayout layout = new VerticalLayout(label, textField);
        layout.getThemeList().clear();
        layout.getThemeList().add("spacing-xs");

        return layout;
    }
    private class WorkingHourFilter {
        private final GridListDataView<WorkingHour> dataView;

        private String fullName;
        private String day;
        private String year;
        private String month;
        private String minutes;
        private String loginTime, logoutTime;


        public WorkingHourFilter(GridListDataView<WorkingHour> dataView) {
            this.dataView = dataView;
            this.dataView.addFilter(this::test);
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
            this.dataView.refreshAll();
        }

        public void setDay(String day) {
            this.day = day;
            this.dataView.refreshAll();
        }

        public void setYear(String year) {
            this.year = year;
            this.dataView.refreshAll();
        }

        public void setMonth(String month) {
            this.month = month;
            this.dataView.refreshAll();
        }

        public void setMinutes(String minutes) {
            this.minutes = minutes;
            this.dataView.refreshAll();
        }

        public void setLoginTime(String loginTime) {
            this.loginTime = loginTime;
            this.dataView.refreshAll();
        }

        public void setLogoutTime(String logoutTime) {
            this.logoutTime = logoutTime;
            this.dataView.refreshAll();
        }


        public boolean test(WorkingHour hour) {

            boolean matchesFullName = matches(workerService.getWorkerById((int) hour.getUser_id()).getName(), fullName);
            boolean matchesDay = matches(String.valueOf(hour.getDay()), day);
            boolean matchesYear = matches(String.valueOf(hour.getYear()), year);
            boolean matchesMonth = matches(String.valueOf(hour.getMonth()), month);
            boolean matchesLoginTime = matches(String.valueOf(loginTime), loginTime);
            boolean matchesLogoutTime = matches(String.valueOf(logoutTime), logoutTime);

            if(!matchesMonth){
                switch (hour.getMonth()) {
                    case 1:
                        matchesMonth = "januar".contains(month.toLowerCase());
                        break;
                    case 2:
                        matchesMonth = "februar".contains(month.toLowerCase());
                        break;
                    case 3:
                        matchesMonth = "märz".contains(month.toLowerCase());
                        break;
                    case 4:
                        matchesMonth = "april".contains(month.toLowerCase());
                        break;
                    case 5:
                        matchesMonth = "mai".contains(month.toLowerCase());
                        break;
                    case 6:
                        matchesMonth = "juni".contains(month.toLowerCase());
                        break;
                    case 7:
                        matchesMonth = "juli".contains(month.toLowerCase());
                        break;
                    case 8:
                        matchesMonth = "august".contains(month.toLowerCase());
                        break;
                    case 9:
                        matchesMonth = "september".contains(month.toLowerCase());
                        break;
                    case 10:
                        matchesMonth = "oktober".contains(month.toLowerCase());
                        break;
                    case 11:
                        matchesMonth = "november".contains(month.toLowerCase());
                        break;
                    case 12:
                        matchesMonth = "dezember".contains(month.toLowerCase());
                        break;
                }
            }

            boolean matchesMinutes = matches(String.valueOf(hour.getWorkingTime()), minutes);

            return matchesFullName && matchesDay && matchesYear && matchesMonth && matchesMinutes && matchesLoginTime && matchesLogoutTime;
        }

        private boolean matches(String value, String searchTerm) {
            return searchTerm == null || searchTerm.isEmpty()
                    || value.toLowerCase().contains(searchTerm.toLowerCase());
        }
    }
    private class WorkingHourContextMenu extends GridContextMenu<WorkingHour> {
        public WorkingHourContextMenu(Grid<WorkingHour> target) {
            super(target);

            GridMenuItem<WorkingHour> nameItem = addItem("",
                    e -> e.getItem().ifPresent(workingHour -> {
                        UI.getCurrent().getPage().open(String.format("worker/%s", workingHour.getUser_id()));
                    }));
            add(new Hr());
            addItem("Edit", e -> e.getItem().ifPresent(workingHour -> {
                EditWorkingHourDialog dialog = new EditWorkingHourDialog(workingHour, workingHourService, WorkingHourView.this);
                dialog.open();
            }));
            addItem("Delete", e -> e.getItem().ifPresent(workingHour -> {
                workingHourService.deleteWorkingHour(workingHour);
                refreshGrid();
            }));
            add(new Hr());

            GridMenuItem<WorkingHour> dateItem = addItem("",
                    e -> e.getItem().ifPresent(workingHour -> {
                        ((TextField)day.getHeaderComponent().getChildren().toList().get(1)).setValue(String.valueOf(workingHour.getDay()));
                        ((TextField)month.getHeaderComponent().getChildren().toList().get(1)).setValue(String.valueOf(workingHour.getMonth()));
                        ((TextField)year.getHeaderComponent().getChildren().toList().get(1)).setValue(String.valueOf(workingHour.getYear()));
                    }));
            setDynamicContentHandler(workingHour -> {
                // Do not show context menu when header is clicked
                if (workingHour == null)
                    return false;
                nameItem.setText(String.format("%s", workerService.getWorkerById((int) workingHour.getUser_id()).getName()));
                dateItem.setText(String.format("%s.%s.%s", workingHour.getDay(), workingHour.getMonth(), workingHour.getYear()));
                return true;
            });
        }
    }

}
