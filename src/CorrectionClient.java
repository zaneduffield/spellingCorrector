import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import java.awt.*;
import java.util.ArrayList;

public interface CorrectionClient<C extends JComponent> {
    Point getPopupLocation(C invoker);

    void setSelectedText(C invoker, String selected_value);

    void decorateInvalidWord(JTextPane tp, AttributeSet attrs);

    void undecorateValidWord(JTextPane tp);

    boolean isValidWord(C invoker);

    ArrayList<String> getSuggestions(C invoker);
}
