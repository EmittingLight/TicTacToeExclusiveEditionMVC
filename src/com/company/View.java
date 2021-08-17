package com.company;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class View {
    private Controller controller;
    private JFrame frame;
    private JLabel label;

    public void create(int width, int height) {
        frame = new JFrame();
        frame.setSize(width, height);
        frame.setUndecorated(true);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                controller.handleMousePress(e.getX(), e.getY());
            }
        });

        label = new JLabel();
        label.setBounds(0, 0, width, height);
        frame.add(label);

        frame.setVisible(true);
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setImage(BufferedImage image) {
        label.setIcon(new ImageIcon(image));
    }
}
