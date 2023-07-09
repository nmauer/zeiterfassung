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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;


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
        Grid.Column<WorkingMonth> hours = grid.addColumn(WorkingMonth::getWorkingHoursSum).setHeader("Minuten");
        Grid.Column<WorkingMonth> buttons = grid.addColumn(createButtonRenderer());

        sort.setVisible(false);

        Stream.of(monthAndYear, hours).forEach(col -> col.setResizable(true));
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

    public ComponentRenderer<Div, WorkingMonth> createButtonRenderer(){
        return new ComponentRenderer<>(Div::new, (div, workingHour)->{
            Grid<WorkingHour> monthGrid = new Grid<>();
            Grid.Column<WorkingHour> date = monthGrid.addColumn(WorkingHour::getDay).setHeader("Datum");
            Grid.Column<WorkingHour> start = monthGrid.addColumn(WorkingHour::getMonth).setHeader("Beginn");
            Grid.Column<WorkingHour> end = monthGrid.addColumn(WorkingHour::getYear).setHeader("Ende");
            Grid.Column<WorkingHour> minutes = monthGrid.addColumn(WorkingHour::getMinutes).setHeader("Minuten");
            GridListDataView<WorkingHour> monthDataView = monthGrid.setItems(workingHourService.getWorkingHourByUserId(worker.getId()));

            HorizontalLayout btnLayout = new HorizontalLayout();
            Button showDetailsBtn = new Button("Details anzeigen");
            showDetailsBtn.addClickListener(event -> {
                grid.setDetailsVisible(workingHour,!grid.isDetailsVisible(workingHour));
            });
            Button exportCSVBtn = new Button("Export als CSV");
            exportCSVBtn.addClickListener(event -> {
                export(monthGrid);
            });

            btnLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
            btnLayout.add(showDetailsBtn, exportCSVBtn);

            div.add(btnLayout);
        });
    }

    public ComponentRenderer<Div, WorkingMonth> createWorkerViewDetailRendererGrid() {
        return new ComponentRenderer<>(Div::new, (layout, workingMonth) -> {
            createBoard();

            for(WorkingHour workingHour: workingMonth.getWorkingHours()){
                NativeLabel day = new NativeLabel();
                NativeLabel time = new NativeLabel();
                if(workingHour.getDay() <= 9){
                    day.setText("0"+workingHour.getDay() + "." + workingHour.getMonth() + "." + workingHour.getYear());
                }else if(workingHour.getMonth() <= 9){
                    time.setText("0"+workingHour.getMinutes() + "");
                }else{
                    day.setText(workingHour.getDay() + "." + workingHour.getMonth() + "." + workingHour.getYear());
                }
                time.setText(workingHour.getMinutes() + "");
                board.addRow(createCell(day.getText()), createCell(time.getText()), createCell("Beginn"), createCell("Ende"));
                addClassName("board-view");
                layout.add(board);
            }
        });
    }
    public void createBoard(){
        board = new Board();
        board.addRow(createHeaderCell("Tag"),createHeaderCell("Minuten"), createHeaderCell("Beginn"), createHeaderCell("Ende"));
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

    public void export(Grid<WorkingHour> monthGrid){
        List<Format> formatList = new ArrayList<>();
        CsvFormat csvFormat = new CsvFormat();
        PdfFormat pdfFormat = new PdfFormat();
        XlsxFormat xlsxFormat =  new XlsxFormat();
        formatList.add(csvFormat);
        formatList.add(pdfFormat);
        formatList.add(xlsxFormat);

        GridExporter<WorkingHour> exporter = GridExporter.newWithDefaults(monthGrid);
        exporter.withFileName("Stundenliste_"+ worker.getName());
        exporter.withAvailableFormats(formatList);
        exporter.open();
    }


}
