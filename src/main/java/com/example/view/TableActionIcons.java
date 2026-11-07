package com.example.view;

import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

final class TableActionIcons {

    private TableActionIcons() {
    }

    static Icon edit() {
        return new ActionIcon(false);
    }

    static Icon delete() {
        return new ActionIcon(true);
    }

    static Icon search() {
        return new SearchIcon();
    }

    private static final class SearchIcon implements Icon {
        @Override
        public int getIconWidth() {
            return 18;
        }

        @Override
        public int getIconHeight() {
            return 18;
        }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.translate(x, y);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(94, 107, 124));
            g2.drawOval(2, 2, 10, 10);
            g2.drawLine(10, 10, 16, 16);
            g2.dispose();
        }
    }

    private static final class ActionIcon implements Icon {
        private final boolean delete;

        private ActionIcon(boolean delete) {
            this.delete = delete;
        }

        @Override
        public int getIconWidth() {
            return 16;
        }

        @Override
        public int getIconHeight() {
            return 16;
        }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.translate(x, y);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(delete ? new Color(195, 66, 66) : new Color(48, 112, 190));

            if (delete) {
                g2.drawLine(3, 4, 13, 4);
                g2.drawLine(6, 2, 10, 2);
                g2.drawRoundRect(4, 5, 8, 9, 1, 1);
                g2.drawLine(7, 7, 7, 12);
                g2.drawLine(9, 7, 9, 12);
            } else {
                g2.drawLine(4, 12, 3, 14);
                g2.drawLine(3, 14, 5, 13);
                g2.drawLine(5, 13, 13, 5);
                g2.drawLine(10, 4, 12, 2);
                g2.drawLine(12, 2, 14, 4);
                g2.drawLine(14, 4, 13, 5);
            }
            g2.dispose();
        }
    }
}
