package main.java;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;

public class MisspellDecorator<C extends JTextComponent>{
    private final StyledDocument doc;
    private final MutableAttributeSet invalidWordAttribute;
    private final CorrectionClient<C> correction_client;
    private final ErrorDecorator decorator;

    private boolean disabled = false;

    public MisspellDecorator(C component, CorrectionClient<C> correction_client, ErrorDecorator decorator) {
        MutableAttributeSet attrs = new SimpleAttributeSet();
        attrs.addAttribute("Underline-Color", Color.red);
        this.invalidWordAttribute = attrs;

        this.doc = (StyledDocument)component.getDocument();
        this.decorator = decorator;
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
                    // loop over all words
                    try {
                        decorator.undecorateAll();
                        String content = doc.getText(0, doc.getLength());
                        int index = 0;
                        for (String word: content.split(" |\\p{Punct}")){
                            if (!correction_client.isValidWord(word)) {
                                decorator.decorate(index, index + word.length());
                            }
                            index += 1 + word.length();
                        }
                    } catch (BadLocationException ex) {
                        ex.printStackTrace();
                    }
                });
            }
        });
    }
}
