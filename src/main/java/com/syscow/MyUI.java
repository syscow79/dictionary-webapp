package com.syscow;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import dictionary.LinkReader;

import java.util.ArrayList;
import java.util.List;

@Theme("mytheme")
public class MyUI extends UI {

    private List<LinkReader> linkReaders = new ArrayList<>();

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final VerticalLayout layout = new VerticalLayout();

        final TextField link = new TextField();
        link.setCaption("Source link");
        link.setWidth("600");

        final TextField words = new TextField();
        words.setCaption("Word");

        Button button = new Button("Click Me");
        button.addClickListener(e -> {
            readLinkAndAddToLayout(layout, link.getValue(), words.getValue());
        });

        Button buttonReadLinsToo = new Button("Read Links Too");
        buttonReadLinsToo.addClickListener(e -> {
            LinkReader dictionary = readLinkAndAddToLayout(layout,  link.getValue(), words.getValue());
            dictionary.getLinks().forEach(alink -> {
                readLinkAndAddToLayout(layout, alink, words.getValue());
            });
        });

        layout.addComponents(link, words, button, buttonReadLinsToo);

        setContent(layout);
    }

    private LinkReader readLinkAndAddToLayout(VerticalLayout layout, String link, String words) {
        LinkReader dictionary = new LinkReader(link, Integer.valueOf(words));

        TextArea collectedWords =
                new TextArea("Collected words: " + link, dictionary.createDictionary());
        collectedWords.setWidth("600");

        LinkReader newWordsDictionary = new LinkReader(link, Integer.valueOf(words), linkReaders);
        TextArea collectedNewWords = new TextArea("new words", newWordsDictionary.createNewWordsDictionary());
        collectedNewWords.setWidth("600");
        layout.addComponent(new HorizontalLayout(collectedWords, collectedNewWords));
        linkReaders.add(dictionary);

        return dictionary;
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
