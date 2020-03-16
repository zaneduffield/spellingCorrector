import javax.swing.*;
import javax.swing.plaf.basic.BasicTextPaneUI;
import javax.swing.text.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.function.Function;

public class SpellingDemo extends JPanel {
    public SpellingDemo(ISpellChecker checker) {
        super(new GridBagLayout());

        JTextPane text_pane = new JTextPane();
        text_pane.setPreferredSize(new Dimension(400, 300));
        text_pane.setEditable(true);
        Font font = new Font("Verdana", Font.PLAIN, 14);
        text_pane.setFont(font);

        // Custom rendering for underlines
        text_pane.setEditorKit(new CustomEditorKit());

        // Attach decorating listeners
        WordCorrectionClient correction_client = new WordCorrectionClient(checker);
        new CorrectionDropdownDecorator<>(text_pane, correction_client);
        new UnderlineDecorator<>(text_pane, correction_client);

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

    public static class CustomEditorKit extends StyledEditorKit {

        public ViewFactory getViewFactory(){
            return new CustomUI();
        }
    }

    public static class CustomUI extends BasicTextPaneUI {
        @Override
        public View create(Element elem){
            View result = null;
            String kind = elem.getName();
            if(kind != null){
                if(kind.equals(AbstractDocument.ContentElementName)){
                    result = new MyLabelView(elem);
                } else if(kind.equals(AbstractDocument.ParagraphElementName)){
                    result = new ParagraphView(elem);
                }else if(kind.equals(AbstractDocument.SectionElementName)){
                    result = new BoxView(elem, View.Y_AXIS);
                }else if(kind.equals(StyleConstants.ComponentElementName)){
                    result = new ComponentView(elem);
                }else if(kind.equals(StyleConstants.IconElementName)){
                    result = new IconView(elem);
                } else{
                    result = new LabelView(elem);
                }
            }else{
                result = super.create(elem);
            }

            return result;
        }
    }

    public static class MyLabelView extends LabelView{

        public MyLabelView(Element arg0) {
            super(arg0);
        }

        public void paint(Graphics g, Shape a){
            super.paint(g, a);
            // Extra painting here
            Color c = (Color)getElement().getAttributes().getAttribute("Underline-Color");
            if(c != null){
                int y = a.getBounds().y + (int)getGlyphPainter().getAscent(this);
                int x1 = a.getBounds().x;
                int x2 = a.getBounds().width + x1;

                g.setColor(c);
                g.drawLine(x1, y, x2, y);
            }

        }
    }

    private static void init() {
        // Create and set up the window.
        JFrame frame = new JFrame("Spelling Correction Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Load dictionary
        File dict = new File("dictionary/english_words.txt");
        File sample = new File("dictionary/big.txt");
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
