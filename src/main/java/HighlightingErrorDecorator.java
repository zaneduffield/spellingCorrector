package main.java;

import javax.swing.text.*;

/**
 * Decorates errors using a highlighter and painter for some document
 */
public class HighlightingErrorDecorator implements ErrorDecorator{
    private final DefaultHighlighter highlighter;
    private final DefaultHighlighter.DefaultHighlightPainter painter;

    public HighlightingErrorDecorator(DefaultHighlighter highlighter, DefaultHighlighter.DefaultHighlightPainter painter) {
        this.highlighter = highlighter;
        this.painter = painter;
    }

    public void decorate(int start, int end) {
        try {
            this.highlighter.addHighlight(start, end, this.painter);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void undecorateAll(){
        this.highlighter.removeAllHighlights();
    }
}
