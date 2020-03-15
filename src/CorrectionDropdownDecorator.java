import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import static java.awt.event.KeyEvent.*;

public class CorrectionDropdownDecorator {
    private final JTextPane component;
    private final CorrectionClient<JTextPane> correction_client;
    private JPopupMenu popup_menu;
    private JList<String> list_comp;
    DefaultListModel<String> list_model;
    private boolean disable_text_event;

    public CorrectionDropdownDecorator(JTextPane component, CorrectionClient<JTextPane> correction_client) {
        this.component = component;
        this.correction_client = correction_client;
    }

    public static void decorate(JTextPane component, CorrectionClient<JTextPane> correction_client) {
        CorrectionDropdownDecorator d = new CorrectionDropdownDecorator(component, correction_client);
        d.init();
    }

    public void init() {
        initPopup();
        initSuggestionCompListener();
        initInvokerKeyListeners();
    }

    private void initPopup() {
        popup_menu = new JPopupMenu();
        list_model = new DefaultListModel<>();
        list_comp = new JList<>(list_model);
        list_comp.setBorder(BorderFactory.createEmptyBorder(0, 2, 5, 2));
        list_comp.setFocusable(false);
        popup_menu.setFocusable(false);
        popup_menu.add(list_comp);
    }

    private void initSuggestionCompListener() {
        component.addCaretListener(e -> {
            if (disable_text_event) {
                return;
            }
            SwingUtilities.invokeLater(() -> {
                ArrayList<String> suggestions = correction_client.getSuggestions(component);
                if (suggestions != null && !suggestions.isEmpty()) {
                    showPopup(suggestions);
                } else {
                    popup_menu.setVisible(false);
                }
            });
        });
    }

    private void showPopup(ArrayList<String> suggestions) {
        list_model.clear();
        suggestions.forEach(list_model::addElement);
        Point p = correction_client.getPopupLocation(component);
        if (p == null) {
            return;
        }
        popup_menu.pack();
        list_comp.setSelectedIndex(0);
        popup_menu.show(component, (int) p.getX(), (int) p.getY());
    }

    private void initInvokerKeyListeners() {
        //not using key inputMap cause that would override the original handling
        component.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == VK_ENTER) {
                    selectFromList(e);
                } else if (e.getKeyCode() == VK_UP) {
                    moveUp(e);
                } else if (e.getKeyCode() == VK_DOWN) {
                    moveDown(e);
                } else if (e.getKeyCode() == VK_ESCAPE) {
                    popup_menu.setVisible(false);
                }
            }
        });
    }

    private void selectFromList(KeyEvent e) {
        if (popup_menu.isVisible()) {
            int selectedIndex = list_comp.getSelectedIndex();
            if (selectedIndex != -1) {
                popup_menu.setVisible(false);
                String selectedValue = list_comp.getSelectedValue();
                disable_text_event = true;
                correction_client.setSelectedText(component, selectedValue);
                disable_text_event = false;
                e.consume();
            }
        }
    }

    private void moveDown(KeyEvent keyEvent) {
        if (popup_menu.isVisible() && list_model.getSize() > 0) {
            int selectedIndex = list_comp.getSelectedIndex();
            if (selectedIndex < list_model.getSize()) {
                list_comp.setSelectedIndex(selectedIndex + 1);
                keyEvent.consume();
            }
        }
    }

    private void moveUp(KeyEvent keyEvent) {
        if (popup_menu.isVisible() && list_model.getSize() > 0) {
            int selectedIndex = list_comp.getSelectedIndex();
            if (selectedIndex > 0) {
                list_comp.setSelectedIndex(selectedIndex - 1);
                keyEvent.consume();
            }
        }
    }
}
