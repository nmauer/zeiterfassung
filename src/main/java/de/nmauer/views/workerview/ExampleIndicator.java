package de.nmauer.views.workerview;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;

@Tag("example-indicator")
public class ExampleIndicator extends Component {
    public ExampleIndicator(String title, String current) {
        this.getElement().setAttribute("title", title).setAttribute("current",
                current);
    }
}
