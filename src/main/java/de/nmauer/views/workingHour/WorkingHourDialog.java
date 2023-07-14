package de.nmauer.views.workingHour;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import de.nmauer.data.DateType;
import de.nmauer.data.entity.Worker;
import de.nmauer.data.entity.timeMapping.WorkingHour;
import de.nmauer.data.service.WorkerService;
import de.nmauer.data.service.timeTracking.WorkingHourService;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.ZoneOffset;
import java.time.temporal.TemporalUnit;

public class WorkingHourDialog extends Dialog {
    private final WorkerService workerService;
    private final WorkingHourService workingHourService;
    public Select<Worker> worker;
    public Select<DateType> workingType;
    public H3 header;
    public DateTimePicker startField, endField;
    public Button saveBtn, cancelBtn;

    public WorkingHourDialog(WorkerService workerService, WorkingHourService workingHourService, WorkingHourView workingHourView) {
        this.workerService = workerService;
        this.workingHourService = workingHourService;
        VerticalLayout layout = new VerticalLayout();
        header = new H3("Stunden Hinzufügen");
        add(header);
        worker = new Select<>();
        workingType =  new Select<>();
        workingType.setLabel("ToDo"); //todo
        worker.setLabel("Mitarbeiter");
        worker.setItems(workerService.findAllWorkers());
        worker.setItemLabelGenerator(Worker::getName);

        workingType.setItems(DateType.values());
        workingType.setItemLabelGenerator(DateType::getTitle);
        workingType.setValue(DateType.WORKING_DAY);

        startField = new DateTimePicker("Begin");
        startField.setStep(Duration.ofMinutes(15));
        endField = new DateTimePicker("Ende");
        endField.setStep(Duration.ofMinutes(15));


        saveBtn = new Button("Hinzufügen");
        saveBtn.addThemeName("success");
        cancelBtn =  new Button("Abbrechen");
        cancelBtn.setThemeName("error");
        cancelBtn.addClickListener(event -> close());

        saveBtn.addClickListener(click -> {
           if(worker.isEmpty() || startField.isEmpty()  || endField.isEmpty() || workingType.isEmpty()){
               Notification.show("Bitte füllen Sie alle Felder aus!");
            }else{
               Timestamp start = new Timestamp(startField.getValue().toInstant(ZoneOffset.UTC).toEpochMilli() - (3600000 *2));
               Timestamp end = new Timestamp(endField.getValue().toInstant(ZoneOffset.UTC).toEpochMilli() - (3600000 *2));
               WorkingHour workingHour = new WorkingHour( worker.getValue().getId(), start, end, start.toLocalDateTime().getDayOfMonth(), start.getMonth() +1 , start.getYear()+1900, workingType.getValue());
               workingHourService.addWorkingHour(workingHour);
               workingHourView.refreshGrid();
               close();
           }
        });

        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.add(new HorizontalLayout(worker, workingType), new HorizontalLayout(startField), endField);

        getFooter().add(cancelBtn, saveBtn);
        add(layout);

    }


}

