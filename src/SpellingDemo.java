import javax.swing.*;
import javax.swing.plaf.basic.BasicTextPaneUI;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.function.Function;

public class SpellingDemo extends JPanel {
    protected JTextArea textArea;
    private final static String newline = "\n";

    public SpellingDemo(Function<String, ArrayList<String>> correction_provider, Function<String, Boolean> validity_checker) {
        super(new GridBagLayout());

//        textField.addActionListener(this);

        JTextPane text_pane = new JTextPane();
        Font font = new Font("Verdana", Font.PLAIN, 14);
        text_pane.setFont(font);
        text_pane.setEditorKit(new CustomEditorKit());
        text_pane.setEditable(true);
        text_pane.setPreferredSize(new Dimension(400, 300));
        JScrollPane scrollPane = new JScrollPane(text_pane);
        WordCorrectionClient correction_client = new WordCorrectionClient(correction_provider, validity_checker);
        CorrectionDropdownDecorator.decorate(text_pane, correction_client);
        UnderlineDecorator.decorate(text_pane, correction_client);

        //Add Components to this panel.
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
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
            //Do whatever other painting here;
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
        //Create and set up the window.
        JFrame frame = new JFrame("Spelling Correction Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        File dict = new File("dictionary/en_AU.dic");
        File sample = new File("dictionary/big.txt");
        char[] alphabet = "abcdefghijklmnopqrstuvwxyz'".toCharArray();
        Spellchecker checker = new Spellchecker(dict, sample, alphabet, 2, 5);

        //Add contents to the window.
        frame.add(new SpellingDemo(checker::getCorrections, checker::isValid));

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args){
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                init();
            }
        });
    }
}
