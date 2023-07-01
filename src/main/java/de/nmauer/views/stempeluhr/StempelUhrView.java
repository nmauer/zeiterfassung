package de.nmauer.views.stempeluhr;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Stempel Uhr")
@Route(value = "time-clock")
@RouteAlias(value = "")
@RolesAllowed("USER")
@Uses(Icon.class)
public class StempelUhrView extends Composite<VerticalLayout> {

    private Paragraph textMedium = new Paragraph();

    private Button buttonPrimary = new Button();

    private Button buttonPrimary2 = new Button();

    public StempelUhrView() {
        getContent().setHeightFull();
        getContent().setWidthFull();
        textMedium.setText("Status Nicht eingestempelt");
        textMedium.getStyle().set("font-size", "var(--lumo-font-size-m)");
        buttonPrimary.setText("Einstempeln");
        buttonPrimary.setWidthFull();
        buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonPrimary2.setText("Ausstempeln");
        buttonPrimary2.setWidthFull();
        buttonPrimary2.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getContent().add(textMedium);
        getContent().add(buttonPrimary);
        getContent().add(buttonPrimary2);
    }
}
