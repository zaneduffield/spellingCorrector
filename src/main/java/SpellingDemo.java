package main.java;

import javax.swing.*;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import java.awt.*;
import java.io.*;
import java.util.Scanner;

public class SpellingDemo extends JPanel {
    public SpellingDemo(Corrector checker) {
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
        new SuggestionDropdownDecorator<>(text_pane, correction_client);
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
        InputStream dict = SpellingDemo.class.getResourceAsStream("/main/resources/words.txt");
        InputStream sample = SpellingDemo.class.getResourceAsStream("/main/resources/sample.txt");
        Scanner sc = new Scanner(SpellingDemo.class.getResourceAsStream("/main/resources/alphabet.txt"));
        char[] alphabet = (sc.nextLine()).toCharArray();

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
