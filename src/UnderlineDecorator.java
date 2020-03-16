import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import java.awt.*;

public class UnderlineDecorator <C extends JTextComponent> {
    private final C component;
    private final StyledDocument doc;
    private final MutableAttributeSet invalidWordAttribute;
    private final CorrectionClient<C> correction_client;
    private boolean disabled = false;

    public UnderlineDecorator(C component, CorrectionClient<C> correction_client) {
        MutableAttributeSet attrs = new SimpleAttributeSet();
        attrs.addAttribute("Underline-Color", Color.red);
        this.invalidWordAttribute = attrs;

        this.doc = (StyledDocument)component.getDocument();
        this.component = component;
        this.correction_client = correction_client;
        this.init();
    }

    public void init() {
        initListener();
    }

    public void setDisabled(boolean disabled){
        this.disabled = disabled;
    }

    private void initListener() {
        this.doc.addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                update(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // do nothing when attributes are changed (infinite loop otherwise)
            }

            private void update(DocumentEvent e) {
                if (disabled) {
                    return;
                }
                SwingUtilities.invokeLater(() -> {
                    if (!correction_client.isValidWord(component)) {
                        correction_client.decorateInvalidWord(component, invalidWordAttribute);
                    } else {
                        correction_client.clearWordAttributes(component);
                    }
                });
            }
        });
    }
}
