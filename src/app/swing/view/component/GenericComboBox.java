package app.swing.view.component;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Function;

public class GenericComboBox<T> extends JComboBox<T> {
    public GenericComboBox(List<T> items, Function<T, String> displayFunc) {
        super(new DefaultComboBoxModel<>(items.toArray((T[]) new Object[0])));
        setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    label.setText(displayFunc.apply((T) value));
                }
                return label;
            }
        });
    }

    public T getSelectedItemModel() {
        return (T) super.getSelectedItem();
    }
}
