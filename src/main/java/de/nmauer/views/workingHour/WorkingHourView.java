package de.nmauer.views.workingHour;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.nmauer.data.entity.timeMapping.WorkingHour;
import de.nmauer.data.service.WorkerService;
import de.nmauer.data.service.timeTracking.WorkingHourService;
import jakarta.annotation.security.RolesAllowed;

import java.util.function.Consumer;
import java.util.stream.Stream;

@PageTitle("Stundenübersicht")
@Route(value = "working_time_view")
@RolesAllowed("USER")
public class WorkingHourView extends VerticalLayout {

    private final WorkingHourService workingHourService;
    private final WorkerService workerService;
    public Grid<WorkingHour> grid;
    public Grid.Column<WorkingHour> worker, year, month,
            day, minutes;

    public GridListDataView<WorkingHour> dataView;

    public WorkingHourView(WorkingHourService workingHourService, WorkerService workerService) {
        this.workingHourService = workingHourService;
        this.workerService = workerService;
        grid = new Grid<>();
        worker = grid.addColumn(createWorkerNameRender());
        minutes = grid.addColumn(WorkingHour::getMinutes);
        day = grid.addColumn(WorkingHour::getDay);
        month = grid.addColumn(createWorkerMonthRenderer());
        year = grid.addColumn(WorkingHour::getYear);




        Stream.of(worker, minutes, day, month, year).forEach(column->{
            column.setResizable(true);
        });

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        dataView = grid.setItems(workingHourService.getAllWorkingHours());

        WorkingHourFilter hourFilter = new WorkingHourFilter(dataView);

        grid.getHeaderRows().clear();
        HeaderRow headerRow = grid.appendHeaderRow();

        headerRow.getCell(worker).setComponent(createFilterHeader("Mitarbeiter", hourFilter::setFullName));
        headerRow.getCell(minutes).setComponent(createFilterHeader("Minuten", hourFilter::setMinutes));
        headerRow.getCell(day).setComponent(createFilterHeader("Tag", hourFilter::setDay));
        headerRow.getCell(month).setComponent(createFilterHeader("Monat", hourFilter::setMonth));
        headerRow.getCell(year).setComponent(createFilterHeader("Jahr", hourFilter::setYear));


        add(grid);
        setSizeFull();
    }
    public void refreshGrid(){
        dataView.removeItems(dataView.getItems().toList());
        dataView.refreshAll();
    }

    private ComponentRenderer<Span, WorkingHour> createWorkerNameRender() {
        return new ComponentRenderer<>(Span::new, (span, workingHour) -> {
            span.setText(String.format("%s", workerService.getWorkerById((int) workingHour.getUser_id()).getName()));
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

        public boolean test(WorkingHour hour) {

            boolean matchesFullName = matches(workerService.getWorkerById((int) hour.getUser_id()).getName(), fullName);
            boolean matchesDay = matches(String.valueOf(hour.getDay()), day);
            boolean matchesYear = matches(String.valueOf(hour.getYear()), year);
            boolean matchesMonth = matches(String.valueOf(hour.getMonth()), month);

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



            boolean matchesMinutes = matches(String.valueOf(hour.getMinutes()), minutes);

            return matchesFullName && matchesDay && matchesYear && matchesMonth && matchesMinutes;
        }

        private boolean matches(String value, String searchTerm) {
            return searchTerm == null || searchTerm.isEmpty()
                    || value.toLowerCase().contains(searchTerm.toLowerCase());
        }
    }

}
