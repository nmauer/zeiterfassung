package de.nmauer.views.timeClock;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;
import de.nmauer.data.Role;
import de.nmauer.data.entity.timeMapping.WorkingHour;
import de.nmauer.data.service.WorkerService;
import de.nmauer.data.service.timeTracking.TimeTrackingService;
import de.nmauer.security.AuthenticatedUser;
import jakarta.annotation.security.PermitAll;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;

@PageTitle("Stempel Uhr")
@Route(value = "time-clock")
@RouteAlias(value = "")
@PermitAll
@Uses(Icon.class)
public class TimeClockView extends VerticalLayout {

    private NativeLabel welcomeText, dateText, timeText;

    private NativeLabel loginTime, time;

    private Button loginBtn, logoutBtn;

    public TimeClockView(AuthenticatedUser authenticatedUser, TimeTrackingService timeTrackingService) {
        if(authenticatedUser.get().get().getRoles().contains(Role.ADMIN)){
            UI.getCurrent().getPage().setLocation("worker-overview");
        }
        welcomeText = new NativeLabel();
        dateText = new NativeLabel();
        timeText = new NativeLabel();

        H2 header = new H2("Stempel-Uhr");
        H4 status = new H4();
        addClassName("time-clock.css");

        loginBtn = new Button("Einstempeln");
        logoutBtn = new Button("Ausstempeln");

        loginBtn.setWidth("75%");
        logoutBtn.setWidth("75%");
        loginBtn.setHeight("50px");
        logoutBtn.setHeight("50px");
        setAlignItems(Alignment.CENTER);
        loginBtn.addClickListener(buttonClickEvent -> {
            timeTrackingService.login(authenticatedUser.get().get().getId());
            UI.getCurrent().getPage().reload();
        });




        logoutBtn.addClickListener(buttonClickEvent -> {
           timeTrackingService.logout(authenticatedUser.get().get().getId());
            UI.getCurrent().getPage().reload();
        });
        welcomeText.setText("Guten Tag, "  + authenticatedUser.get().get().getName());
        dateText.setText("Datum: " + new SimpleDateFormat("dd.MM.YYYY").format(Timestamp.from(Instant.now())));
        timeText.setText("Uhrzeit: " + new SimpleDateFormat("HH:mm").format(Timestamp.from(Instant.now())));
        //welcomeText.getStyle().set("font-size", "var(--lumo-font-size-m)");


        loginBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        logoutBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);

        VerticalLayout dateLayout = new VerticalLayout();
        dateLayout.setAlignItems(Alignment.CENTER);
        dateLayout.add(welcomeText, dateText, timeText);
        dateLayout.setSpacing(false);
        dateLayout.setMargin(false);

        add(header);
        add(dateLayout);
        add(status);

        loginTime = new NativeLabel();
        time = new NativeLabel();

        if(timeTrackingService.isUserLoggedIn(authenticatedUser.get().get().getId())){
            H4 a = new H4();
            a.setText("Eingestempelt");
            a.addClassName("time-clock-color2");
            HorizontalLayout l = new HorizontalLayout();
            l.addClassName("horizontal_layout");
            status.setText("Status: ");
            l.add(status, a);
            add(l);
            add(logoutBtn);



            loginTime.setText("Eingestempelt seit: " + new SimpleDateFormat("HH:mm").format(timeTrackingService.getLogInTime(authenticatedUser.get().get().getId())));

            Timestamp workingTime = new Timestamp(System.currentTimeMillis() - timeTrackingService.getLogInTime(authenticatedUser.get().get().getId()).getTime());
            String minutes = new SimpleDateFormat("mm").format(workingTime);
            String hours = String.valueOf(Integer.parseInt(new SimpleDateFormat("HH").format(workingTime))-1);
            time.setText(String.format("%s Stunden und %s Minuten", hours, minutes));




            add(loginTime);
            add(time);
        }else{

            H4 a = new H4();
            a.setText("Nicht eingestempelt");
            a.addClassName("time-clock-color");
            HorizontalLayout l = new HorizontalLayout();
            l.addClassName("horizontal_layout");
            status.setText("Status: ");
            l.add(status, a);
            add(l);
            add(loginBtn);
        }
    }



}
