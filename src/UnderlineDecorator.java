import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import java.awt.*;

public class UnderlineDecorator {
    private final JTextPane pane;
    private final StyledDocument doc;
    private final MutableAttributeSet invalidWordAttribute;
    private final CorrectionClient<JTextPane> correction_client;
    DefaultListModel<String> list_model;
    private boolean disable_text_event;

    public UnderlineDecorator(JTextPane pane, CorrectionClient<JTextPane> correction_client) {
        MutableAttributeSet attrs = new SimpleAttributeSet();
        attrs.addAttribute("Underline-Color", Color.red);
        this.invalidWordAttribute = attrs;

        this.doc = (StyledDocument)pane.getDocument();
        this.pane = pane;
        this.correction_client = correction_client;
    }

    public static void decorate(JTextPane component, CorrectionClient<JTextPane> correction_client) {
        UnderlineDecorator d = new UnderlineDecorator(component, correction_client);
        d.init();
    }

    public void init() {
        initListener();
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
//                update(e);
            }

            private void update(DocumentEvent e) {
                if (disable_text_event) {
                    return;
                }
                SwingUtilities.invokeLater(() -> {
                    if (!correction_client.isValidWord(pane)) {
                        // underline red
                        correction_client.decorateInvalidWord(pane, invalidWordAttribute);
                    } else {
                        correction_client.undecorateValidWord(pane);
                    }
                });
            }
        });
    }
}
