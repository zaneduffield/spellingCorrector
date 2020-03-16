package main.java;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public interface CorrectionClient<C extends JComponent> {
    Point getPopupLocation(C invoker);

    void setSelectedText(C invoker, String selected_value);

    boolean isValidWord(String word);

    ArrayList<String> getSuggestions(C invoker);
}
