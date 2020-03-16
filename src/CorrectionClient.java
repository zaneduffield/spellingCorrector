import javax.swing.*;
import javax.swing.text.AttributeSet;
import java.awt.*;
import java.util.ArrayList;

public interface CorrectionClient<C extends JComponent> {
    Point getPopupLocation(C invoker);

    void setSelectedText(C invoker, String selected_value);

    void decorateInvalidWord(C invoker, AttributeSet attrs);

    void clearWordAttributes(C invoker);

    boolean isValidWord(C invoker);

    ArrayList<String> getSuggestions(C invoker);
}
