package de.nmauer.views.workerOverview;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.nmauer.data.entity.Worker;
import de.nmauer.data.service.WorkerService;
import de.nmauer.security.AuthenticatedUser;
import jakarta.annotation.security.RolesAllowed;

import java.util.stream.Stream;

@PageTitle("Mitarbeiter Übersicht")
@Route(value = "worker-overview")
@RolesAllowed({"ADMIN", "OWNER"})
public class WorkerOverview extends VerticalLayout {

    private final AuthenticatedUser authenticatedUser;
    private final WorkerService workerService;
    private HorizontalLayout filterHeader;
    private TextField searchField;
    private Button columnVisibilityBtn;
    private Button addBtn;

    public Grid<Worker> grid;
    public GridListDataView<Worker> dataView;
    public Grid.Column<Worker> nameColumn,
            usernameColumn,
            streetColumn,
            cityColumn,
            zipcodeColumn,
            phoneNumberColumn,
            mobileNumberColumn,
            emailColumn,
            buttonColumn;

    public WorkerOverview(AuthenticatedUser authenticatedUser, WorkerService workerService) {
        this.authenticatedUser = authenticatedUser;
        this.workerService = workerService;
        setSizeFull();

        filterHeader = new HorizontalLayout();

        searchField = new TextField();
        searchField.setPlaceholder("Suchen...");
        columnVisibilityBtn = new Button("Spalten ein-/ausblenden");
        addBtn = new Button(new Icon(VaadinIcon.PLUS));
        addBtn.addThemeVariants(ButtonVariant.LUMO_ICON);

        grid = new Grid<>();
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        dataView = grid.setItems(workerService.findAllWorkers());

        dataView.addFilter(worker -> {
            if(searchField.isEmpty())
                return true;

            String searchTerm = searchField.getValue();

            boolean matchesName = matches(worker.getName(), searchTerm);
            boolean matchesUsername = matches(worker.getUsername(), searchTerm);
            boolean matchesStreet = matches(worker.getStreet(), searchTerm);
            boolean matchesCity = matches(worker.getCity(), searchTerm);
            boolean matchesZipcode = matches(worker.getZipcode(), searchTerm);
            boolean matchesPhoneNumber = matches(worker.getPhoneNumber(), searchTerm);
            boolean matchesMobileNumber = matches(worker.getMobileNumber(), searchTerm);
            boolean matchesEmail = matches(worker.getEmail(), searchTerm);

            return matchesName || matchesUsername || matchesStreet || matchesCity || matchesZipcode || matchesPhoneNumber
                    || matchesMobileNumber || matchesEmail;
        });

        nameColumn = grid.addColumn(Worker::getName).setHeader("Name");
        usernameColumn = grid.addColumn(Worker::getUsername).setHeader("Username");
        streetColumn = grid.addColumn(Worker::getStreet).setHeader("Straße");
        cityColumn = grid.addColumn(Worker::getCity).setHeader("Stadt");
        zipcodeColumn = grid.addColumn(Worker::getZipcode).setHeader("PLZ");
        phoneNumberColumn = grid.addColumn(Worker::getPhoneNumber).setHeader("Telefonnummer");
        mobileNumberColumn = grid.addColumn(Worker::getMobileNumber).setHeader("Handynummer");
        emailColumn = grid.addColumn(Worker::getEmail).setHeader("E-Mail");
        buttonColumn = grid.addColumn(createButtonRenderer());

        Stream.of(nameColumn, usernameColumn, streetColumn, cityColumn, zipcodeColumn, phoneNumberColumn, mobileNumberColumn, emailColumn).forEach(workerColumn -> {
            workerColumn.setVisible(false);
            workerColumn.setSortable(true);
            workerColumn.setAutoWidth(true);
            workerColumn.setResizable(true);
        });

        Stream.of(nameColumn, mobileNumberColumn, emailColumn).forEach(workerColumn -> {
            workerColumn.setVisible(true);
        });

        buttonColumn.setAutoWidth(true);

        columnVisibilityBtn.addClickListener(buttonClickEvent -> {
            new ColumnVisibilityDialog(this).open();
        });

        searchField.addValueChangeListener(textFieldStringComponentValueChangeEvent -> dataView.refreshAll());

        filterHeader.add(searchField, columnVisibilityBtn, addBtn);

        addBtn.addClickListener(buttonClickEvent -> {
            Button saveBtn = new Button("Speichern");
            Button cancelBtn = new Button("Abbrechen");
            cancelBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);

            Dialog dialog = new Dialog();
            UserDataLayout userDataLayout = new UserDataLayout(saveBtn, dialog);

            dialog.setWidth("60%");

            cancelBtn.addClickListener(buttonClickEvent1 -> {
                dialog.close();
            });

            dialog.getFooter().add(cancelBtn, saveBtn);

            dialog.setHeaderTitle("Mitarbeiter anlegen");

            dialog.add(userDataLayout);
            dialog.open();
        });

