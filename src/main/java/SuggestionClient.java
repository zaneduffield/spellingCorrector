package main.java;

import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.ArrayList;

/**
 * Handles word location and replacement in the document in order to provide services to be used by a text suggestion
 * decorator
 * @param <C> A Swing JTextComponent
 */
public interface SuggestionClient<C extends JTextComponent> {
    /**
     * finds the location to create the suggestion popup at
     * @param component the component to find the point in
     * @return the Point in the document where the popup will be created
     */
    Point getPopupLocation(C component);

    /**
     * performs the replacement with suggestion
     * @param component the component to perform the replacement inside
     * @param suggestion the new value for the word
     */
    void setSelectedText(C component, String suggestion);

    /**
     * finds suggestions given in the component
     * @param component the component to find suggestions in
     * @return the suggestions found
     */
    ArrayList<String> getSuggestions(C component);

    boolean isValidWord(String word);
}
