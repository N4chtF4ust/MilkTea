package com.kiosk.main;


import javax.swing.*;
import java.awt.*;

public class DashBoard extends JPanel {
    public DashBoard(JFrame parentFrame) {
        parentFrame.getContentPane().setBackground(new Color(15, 23, 42));
        setPreferredSize(new Dimension(500, 450));
        setBackground(new Color(209, 213, 219));
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(new Color(55, 65, 81), 2));

        // Header Panel
        JPanel headerPanel = new JPanel(new GridLayout(1, 2));
        headerPanel.setPreferredSize(new Dimension(450, 100));
        headerPanel.setBackground(new Color(190, 212, 233));
        headerPanel.setBorder(BorderFactory.createLineBorder(new Color(18, 52, 88), 2, true));

        JPanel leftHeaderPanel = new JPanel(new GridBagLayout());
        leftHeaderPanel.setOpaque(false);
        JLabel pendingOrderLabel = new JLabel("Pending Order");
        pendingOrderLabel.setForeground(new Color(11, 56, 95));
        pendingOrderLabel.setFont(new Font("Rockwell", Font.BOLD, 20));
        leftHeaderPanel.add(pendingOrderLabel);

        JPanel rightHeaderPanel = new JPanel(new GridBagLayout());
        rightHeaderPanel.setOpaque(false);
        JLabel toReceiveLabel = new JLabel("To Receive");
        toReceiveLabel.setForeground(new Color(11, 56, 95));
        toReceiveLabel.setFont(new Font("Rockwell", Font.BOLD, 20));
        rightHeaderPanel.add(toReceiveLabel);

        headerPanel.add(leftHeaderPanel);
        headerPanel.add(rightHeaderPanel);
        add(headerPanel, BorderLayout.NORTH);

        // Wrapper panel for outer spacing
        JPanel mainWrapperPanel = new JPanel(new BorderLayout());
        mainWrapperPanel.setBackground(new Color(209, 213, 219));
        mainWrapperPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Main container with space in between panels
        JPanel mainContainerPanel = new JPanel(new GridLayout(1, 2, 40, 0));
        mainContainerPanel.setBackground(Color.WHITE);
        mainContainerPanel.setBorder(BorderFactory.createLineBorder(new Color(18, 52, 88), 3, true));

        JPanel leftContentPanel = new JPanel(new GridBagLayout());
        leftContentPanel.setOpaque(false);

        JPanel rightContentPanel = new JPanel(new GridBagLayout());
        rightContentPanel.setOpaque(false);

        mainContainerPanel.add(leftContentPanel);
        mainContainerPanel.add(rightContentPanel);

        mainWrapperPanel.add(mainContainerPanel, BorderLayout.CENTER);
        add(mainWrapperPanel, BorderLayout.CENTER);
    }
}
