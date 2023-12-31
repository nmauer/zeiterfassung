package de.nmauer.views.workingHour;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import de.nmauer.data.DateType;
import de.nmauer.data.entity.timeMapping.WorkingHour;
import de.nmauer.data.service.timeTracking.WorkingHourService;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.ZoneOffset;

public class EditWorkingHourDialog extends Dialog {

    public H2 header;
    public DateTimePicker startField, endField;
    public Button saveButton, cancelButton;
    public Select<DateType> workingType;

    public EditWorkingHourDialog(WorkingHour workingHour, WorkingHourService workingHourService, WorkingHourView workingHourView) {
        VerticalLayout layout = new VerticalLayout();
        header = new H2("Stunden Bearbeiten");
        startField = new DateTimePicker("Begin");
        endField = new DateTimePicker("Ende");
        saveButton = new Button("Speichern");
        saveButton.setThemeName("success");
        workingType = new Select<>();
        workingType.setLabel("ToDo"); // ToDo
        workingType.setWidth("100%");
        workingType.setItems(DateType.values());
        workingType.setItemLabelGenerator(DateType::getTitle);
        workingType.setValue(DateType.WORKING_DAY);
        saveButton.addClickListener(event -> {
            Timestamp start = new Timestamp(startField.getValue().toInstant(ZoneOffset.UTC).toEpochMilli() - (3600000 * 2));
            Timestamp end = new Timestamp(endField.getValue().toInstant(ZoneOffset.UTC).toEpochMilli() - (3600000 * 2));
            workingHour.setLoginDate(start);
            workingHour.setLogoutDate(end);
            workingHour.setDay(start.toLocalDateTime().getDayOfMonth());
            workingHour.setMonth(start.getMonth() + 1);
            workingHour.setYear(start.getYear() + 1900);
            workingHour.setDateType(workingType.getValue());
            workingHourService.update(workingHour);
            workingHourView.refreshGrid();
            close();
        });
        cancelButton =  new Button("Abbrechen");
        cancelButton.addClickListener(buttonClickEvent -> close());
        cancelButton.setThemeName("error");

        startField.setStep(Duration.ofMinutes(15));
        endField.setStep(Duration.ofMinutes(15));

        startField.setValue(workingHour.getLoginDate().toLocalDateTime());
        endField.setValue(workingHour.getLogoutDate().toLocalDateTime());


        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.add(workingType,startField, endField);
        getFooter().add(cancelButton, saveButton);
        add(header, layout);
    }
}