        add(filterHeader);
        add(grid);
    }

    private boolean matches(String value, String searchTerm){
        if(value == null || searchTerm.isEmpty())
            return false;
        return value.toLowerCase().contains(searchTerm.toLowerCase());
    }

    private void refreshGrid(){
        dataView.removeItems(dataView.getItems().toList());
        dataView.addItems(workerService.findAllWorkers());
        dataView.refreshAll();
    }

    public ComponentRenderer<Span, Worker> createButtonRenderer() {
        return new ComponentRenderer<>(Span::new, (span, worker) -> {
            Button showWorkingHoursBtn = new Button("Zur Stundenübersicht");
            Button editUserBtn = new Button("Mitarbeiter bearbeiten");
            Button deleteUserBtn = new Button("Mitarbeiter löschen");

            // todo password reset button

            Stream.of(showWorkingHoursBtn, editUserBtn, deleteUserBtn).forEach(button -> {
                button.setVisible(true);
                button.setEnabled(true);

                button.addClassName("button-margin-style");

                span.add(button);
            });

            deleteUserBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);

            deleteUserBtn.addClickListener(buttonClickEvent -> {
                ConfirmDialog dialog = new ConfirmDialog();

                dialog.setHeader("Benutzer löschen");
                dialog.setText(String.format("Sicher das Sie den Benutzer %s löschen möchten?", worker.getName()));

                dialog.setCancelable(false);
                dialog.setRejectable(true);

                dialog.setConfirmText("Löschen");
                dialog.setRejectText("Abbrechen");

                dialog.addConfirmListener(confirmEvent -> {
                    workerService.delete(worker);
                });
                dialog.addRejectListener(rejectEvent -> {
                    dialog.close();
                });

                dialog.open();
            });

            editUserBtn.addClickListener(buttonClickEvent -> {
                Button saveBtn = new Button("Speichern");
                Button cancelBtn = new Button("Abbrechen");
                cancelBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);

                Dialog dialog = new Dialog();
                UserDataLayout userDataLayout = new UserDataLayout(saveBtn, dialog);
                userDataLayout.setWorker(worker);

                dialog.setWidth("60%");

                cancelBtn.addClickListener(buttonClickEvent1 -> {
                    dialog.close();
                });

                dialog.getFooter().add(cancelBtn, saveBtn);

                dialog.setHeaderTitle("Mitarbeiter anlegen");

                dialog.add(userDataLayout);
                dialog.open();
            });
        });
    }

    public class UserDataLayout extends FormLayout {

        public TextField nameField,
                usernameField,
                addressStreetField,
                addressCityField,
                addressZipcodeField,
                contactPhoneNumberField,
                contactMobileNumberField,
                contactEmailField;

        private Worker worker;

        public PasswordField passwordField, confirmPasswordField;

        private boolean update;

        public UserDataLayout(Button saveBtn, Dialog dialog) {
            update = false;

            nameField = new TextField("Name");
            usernameField = new TextField("Benutzername");
            addressStreetField = new TextField("Straße");
            addressCityField = new TextField("Stadt");
            addressZipcodeField = new TextField("PLZ");
            contactPhoneNumberField = new TextField("Telefonnummer");
            contactMobileNumberField = new TextField("Handynummer");
            contactEmailField = new TextField("E-Mail");

            passwordField = new PasswordField("Passwort");
            confirmPasswordField = new PasswordField("Passwort wiederholen");

            Stream.of(nameField, usernameField, contactMobileNumberField, contactEmailField, contactPhoneNumberField,
                    addressStreetField, addressZipcodeField, addressCityField, passwordField, confirmPasswordField).forEach(textField -> {
                textField.setEnabled(true);
                textField.setVisible(true);
                add(textField);
            });

            setResponsiveSteps(new ResponsiveStep("0", 5));

            setColspan(nameField, 3);
            setColspan(usernameField, 2);
            setColspan(addressStreetField, 5);
            setColspan(addressZipcodeField, 2);
            setColspan(addressCityField, 3);
            setColspan(contactMobileNumberField, 2);
            setColspan(contactEmailField, 2);
            setColspan(passwordField, 3);
            setColspan(confirmPasswordField, 2);

            saveBtn.addClickListener(buttonClickEvent -> {
                if(update){
                    if(nameField.isEmpty() || usernameField.isEmpty()){
                        Notification.show("Bitte füllen Sie alle Pflichtfelder aus!");
                    }

                    worker.setName(nameField.getValue() != null? nameField.getValue() : "");
                    worker.setPhoneNumber(contactPhoneNumberField.getValue() != null? nameField.getValue() : "");
                    worker.setMobileNumber(contactMobileNumberField.getValue() != null? nameField.getValue() : "");
                    worker.setEmail(contactEmailField.getValue() != null? nameField.getValue() : "");
                    worker.setStreet(addressStreetField.getValue() != null? nameField.getValue() : "");
                    worker.setCity(addressCityField.getValue() != null? nameField.getValue() : "");
                    worker.setZipcode(addressZipcodeField.getValue() != null? nameField.getValue() : "");

                    workerService.updateWorker(worker);
                }else{
                    if(nameField.isEmpty() || usernameField.isEmpty() || passwordField.isEmpty() || confirmPasswordField.isEmpty()){
                        Notification.show("Bitte füllen Sie alle Pflichtfelder aus!");
                    }

                    if(passwordField.getValue().equals(confirmPasswordField.getValue())){
                        Worker newWorker = new Worker(
                                -1,
                                nameField.getValue(),
                                usernameField.getValue(),
                                passwordField.getValue(),
                                contactPhoneNumberField.getValue(),
                                contactMobileNumberField.getValue(),
                                contactEmailField.getValue(),
                                addressStreetField.getValue(),
                                addressCityField.getValue(),
                                addressZipcodeField.getValue()
                        );

                        workerService.createWorker(newWorker);
                        dialog.close();
                    }else{
                        Notification.show("Die Passwörter stimmen nicht überein!");
                    }
                }
            });
        }

        public void setWorker(Worker worker){
            this.worker = worker;
            update = true;

            nameField.setValue(worker.getName() != null ? worker.getName(): "");
            usernameField.setValue(worker.getUsername() != null ? worker.getUsername(): "");
            addressStreetField.setValue(worker.getStreet() != null ? worker.getStreet(): "");
            addressCityField.setValue(worker.getCity() != null ? worker.getCity(): "");
            addressZipcodeField.setValue(worker.getZipcode() != null ? worker.getZipcode(): "");
            contactPhoneNumberField.setValue(worker.getPhoneNumber() != null ? worker.getPhoneNumber(): "");
            contactMobileNumberField.setValue(worker.getMobileNumber() != null ? worker.getMobileNumber(): "");
            contactEmailField.setValue(worker.getEmail() != null ? worker.getEmail(): "");

            remove(passwordField, confirmPasswordField);
        }
    }

}
