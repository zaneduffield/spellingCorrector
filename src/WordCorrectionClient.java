import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.function.Function;

public class WordCorrectionClient implements CorrectionClient<JTextPane> {
    private Function<String, ArrayList<String>> correction_provider;
    private Function<String, Boolean> validity_checker;

    public WordCorrectionClient(Function<String, ArrayList<String>> correction_provider, Function<String, Boolean> validity_checker) {
        this.correction_provider = correction_provider;
        this.validity_checker = validity_checker;
    }

    @Override
    public Point getPopupLocation(JTextPane tp) {
        int[] word_indices = this.getWordIndicesAtCaret(tp);
        if (word_indices != null){
            try {
                Rectangle2D rectangle2D = tp.modelToView2D(word_indices[0]);
                return new Point((int) rectangle2D.getX(), (int) (rectangle2D.getY() + rectangle2D.getHeight()));
            } catch (BadLocationException e) {
                System.err.println(e);
            }
        }
        return null;
    }

    @Override
    public void setSelectedText(JTextPane tp, String selectedValue) {
        int cp = tp.getCaretPosition();
        try {
            if (cp == 0 || tp.getText(cp - 1, 1).trim().isEmpty()) {
                tp.getDocument().insertString(cp, selectedValue, null);
            } else {
                int[] word_indices = this.getWordIndicesAtCaret(tp);
                if (word_indices == null){
                    return;
                }
                int start = word_indices[0];
                int end = word_indices[1];
                tp.getDocument().remove(start, end - start);
                tp.getDocument().insertString(start, selectedValue, null);
            }
        } catch (BadLocationException e) {
            System.err.println(e);
        }
    }

    @Override
    public void decorateInvalidWord(JTextPane tp, AttributeSet attrs){
        StyledDocument doc = (StyledDocument)tp.getDocument();
        int[] word_indices = this.getWordIndicesAtCaret(tp);
        if (word_indices != null){
            doc.setCharacterAttributes(word_indices[0], word_indices[1] - word_indices[0], attrs, false);
        }
    }

    @Override
    public void undecorateValidWord(JTextPane tp){
        StyledDocument doc = (StyledDocument)tp.getDocument();
        int[] word_indices = this.getWordIndicesAtCaret(tp);
        if (word_indices != null){
            doc.setCharacterAttributes(word_indices[0], word_indices[1] - word_indices[0], new SimpleAttributeSet(), true);
        }
    }

    private int[] getWordIndicesAtCaret(JTextPane tp){
        try {
            int cp = tp.getCaretPosition();
            if (cp != 0) {
                String text = tp.getText(cp - 1, 1);
                if (text.trim().isEmpty()) {
                    return null;
                }
            }
            int previousWordIndex = Utilities.getPreviousWord(tp, cp);
            int wordEndIndex = Utilities.getWordEnd(tp, cp);
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

    public boolean isValidWord(JTextPane tp) {
        String word = this.getWordAtCaret(tp);
        if (word != null){
            return this.validity_checker.apply(word);
        }
        return true;
    }

    @Override
    public ArrayList<String> getSuggestions(JTextPane tp) {
        String text = this.getWordAtCaret(tp);
        if (text != null) {
            return this.correction_provider.apply(text.trim());
        }
        return null;
    }
}