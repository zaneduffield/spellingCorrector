package main.java;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;

/**
 * Listens for changes in the provided component's document and provides decorates words deemed invalid by the
 * correction client.
 * @param <C> the text component to be decorated
 */
public class MisspellDecorator<C extends JTextComponent>{
    private final StyledDocument doc;
    private final SuggestionClient<C> correction_client;
    private final ErrorDecorator decorator;
    private boolean disabled = false;

    public MisspellDecorator(C component, SuggestionClient<C> correction_client, ErrorDecorator decorator) {
        this.doc = (StyledDocument)component.getDocument();
        this.decorator = decorator;
        this.correction_client = correction_client;
        this.init();
    }

    private void init() {
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
                    // inefficient to loop over all words every time but attempting to change only the decoration of
                    // a changed section proved quite difficult
                    try {
                        decorator.undecorateAll();
                        String content = doc.getText(0, doc.getLength());
                        int index = 0;
                        for (String word: content.split(" |(\\p{Punct} && [^'])|\n")){
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
