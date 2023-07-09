package de.nmauer.views.workerview;

import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import de.nmauer.data.entity.Worker;
import de.nmauer.data.entity.timeMapping.WorkingHour;
import de.nmauer.data.entity.timeMapping.WorkingMonth;
import de.nmauer.data.service.WorkerService;
import de.nmauer.data.service.timeTracking.WorkingHourService;
import jakarta.annotation.security.RolesAllowed;
import org.hibernate.jdbc.Work;

import java.util.Arrays;
import java.util.stream.Stream;


@Route(value = "worker")
@RolesAllowed({"USER", "ADMIN"})
public class WorkerView extends VerticalLayout implements HasDynamicTitle, HasUrlParameter<Integer> {

    private final WorkerService workerService;
    private final WorkingHourService workingHourService;
    private Worker worker;
    private Grid<WorkingMonth> grid;
    private GridListDataView<WorkingMonth> dataView;

    public WorkerView(WorkerService workerService, WorkingHourService workingHourService){
        this.workerService = workerService;
        this.workingHourService = workingHourService;
    }

    public void setup(){
        worker = workerService.getWorkerById(worker.getId());
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        H2 header = new H2(worker.getName());
        horizontalLayout.add(header);

        grid = new Grid<>();

        Grid.Column<WorkingMonth> sort = grid.addColumn(WorkingMonth::getSorting);
        Grid.Column<WorkingMonth> monthAndYear = grid.addColumn(createMonthYearRenderer()).setHeader("Monat");
        Grid.Column<WorkingMonth> hours = grid.addColumn(WorkingMonth::getWorkingHoursSum).setHeader("Minuten");
        Grid.Column<WorkingMonth> buttons = grid.addColumn(createButtonRenderer());

        sort.setVisible(false);

        Stream.of(monthAndYear, hours).forEach(col -> col.setResizable(true));
        monthAndYear.setWidth("100px");

        GridSortOrder<WorkingMonth> order = new GridSortOrder<>(sort, SortDirection.DESCENDING);
        grid.sort(Arrays.asList(order));

        dataView = grid.setItems(workingHourService.getWorkingMonth(worker.getId()));
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setItemDetailsRenderer(createWorkerViewDetailRendererGrid());
        add(horizontalLayout, grid);
        setSizeFull();
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, Integer workerId) {
        this.worker = workerService.getWorkerById(workerId);
        setup();
    }
    @Override
    public String getPageTitle() {
        return worker.getName();
    }

    public ComponentRenderer<Span, WorkingMonth> createMonthYearRenderer(){
        return new ComponentRenderer<>(Span::new, (span, workingHour)->{
            span.setText(workingHour.getMonthName() + " " + workingHour.getYear());
        });
    }

    public ComponentRenderer<Div, WorkingMonth> createButtonRenderer(){
        return new ComponentRenderer<>(Div::new, (div, workingHour)->{
            HorizontalLayout btnLayout = new HorizontalLayout();
            Button showDetailsBtn = new Button("Details anzeigen");
            Button exportCSVBtn = new Button("Export als CSV");

            btnLayout.setJustifyContentMode(JustifyContentMode.END);
            btnLayout.add(showDetailsBtn, exportCSVBtn);

            div.add(btnLayout);
        });
    }

    public ComponentRenderer<VerticalLayout, WorkingMonth> createWorkerViewDetailRendererGrid() {
        return new ComponentRenderer<>(VerticalLayout::new, (layout, workingMonth) -> {
            for(WorkingHour workingHour: workingMonth.getWorkingHours()){
                HorizontalLayout horizontalLayout = new HorizontalLayout();
                Board board = new Board();


                NativeLabel day = new NativeLabel(workingHour.getDay() + "." + workingHour.getMonth() + "." + workingHour.getYear());
                NativeLabel time = new NativeLabel(workingHour.getMinutes() + "");

                horizontalLayout.add(day, time);
                layout.add(horizontalLayout);
            }
        });
    }
    public Div createCell(String text){
        Div div = new Div();
        div.setText(text);
        div.addClassNames("cell", "color");

        return div;
    }
    public class WorkerViewDetailRenderer extends Div{
        public Grid<WorkingHour> grid;
        public GridListDataView<WorkingHour> dataView;

        public WorkerViewDetailRenderer(){
            grid = new Grid<>();
            Grid.Column<WorkingHour> day = grid.addColumn(WorkingHour::getDay).setHeader("Tag");
            Grid.Column<WorkingHour> month = grid.addColumn(WorkingHour::getMonth).setHeader("Monat");
            Grid.Column<WorkingHour> year = grid.addColumn(WorkingHour::getYear).setHeader("Jahr");
            Grid.Column<WorkingHour> minutes = grid.addColumn(WorkingHour::getMinutes).setHeader("Minuten");

            Stream.of(day, month, year, minutes).forEach(col -> col.setResizable(true));


            add(grid);
        }

        public void setGrid(WorkingMonth workingMonth){
            dataView = grid.setItems(workingMonth.getWorkingHours());
        }

    }


}
