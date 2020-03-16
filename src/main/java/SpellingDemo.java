package main.java;

import javax.swing.*;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import java.awt.*;
import java.io.*;

public class SpellingDemo extends JPanel {
    public SpellingDemo(ISpellChecker checker) {
        super(new GridBagLayout());

        JTextPane text_pane = new JTextPane();
        text_pane.setPreferredSize(new Dimension(400, 300));
        text_pane.setEditable(true);
        Font font = new Font("Verdana", Font.PLAIN, 14);
        text_pane.setFont(font);

        DefaultHighlighter highlighter = new DefaultHighlighter();
        DefaultHighlightPainter painter = new DefaultHighlightPainter(Color.YELLOW);
        text_pane.setHighlighter(highlighter);

        // Attach decorating listeners
        WordCorrectionClient correction_client = new WordCorrectionClient(checker);
        new CorrectionDropdownDecorator<>(text_pane, correction_client);
        new MisspellDecorator<>(text_pane, correction_client, new HighlightingErrorDecorator(highlighter, painter));

        // Add Components to this panel.
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        JScrollPane scrollPane = new JScrollPane(text_pane);
        add(scrollPane, c);
    }


    private static void init() {
        // Create and set up the window.
        JFrame frame = new JFrame("Spelling Correction Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Load Spellchecker.dictionary
        InputStream dict = SpellingDemo.class.getResourceAsStream("/main/resources/english_words.txt");
        InputStream sample = SpellingDemo.class.getResourceAsStream("/main/resources/big.txt");
        String letters = "abcdefghijklmnopqrstuvwxyz";
        String symbols = "'";
        char[] alphabet = (letters + symbols).toCharArray();

        SpellChecker checker = new SpellChecker(dict, sample, alphabet, 2, 5);

        // Add contents
        frame.add(new SpellingDemo(checker));

        // Display
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args){
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                init();
            }
        });
    }
}
