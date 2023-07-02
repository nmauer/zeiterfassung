package de.nmauer.views.logout;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.nmauer.security.AuthenticatedUser;
import jakarta.annotation.security.PermitAll;

@Route(value = "logout")
@PermitAll
public class LogoutView extends VerticalLayout {
    public LogoutView(AuthenticatedUser authenticatedUser){
        authenticatedUser.logout();
    }
}
