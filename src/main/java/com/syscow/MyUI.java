package com.syscow;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import dictionary.OpenReader;

import java.io.IOException;

/**
 * This UI is the application entry point. A UI may either represent a browser window
 * (or tab) or some part of an HTML page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
public class MyUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final VerticalLayout layout = new VerticalLayout();

        final TextField link = new TextField();
        link.setCaption("Source link");
        link.setWidth("600");

        final TextField words = new TextField();
        words.setCaption("Source link");

        Button button = new Button("Click Me");
        button.addClickListener(e -> {
            try {
                OpenReader dictionary = new OpenReader(link.getValue(), Integer.valueOf(words.getValue()));
                TextArea collectedWords =
                        new TextArea("Collected words: " + link.getValue(), dictionary.createDictionary());
                collectedWords.setWidth("600");
                layout.addComponent(collectedWords);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        layout.addComponents(link, words, button);

        setContent(layout);
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
