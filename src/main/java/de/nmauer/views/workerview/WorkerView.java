package de.nmauer.views.workerview;

import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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
import software.xdev.vaadin.grid_exporter.GridExporter;
import software.xdev.vaadin.grid_exporter.format.Format;
import software.xdev.vaadin.grid_exporter.jasper.format.CsvFormat;
import software.xdev.vaadin.grid_exporter.jasper.format.PdfFormat;
import software.xdev.vaadin.grid_exporter.jasper.format.XlsxFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;
import java.util.List;

@Route(value = "worker")
@RolesAllowed({"USER", "ADMIN"})
public class WorkerView extends VerticalLayout implements HasDynamicTitle, HasUrlParameter<Integer> {

    private final WorkerService workerService;
    private Board board;
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
        Grid.Column<WorkingMonth> hours = grid.addColumn(WorkingMonth::getWorkingHoursSum).setHeader("Stunden");
        Grid.Column<WorkingMonth> usedVacationDays = grid.addColumn(WorkingMonth::getVacationDayAmount).setHeader("Verwendete Urlaubstage");
        Grid.Column<WorkingMonth> buttons = grid.addColumn(createButtonRenderer());

        sort.setVisible(false);

        Stream.of(monthAndYear, hours, usedVacationDays).forEach(col -> col.setResizable(true));
        monthAndYear.setWidth("100px");

        GridSortOrder<WorkingMonth> order = new GridSortOrder<>(sort, SortDirection.DESCENDING);
        grid.sort(Arrays.asList(order));
        grid.setDetailsVisibleOnClick(false);

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
    private Grid.Column<WorkingHour> date;
    private Grid.Column<WorkingHour> start;
    private Grid.Column<WorkingHour> end;
    private Grid.Column<WorkingHour> workingTime;
    private Grid.Column<WorkingHour> pauseTime;
    private Grid.Column<WorkingHour> workingType;

    public ComponentRenderer<Div, WorkingMonth> createButtonRenderer(){
        return new ComponentRenderer<>(Div::new, (div, workingMonth)->{
            Grid<WorkingHour> monthGrid = new Grid<>();
            date = monthGrid.addColumn(WorkingHour::getDate).setHeader("Datum");
            start = monthGrid.addColumn(WorkingHour::getLoginDate).setHeader("Begin");
            end = monthGrid.addColumn(WorkingHour::getLogoutDate).setHeader("Ende");
            workingTime = monthGrid.addColumn(WorkingHour::getWorkingTime).setHeader("Stunden");
            pauseTime = monthGrid.addColumn(WorkingHour::getPauseTime).setHeader("Pausenzeit");
            workingType = monthGrid.addColumn(WorkingHour::getDateType).setHeader("ToDo"); // ToDo
            GridListDataView<WorkingHour> monthDataView = monthGrid.setItems(workingHourService.getWorkingHourByUserId(worker.getId(), workingMonth.getMonth(), workingMonth.getYear()));

            HorizontalLayout btnLayout = new HorizontalLayout();
            Button showDetailsBtn = new Button("Details anzeigen");
            showDetailsBtn.addClickListener(event -> {
                grid.setDetailsVisible(workingMonth,!grid.isDetailsVisible(workingMonth));
            });
            Button exportCSVBtn = new Button("Exportieren");
            exportCSVBtn.addClickListener(event -> {
                export(monthGrid, workingMonth);
            });

            btnLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
            btnLayout.add(showDetailsBtn, exportCSVBtn);

            div.add(btnLayout);
        });
    }

    public ComponentRenderer<Span, WorkingHour> createLoginTimeRender() {
        return new ComponentRenderer<>(Span::new, (span, workingHour) -> {
            span.setText(new SimpleDateFormat("HH:mm").format(workingHour.getLoginDate()));
        });
    }

    public ComponentRenderer<Span, WorkingHour> createLogoutTimeRender() {
        return new ComponentRenderer<>(Span::new, (span, workingHour) -> {
            span.setText(new SimpleDateFormat("HH:mm").format(workingHour.getLogoutDate()));
        });
    }

    public ComponentRenderer<Div, WorkingMonth> createWorkerViewDetailRendererGrid() {
        return new ComponentRenderer<>(Div::new, (layout, workingMonth) -> {
            createBoard();

            for(WorkingHour workingHour: workingMonth.getWorkingHours()){
                NativeLabel day = new NativeLabel();
                NativeLabel time = new NativeLabel();

                day.setText(workingHour.getDay() + "." + workingHour.getMonth() + "." + workingHour.getYear());
                time.setText(String.valueOf(workingHour.getWorkingTime()));
                board.addRow(createCell(day.getText()), createCell(time.getText() + " h"), createCell(new SimpleDateFormat("HH:mm").format(workingHour.getLoginDate()) + " Uhr - " + new SimpleDateFormat("HH:mm").format(workingHour.getLogoutDate()) + " Uhr"), createCell(workingHour.getDateType().toString()));
                addClassName("board-view");
                layout.add(board);
            }
        });
    }
    public void createBoard(){
        board = new Board();
        board.addRow(createHeaderCell("Tag"),createHeaderCell("Stunden"), createHeaderCell("Begin - Ende"), createHeaderCell("ToDo")); // ToDo
    }
    public Div createCell(String text){
        Div div = new Div();
        VerticalLayout layout = new VerticalLayout();
        NativeLabel label = new NativeLabel(text);
        layout.add(label);
        div.add(layout);
        return div;
    }
    public Div createHeaderCell(String text){
        Div div = new Div();
        VerticalLayout layout = new VerticalLayout();
        H5 label = new H5(text);
        layout.add(label);
        div.add(layout);
        return div;
    }

    public void export(Grid<WorkingHour> monthGrid, WorkingMonth workingMonth){
        List<Format> formatList = new ArrayList<>();
        CsvFormat csvFormat = new CsvFormat();
        PdfFormat pdfFormat = new PdfFormat();
        XlsxFormat xlsxFormat =  new XlsxFormat();
//        formatList.add(csvFormat);
        formatList.add(pdfFormat);
        formatList.add(xlsxFormat);

        GridExporter<WorkingHour> exporter = GridExporter.newWithDefaults(monthGrid);
        exporter.withFileName("Stundenliste-"+ workingMonth.getMonthName()+ "-"+ worker.getName().replace(" ", "_"));

        exporter.withAvailableFormats(formatList);
        exporter.open();
    }

}
