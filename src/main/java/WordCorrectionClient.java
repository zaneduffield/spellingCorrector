package main.java;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * @see SuggestionClient
 * Bases everything on the location of the caret in the document.
 * Sets the popup location as the start of the word at the caret.
 * Sets selected text by replacing the word at the caret.
 * Determines valid word by validity of the word at the caret.
 * Gets suggestions based on the word at the caret.
 */
public class WordCorrectionClient implements SuggestionClient<JTextPane> {
    private Function<String, ArrayList<String>> correction_provider;
    private Function<String, Boolean> validity_checker;
    private ArrayList<String> previous_corrections;
    private String previous_word = "";

    public WordCorrectionClient(Corrector checker) {
        this.correction_provider = checker::getCorrections;
        this.validity_checker = checker::isValid;
    }

    @Override
    public Point getPopupLocation(JTextPane component) {
        int[] word_indices = this.getWordIndicesAtCaret(component);
        if (word_indices != null){
            try {
                Rectangle2D rectangle2D = component.modelToView2D(word_indices[0]);
                return new Point((int) rectangle2D.getX(), (int) (rectangle2D.getY() + rectangle2D.getHeight()));
            } catch (BadLocationException e) {
                System.err.println(e);
            }
        }
        return null;
    }

    @Override
    public void setSelectedText(JTextPane component, String suggestion) {
        try {
            int[] word_indices = this.getWordIndicesAtCaret(component);
            if (word_indices == null){
                return;
            }
            int start = word_indices[0];
            int end = word_indices[1];
            component.getDocument().remove(start, end - start);
            component.getDocument().insertString(start, suggestion, null);
        } catch (BadLocationException e) {
            System.err.println(e);
        }
    }

    private int[] getWordIndicesAtCaret(JTextPane tp){
        try {
            int cp = tp.getCaretPosition();
            int previousWordIndex;
            int wordEndIndex;
            if (cp != 0) {
                String text = tp.getText(cp - 1, 1);
                if (text.trim().isEmpty() || Pattern.matches("\\p{Punct}", text)) {
                    previousWordIndex = cp;
                } else {
                    previousWordIndex = Utilities.getPreviousWord(tp, cp);
                }
            } else {
                previousWordIndex = 0;
            }
            String text = tp.getText(cp, 1);
            if (text.trim().isEmpty() || Pattern.matches("\\p{Punct}", text)) {
                wordEndIndex = cp;
            } else {
                wordEndIndex = Utilities.getWordEnd(tp, cp);
            }
            return new int[]{previousWordIndex, wordEndIndex};
        } catch (BadLocationException e) {
            System.err.println(e);
        }
        return null;
    }

    private String getWordAtCaret(JTextPane tp){
        int[] word_indices = this.getWordIndicesAtCaret(tp);
        if (word_indices != null) {
            try {
                return tp.getText(word_indices[0], word_indices[1] - word_indices[0]);
            } catch (BadLocationException e) {
                System.err.println(e);
            }
        }
        return null;
    }

    @Override
    public boolean isValidWord(String word) {
        if (word != null && !word.isEmpty()){
            return this.validity_checker.apply(word);
        }
        return true;
    }

    @Override
    public ArrayList<String> getSuggestions(JTextPane component) {
        String text = this.getWordAtCaret(component);
        if (text != null && text.length() != 0) {
            if (text.equals(this.previous_word)){
                return this.previous_corrections;
            }
            this.previous_word = text;
            this.previous_corrections = this.correction_provider.apply(text.trim());
            return this.previous_corrections;
        }
        return null;
    }
}