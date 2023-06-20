package com.natia.downloadreplace;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class FrameMaker {
    List<JLabel> jlabels = new ArrayList<>();

    List<JButton> jButtons = new ArrayList<>();

    List<JProgressBar> jProgressBars = new ArrayList<>();

    List<JTextField> jTextFields = new ArrayList<>();

    List<JTextArea> jTextAreas = new ArrayList<>();

    List<JPanel> jRects = new ArrayList<>();

    JFrame frame;

    JPanel panel;

    Dimension windowDimensions;

    String windowtitle;

    int closeOperation;

    boolean resizeable;

    public FrameMaker(String windowtitle, Dimension windowDimensions, int closeOperation, boolean resizeable) {
        this.windowDimensions = windowDimensions;
        this.windowtitle = windowtitle;
        this.closeOperation = closeOperation;
        this.resizeable = resizeable;
        this.frame = new JFrame(windowtitle);
        this.panel = new JPanel();
    }

    public JPanel getJPanel() {
        return this.panel;
    }

    public JFrame getJFrame() {
        return this.frame;
    }

    public JLabel addText(String text, int x, int y, int fontSize, boolean bold) {
        System.out.println("     [+] Creating text \"" + text + "\"");
        JLabel label = new JLabel(text);
        if (bold) {
            label.setFont(new Font("Arial", 1, fontSize));
        } else {
            label.setFont(new Font("Arial", 0, fontSize));
        }
        Dimension size = label.getPreferredSize();
        label.setBounds(x, y, size.width + 30, size.height);
        this.jlabels.add(label);
        return label;
    }

    public JButton addButton(String text, int x, int y, int scale, ActionListener listener) {
        System.out.println("     [+] Creating button \"" + text + "\"");
        JButton button = new JButton(text);
        Dimension size = button.getPreferredSize();
        button.setBounds(x, y, scale, size.height);
        button.setFocusPainted(false);
        button.addActionListener(listener);
        this.jButtons.add(button);
        return button;
    }

    public JButton addButton(String text, int x, int y, int scaleX, int scaleY, ActionListener listener) {
        System.out.println("     [+] Creating button \"" + text + "\"");
        JButton button = new JButton(text);
        Dimension size = button.getPreferredSize();
        button.setBounds(x, y, scaleX, scaleY);
        button.setFocusPainted(false);
        if (listener != null)
            button.addActionListener(listener);
        this.jButtons.add(button);
        return button;
    }

    public JProgressBar addProgressBar(int lengthOfTask, int x, int y, int scaleX, int scaleY) {
        JProgressBar progressBar = new JProgressBar(0, lengthOfTask);
        progressBar.setValue(0);
        progressBar.setBounds(x, y, scaleX, scaleY);
        progressBar.setStringPainted(true);
        this.jProgressBars.add(progressBar);
        return progressBar;
    }

    public JLabel addImage(ImageIcon image, int x, int y, int width, int height) {
        JLabel imageLabel = new JLabel(image);
        imageLabel.setBounds(x, y, width, height);
        this.jlabels.add(imageLabel);
        return imageLabel;
    }

    public JTextField addTextField(int x, int y, int scale) {
        JTextField field = new JTextField();
        field.setBounds(x, y, scale, (field.getPreferredSize()).height);
        this.jTextFields.add(field);
        return field;
    }

    public JTextField addTextField(int x, int y, int width, int height) {
        JTextField field = new JTextField();
        field.setBounds(x, y, width, height);
        this.jTextFields.add(field);
        return field;
    }

    public JTextArea addTextArea(int x, int y, int width, int height) {
        JTextArea field = new JTextArea();
        field.setFont(new Font("Arial", 0, 12));
        field.setBounds(x, y, width, height);
        this.jTextAreas.add(field);
        return field;
    }

    public void clear() {
        this.jlabels.clear();
        this.jButtons.clear();
        this.jTextAreas.clear();
        this.jProgressBars.clear();
        this.jTextFields.clear();
        this.jRects.clear();
    }

    public JFrame override() {
        changeLook();
        this.panel = new JPanel();
        this.frame.getContentPane().removeAll();
        this.frame.repaint();
        this.panel.setLayout((LayoutManager)null);
        this.jlabels.forEach(this.panel::add);
        this.jButtons.forEach(this.panel::add);
        this.jProgressBars.forEach(this.panel::add);
        this.jTextFields.forEach(this.panel::add);
        this.jTextAreas.forEach(this.panel::add);
        this.jRects.forEach(this.panel::add);
        this.panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.frame.setDefaultCloseOperation(this.closeOperation);
        this.frame.add(this.panel);
        this.frame.setPreferredSize(this.windowDimensions);
        this.frame.pack();
        this.frame.setResizable(this.resizeable);
        this.frame.setVisible(true);
        return this.frame;
    }

    public JFrame pack() {
        changeLook();
        this.frame.getContentPane();
        this.panel.setLayout((LayoutManager)null);
        this.jlabels.forEach(this.panel::add);
        this.jButtons.forEach(this.panel::add);
        this.jTextAreas.forEach(this.panel::add);
        this.jProgressBars.forEach(this.panel::add);
        this.jTextFields.forEach(this.panel::add);
        this.jRects.forEach(this.panel::add);
        this.panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.frame.setDefaultCloseOperation(this.closeOperation);
        this.frame.add(this.panel);
        this.frame.setPreferredSize(this.windowDimensions);
        this.frame.setVisible(true);
        this.frame.pack();
        this.frame.setLocationRelativeTo((Component)null);
        this.frame.setResizable(this.resizeable);
        return this.frame;
    }

    private void changeLook() {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException|ClassNotFoundException|InstantiationException|IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
