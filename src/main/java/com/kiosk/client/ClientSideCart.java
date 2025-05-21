package com.kiosk.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;

import com.kiosk.cache_image.GetCachedImagePath;
import com.kiosk.dbConnection.dbCon;
import com.kiosk.icons.Icons;
import com.kiosk.loading.LoadingRotate;
import com.kiosk.main.Welcome;
import com.kiosk.model.AddOns;
import com.kiosk.model.ClientOrders;
import com.kiosk.model.Product;

public class ClientSideCart extends JPanel {
    private DefaultListModel<String> cartModel;
    private JList<String> cartList;
    private JLabel totalLabel;
    private JButton buyButton;
    private int totalPrice = 0;
    public static List<AddOns> addOns = new ArrayList<>();
    public static List<Product> products = new ArrayList<>();
    public static List<Product> oldProducts = new ArrayList<>();
    public static List<ClientOrders> clientOrders = new ArrayList<>();
    private double currentTotal = 0.0;   // Instance variable to store total


    //CartPanel
    public static JPanel cartPanel = new JPanel();
    //productPanel
    public static JPanel productPanel = new JPanel();
    public static JPanel productPanelCenter = new JPanel();
    public static JPanel cartPanelCenter = new JPanel();
    public static JScrollPane productPanelCenterScrollPane;
    


    boolean haha = true;
    private boolean pollingStarted = false;
    private Timer pollingTimer; // Added instance variable for the timer
    private HierarchyListener hierarchyListener; // Added for clean removal
    private ScheduledExecutorService scheduler;




    public ClientSideCart( ) {
    	
    	

        setSize(900, 600);

        setLayout(new BorderLayout());

        // Cart Panel
        cartPanel.setLayout(new BorderLayout());
        cartPanel.setBackground(new Color(200, 190, 180));

        JLabel cartLabel = new JLabel("Cart");
        cartLabel.setFont(  new Font("Arial", Font.BOLD, 24));
        cartLabel.setForeground(Color.decode("#123458"));
        cartLabel.setBorder(new EmptyBorder(9,9,9,9));

        // Create JPanel and add label and canvas
        JPanel cartPanelNorth = new JPanel();
        cartPanelNorth.setBackground(Color.WHITE);


        cartPanelNorth.setBorder(new LineBorder(Color.decode("#123458"), 1));






        JLabel cartIcon = new JLabel(Icons.createCartIcon());

        cartIcon.setOpaque(false); // Transparent background (optional)

        cartPanelNorth.add(cartIcon);
        cartPanelNorth.add(cartLabel);


        // Add the panel to your main panel or frame
        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.add(cartPanelNorth, BorderLayout.NORTH);

        cartPanelCenter.setLayout(new BoxLayout(cartPanelCenter, BoxLayout.Y_AXIS));

        // Wrap cartPanelCenter inside JScrollPane
        JScrollPane scrollPane = new JScrollPane(cartPanelCenter);
     // Set background color (e.g., light gray)
        Color bgColor = Color.decode("#D9D9D9");
        cartPanelCenter.setBackground(bgColor); // Set background for the panel inside the scroll pane



        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(400,600));


        // Add JScrollPane instead of cartPanelCenter
        cartPanel.add(scrollPane, BorderLayout.CENTER);

        for (ClientOrders clientOrder :clientOrders) {

        	  cartPanelCenter.add(cartItemsPanel( clientOrder));

        }



        totalLabel = new JLabel("Total: PHP " + String.format("%.2f", currentTotal));


        JPanel bottomPanel = new JPanel();
        JButton emptyButton = new JButton("Empty");
        buyButton = new JButton("Buy");

        emptyButton.setBackground(Color.RED);
        emptyButton.setForeground(Color.WHITE);
        emptyButton.addActionListener(e -> clearCart());

        buyButton.setBackground(Color.decode("#123458"));
        buyButton.setForeground(Color.WHITE);

        // Add appropriate listener
        if (currentTotal == 0.0) {
            buyButton.addActionListener(e ->
                JOptionPane.showMessageDialog(null, "Please add some product to cart.")
            );
        }




        bottomPanel.add(totalLabel);
        bottomPanel.add(emptyButton);
        bottomPanel.add(buyButton);

        cartPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Product Panel
        productPanel.setLayout(new BorderLayout());

        productPanel.setBackground(Color.WHITE);

        JPanel productPanelNorth = new JPanel();
        productPanelNorth.setBackground(Color.decode("#123458"));
        productPanelNorth.setBorder(new EmptyBorder(10,10,10,10));
        productPanel.add(productPanelNorth,BorderLayout.NORTH);

        JLabel productPanelNorthText = new JLabel("Milkteassai");
        productPanelNorthText.setFont(new Font("Arial", Font.BOLD, 24)); // Set font size and style
        productPanelNorthText.setForeground(Color.WHITE); // Set text color
        productPanelNorth.add(productPanelNorthText);



        productPanelCenter.setBackground(Color.white);
        productPanelCenter.setBorder(new EmptyBorder(20, 20, 20, 20));


        productPanel.add(productPanelCenter,BorderLayout.CENTER);


      for (Product product:products) {
    	  if(product.availability()) {
    		  productPanelCenter.add(createProductCard(product));
    	  }


      }

         productPanelCenterScrollPane = new JScrollPane(productPanelCenter);
        productPanelCenterScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Assuming the parent container is a JPanel or JFrame, add the scrollPane there
        productPanel.add(productPanelCenterScrollPane);
        add(cartPanel, BorderLayout.WEST);
        add(productPanel, BorderLayout.CENTER);



        //  media queries
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width = getWidth();
                int columns;

                if (width > 1200) {
                    columns = 3;  // Large screen
                } else if (width > 1000) {
                    columns = 2;  // Medium screen
                } else {
                    columns = 1;  // Small and very small screens
                }

                productPanelCenter.setLayout(new GridLayout(0, columns, 20, 20));
                productPanelCenter.revalidate();
                productPanelCenter.repaint();
            }
        });

        startPolling();
        revalidate();
        repaint();
        setVisible(true);


    }

    //Thjs is the product card with event handling

    private JPanel createProductCard(Product product) {
    	final double[] selectedPrice = {product.small()};
        final double[] priceIncrease = { selectedPrice[0] };
    	JLabel priceLabel = new JLabel("PHP "+String.format("%.2f",product.small()));
    	JLabel bottomPanelCenterAddOns = new JLabel("Add-Ons: No add ons");
    	bottomPanelCenterAddOns.setFont(new Font("Arial", Font.BOLD, 10));

    	JPanel panel = new JPanel() {
    	    @Override
    	    protected void paintComponent(Graphics g) {
    	        super.paintComponent(g);
    	        Graphics2D g2 = (Graphics2D) g;

    	        // Enable smooth rendering
    	        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    	        // Draw background (rounded rectangle)
    	        g2.setColor(getBackground());
    	        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);


    	    }
    	};




          panel.setOpaque(false); // Ensures the transparency for rounded corners
          panel.setLayout(new BorderLayout());
          panel.setBackground(Color.decode("#D9D9D9")); // Make sure it has a visible color
          panel.setPreferredSize(new Dimension(250, 350));
          panel.setMinimumSize(new Dimension(250, 350));
          panel.setMaximumSize(new Dimension(250, 350));




        //Create Border


        // Wrapper panel using GridLayout to enforce 50% width
        JPanel wrapperPanel = new JPanel(new GridLayout(1, 2,20,0));
        wrapperPanel.setBorder(new EmptyBorder(10,10,10,10));
        wrapperPanel.setOpaque(false);


        panel.add(wrapperPanel, BorderLayout.NORTH);





        JComboBox<String> sizeBox = new JComboBox<>(new String[]{"Small", "Medium", "Large"}) {

        };
        sizeBox.setBorder(new EmptyBorder(5, 5, 5, 5));
        sizeBox.setBackground(Color.decode("#123458"));
        sizeBox.setSelectedItem(Color.decode("#123458"));

        sizeBox.setForeground(Color.WHITE);

        // Customizing the JComboBox to change the arrow color
        sizeBox.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton arrowButton = super.createArrowButton();
                arrowButton.setBackground(Color.decode("#123458")); // Arrow button background color
                arrowButton.setForeground(Color.white);   // Change the arrow color to red (or any color)
                return arrowButton;
            }
        });
        wrapperPanel.add(sizeBox);

        JButton dropdownButton = new JButton("Add-Ons");
        dropdownButton.setBackground(Color.decode("#123458"));
        dropdownButton.setForeground(Color.white);
        JPopupMenu popupMenu = new JPopupMenu();

        // Checkbox items
        int checkBoxMenuItemsIndex = 0;
        JCheckBoxMenuItem[] checkBoxMenuItems = new JCheckBoxMenuItem[addOns.size()];

        for (AddOns addon : addOns) {
        checkBoxMenuItems[checkBoxMenuItemsIndex] = new JCheckBoxMenuItem(addon.AddOnName() + " - PHP " + String.format("%.2f", addon.AddOnPrice()));
        checkBoxMenuItems[checkBoxMenuItemsIndex].setOpaque(false);
        checkBoxMenuItems[checkBoxMenuItemsIndex].setForeground(Color.WHITE);
        popupMenu.add(checkBoxMenuItems[checkBoxMenuItemsIndex]);
        checkBoxMenuItemsIndex++;
        }
        popupMenu.setBackground(Color.decode("#123458"));

        // "No Add Ons" checkbox (when selected, deselects others)
        JButton noAddOns = new JButton("No Add Ons");
        noAddOns.setBackground(Color.WHITE);
        noAddOns.setForeground(Color.decode("#123458"));

        JPanel noAddOnsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        noAddOnsPanel.setOpaque(false);
        noAddOnsPanel.add(noAddOns);
        // Add checkboxes to popup menu
        popupMenu.addSeparator();
        popupMenu.add(noAddOnsPanel);

        wrapperPanel.add(dropdownButton);

        // Load the image
     // Using getClass().getResource() to correctly reference the image from the resources folder


        // Image loading
        try {
            String signedUrl = GetCachedImagePath.getCachedImagePath(product.img());
            if (signedUrl != null) {
                URL imageURL = new URL(signedUrl);
                ImageIcon imageIcon = new ImageIcon(imageURL);
                Image image = imageIcon.getImage();

                int panelWidth = 150;
                int panelHeight = 150;
                int imageWidth = image.getWidth(null);
                int imageHeight = image.getHeight(null);

                double aspectRatio = (double) imageWidth / imageHeight;
                int newWidth = panelWidth;
                int newHeight = (int) (panelWidth / aspectRatio);

                if (newHeight > panelHeight) {
                    newHeight = panelHeight;
                    newWidth = (int) (panelHeight * aspectRatio);
                }

                Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
                panel.add(imageLabel, BorderLayout.CENTER);
            }
        } catch (MalformedURLException e) {
            System.err.println("Invalid URL for image: " + e.getMessage());
        }

        // Create a JLabel to hold the scaled image



        JLabel nameLabel = new JLabel(product.productName());

        nameLabel.setForeground(Color.decode("#123458"));
        nameLabel.setFont(new Font("Arial", Font.BOLD, 15));


        priceLabel.setForeground(Color.decode("#123458"));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 15));

        // Create a sub-panel to stack the label above the button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(10,10,10,10));

        JPanel bottomPanelTop = new JPanel(new BorderLayout());
        bottomPanelTop.setOpaque(false);

        bottomPanelTop.add(nameLabel, BorderLayout.WEST);
        bottomPanelTop.add(priceLabel, BorderLayout.EAST);


        bottomPanelCenterAddOns.setForeground(Color.decode("#123458"));
        bottomPanelCenterAddOns.setBorder(new EmptyBorder(5,0,5,5));


        JButton addButton = new JButton("ADD");
        addButton.setBorder(new EmptyBorder(10,10,10,10));


        addButton.setBackground(Color.decode("#123458"));
        addButton.setForeground(Color.WHITE);
        bottomPanel.add(addButton, BorderLayout.SOUTH);
        bottomPanel.add(bottomPanelTop, BorderLayout.NORTH);
        bottomPanel.add(bottomPanelCenterAddOns, BorderLayout.CENTER);

        panel.add(bottomPanel, BorderLayout.SOUTH); // Add the bottom panel to the main panel


        //Event Handling are in this area
        dropdownButton.addActionListener(e -> popupMenu.show(dropdownButton, 0, dropdownButton.getHeight()));

        StringBuilder checkBoxMenuItemsSelectedNames = new StringBuilder(); // Store selected names
        int checkBoxMenuItemsEventsIndex = 0;
        for (AddOns addon : addOns) {

            // Use a single-element array to allow modification inside lambda

            checkBoxMenuItems[checkBoxMenuItemsEventsIndex].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JCheckBoxMenuItem source = (JCheckBoxMenuItem) e.getSource();
                    if (source.isSelected()) {
                    	priceIncrease[0]+= addon.AddOnPrice();
                        if (!checkBoxMenuItemsSelectedNames.toString().contains(addon.AddOnName())) {
                        	checkBoxMenuItemsSelectedNames.append(addon.AddOnName()).append(", ");
                        }

                    } else if (!source.isSelected()) {


                        if(priceIncrease[0]>selectedPrice[0]) {
                        	priceIncrease[0]-= addon.AddOnPrice();
                        }

                        // Remove the deselected item from selectedNames
                        String nameToRemove = addon.AddOnName() + ", ";
                        int startIndex = checkBoxMenuItemsSelectedNames.indexOf(nameToRemove);
                        if (startIndex != -1) {
                        	checkBoxMenuItemsSelectedNames.delete(startIndex, startIndex + nameToRemove.length());
                        }
                    }

                    priceLabel.setText("PHP "+String.format("%.2f",priceIncrease[0]));
                    bottomPanelCenterAddOns.setText("Add-Ons: " + (checkBoxMenuItemsSelectedNames.length() > 0
                            ? checkBoxMenuItemsSelectedNames.substring(0, checkBoxMenuItemsSelectedNames.length() - 2)
                            : "No add ons"));
                }
            });

            checkBoxMenuItemsEventsIndex++;
        }

       // Use an array to store the selected price (workaround for effectively final requirement)

        sizeBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	for (JCheckBoxMenuItem item : checkBoxMenuItems) {
            	    item.setSelected(false);
            	}
                String selectedOption = (String) sizeBox.getSelectedItem();


                if (selectedOption != null) {
                    switch (selectedOption) {
                        case "Small":
                            selectedPrice[0] = product.small();

                            break;
                        case "Medium":
                            selectedPrice[0] = product.medium();
                            break;
                        case "Large":
                            selectedPrice[0] = product.large();
                            break;
                        default:
                            selectedPrice[0] = 0.0; // Fallback value
                    }

                    priceIncrease[0] = selectedPrice[0];



                    priceLabel.setText("PHP "+String.format("%.2f",selectedPrice[0]));
                    bottomPanelCenterAddOns.setText("Add-Ons: No add ons");
                    checkBoxMenuItemsSelectedNames.setLength(0);
                }
            }
        });

        noAddOns.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e) {
            	for (JCheckBoxMenuItem item : checkBoxMenuItems) {
            	    item.setSelected(false);
            	}

            	priceIncrease[0] = selectedPrice[0];

            	priceLabel.setText("PHP "+String.format("%.2f",selectedPrice[0]));
                    bottomPanelCenterAddOns.setText("Add-Ons: No add ons");
                    checkBoxMenuItemsSelectedNames.setLength(0);

            }
        });


        // WIP - Add action listener for the add to cart Button
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	 String orderAddons;
                if (checkBoxMenuItemsSelectedNames.length() > 2) {
                	orderAddons = checkBoxMenuItemsSelectedNames.substring(0, checkBoxMenuItemsSelectedNames.length() - 2);

                } else {
                	orderAddons="No add ons";

                }

                System.out.println(product.img()+" "+product.productName()+" " +sizeBox.getSelectedItem() + " " + priceIncrease[0]+ " " + 1 + " " + orderAddons);

                String size = (String) sizeBox.getSelectedItem();

                addToCart(
                		  product.img(),
                		  product.productName(),
                		  size,
                		  priceIncrease[0],
                		  1,
                		  orderAddons
                		);


            }
        });



        return panel;
    }






    private JPanel cartItemsPanel(final ClientOrders clientOrder) {
        final Color BLUE_COLOR = Color.decode("#123458");
        final Color TRANSPARENT = new Color(0, 0, 0, 0);
        final Dimension PANEL_SIZE = new Dimension(500, 100);
        final Font BOLD_FONT = new Font("Arial", Font.BOLD, 15);

        final JPanel itemPanelWrapper = createWrapperPanel(BLUE_COLOR, PANEL_SIZE);
        final JPanel loadingPanel = createLoadingPanel(BLUE_COLOR, BOLD_FONT, PANEL_SIZE);
        final JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setVisible(false);

        itemPanelWrapper.add(loadingPanel, BorderLayout.CENTER);

        new SwingWorker<JPanel, Void>() {
            @Override
            protected JPanel doInBackground() throws Exception {
                Thread.sleep(200); // Simulate delay

                JPanel itemPanel = new JPanel(new BorderLayout());

                itemPanel.add(createNorthPanel(clientOrder), BorderLayout.NORTH);
                itemPanel.add(createCenterPanel(clientOrder, BLUE_COLOR, BOLD_FONT), BorderLayout.CENTER);
                itemPanel.add(createSouthPanel(clientOrder), BorderLayout.SOUTH);

                JPanel westImagePanel = createImagePanel(clientOrder.img());

                contentPanel.add(westImagePanel, BorderLayout.WEST);
                contentPanel.add(itemPanel, BorderLayout.CENTER);

                return contentPanel;
            }

            @Override
            protected void done() {
                try {
                    get();
                    itemPanelWrapper.remove(loadingPanel);
                    itemPanelWrapper.add(contentPanel, BorderLayout.CENTER);
                    contentPanel.setVisible(true);
                    itemPanelWrapper.revalidate();
                    itemPanelWrapper.repaint();
                } catch (Exception e) {
                    e.printStackTrace();
                    for (Component comp : loadingPanel.getComponents()) {
                        if (comp instanceof JLabel lbl) {
							lbl.setText("Error loading item");
						}
                        if (comp instanceof JProgressBar bar) {
							bar.setVisible(false);
						}
                    }
                }
            }
        }.execute();

        return itemPanelWrapper;
    }

    private JPanel createWrapperPanel(Color borderColor, Dimension size) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor));
        panel.setMinimumSize(size);
        panel.setMaximumSize(size);
        return panel;
    }

    private JPanel createLoadingPanel(Color color, Font font, Dimension size) {
        JLabel loadingLabel = new JLabel("Loading item...");
        loadingLabel.setForeground(color);
        loadingLabel.setFont(font);
        loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setForeground(color);

        JPanel panel = new JPanel(null) {
            @Override
            public void doLayout() {
                int width = getWidth(), height = getHeight();
                int labelW = loadingLabel.getPreferredSize().width;
                int labelH = loadingLabel.getPreferredSize().height;
                int barW = Math.min(size.width / 2, width - 40);
                int barH = 20;

                loadingLabel.setBounds((width - labelW) / 2, (height - labelH - barH - 10) / 2, labelW, labelH);
                progressBar.setBounds((width - barW) / 2, loadingLabel.getY() + labelH + 10, barW, barH);
            }
        };

        panel.add(loadingLabel);
        panel.add(progressBar);
        return panel;
    }

    private JPanel createNorthPanel(ClientOrders order) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        panel.setOpaque(false);
        panel.add(new JLabel(order.productName()));
        panel.add(new JLabel(String.format("PHP %.2f", order.price())));
        return panel;
    }

    private JPanel createCenterPanel(ClientOrders order, Color color, Font font) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));

        JLabel quantityLabel = new JLabel(String.valueOf(order.quantity()));
        quantityLabel.setForeground(color);

        JButton deductBtn = createButton("-", font, color, Color.white);
        JButton increaseBtn = createButton("+", font, color, Color.white);
        JButton deleteBtn = createDeleteButton(order);

        deductBtn.addActionListener(e -> adjustQuantity(order, -1));
        increaseBtn.addActionListener(e -> adjustQuantity(order, 1));

        panel.add(deductBtn);
        panel.add(quantityLabel);
        panel.add(increaseBtn);
        panel.add(deleteBtn);

        return panel;
    }

    private JPanel createSouthPanel(ClientOrders order) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        panel.setOpaque(false);

        JLabel sizeLabel = new JLabel("Size: " + order.size());
        sizeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(sizeLabel);

        String addons = "Add ons: " + order.Addons();
        if (addons.length() > 30) {
			addons = addons.substring(0, 27) + "...";
		}

        JLabel addonsLabel = new JLabel(addons);
        addonsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(addonsLabel);

        return panel;
    }

    private JPanel createImagePanel(String imgPath) {
        JPanel panel = new JPanel(new GridBagLayout());
        ImageIcon icon = loadAndScaleImage(imgPath);
        JLabel label = new JLabel(icon);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(label, gbc);
        return panel;
    }

    private JButton createDeleteButton(ClientOrders order) {
        JButton button = new JButton();
        button.setBackground(Color.RED);

        JLabel iconLabel = new JLabel(Icons.createTrashIcon());
        JPanel iconPanel = new JPanel(new BorderLayout());
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(20, 20));
        iconPanel.add(iconLabel, BorderLayout.CENTER);
        button.add(iconPanel);

        button.addActionListener(e -> {
            button.setEnabled(false);
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    clientOrders.removeIf(o -> o.id() == order.id());
                    return null;
                }

                @Override
                protected void done() {
                    refreshAddToCart();
                }
            }.execute();
        });

        iconLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                button.doClick();
            }
        });

        return button;
    }

    private void adjustQuantity(ClientOrders order, int delta) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                for (int i = 0; i < clientOrders.size(); i++) {
                    if (clientOrders.get(i).id() == order.id()) {
                        ClientOrders current = clientOrders.get(i);
                        int quantity = current.quantity();
                        int newQuantity = quantity + delta;

                        if (newQuantity > 0) {
                            double basePrice = current.price() / quantity;
                            current.setQuantity(newQuantity);
                            current.setPrice(basePrice * newQuantity);
                            clientOrders.set(i, current);
                        } else {
                            clientOrders.remove(i);
                        }
                        break;
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                refreshAddToCart();
            }
        }.execute();
    }





    // Helper method to load and scale an image
    private ImageIcon loadAndScaleImage(String imagePath) {
        try {
            String signedUrl = GetCachedImagePath.getCachedImagePath(imagePath);
            if (signedUrl != null) {
                URL imageURL = new URL(signedUrl);
                ImageIcon imageIcon = new ImageIcon(imageURL);
                Image image = imageIcon.getImage();

                final int panelWidth = 50;
                final int panelHeight = 50;
                int imageWidth = image.getWidth(null);
                int imageHeight = image.getHeight(null);

                // Fallback in case image failed to load
                if (imageWidth <= 0 || imageHeight <= 0) {
                    System.err.println("Failed to load image dimensions.");
                    return null;
                }

                double aspectRatio = (double) imageWidth / imageHeight;
                int newWidth = panelWidth;
                int newHeight = (int) (panelWidth / aspectRatio);

                if (newHeight > panelHeight) {
                    newHeight = panelHeight;
                    newWidth = (int) (panelHeight * aspectRatio);
                }

                Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_FAST);
                return new ImageIcon(scaledImage);
            } else {
                System.err.println("Signed URL is null for imagePath: " + imagePath);
            }
        } catch (MalformedURLException e) {
            System.err.println("Invalid URL for image: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error loading or scaling image: " + e.getMessage());
        }

        return null; // Ensure method always returns a value
    }

    // Create a simple trash icon as a fallback


    // Simple interfaces to avoid Callable/Consumer dependencies
    private interface BackgroundTask<T> {
        T execute() throws Exception;
    }

    private interface CompletionHandler<T> {
        void handleCompleted(T result);
    }

    // Helper method to create buttons with consistent settings
    private JButton createButton(String text, Font font, Color bg, Color fg) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setBackground(bg);
        button.setForeground(fg);
        return button;
    }




    private synchronized void addToCart(String img, String productName, String size, double price, int quantityy, String addons) {
        System.out.println("The add to cart is working");

        // Loop through the list of clientOrders to check if the productName, size, and Addons already exist
        boolean exists = false;
        for (ClientOrders order : clientOrders) {
            if (order.productName().equals(productName) &&
                order.size().equals(size) &&
                order.Addons().equals(addons)) {

                int newQuantity = order.quantity() + quantityy;
                // Calculate price per unit (for a single item)
                double pricePerUnit = price;
                // Update with new quantity and price
                order.setQuantity(newQuantity);
                order.setPrice(pricePerUnit * newQuantity);

                exists = true;
                break;
            }
        }

        // If the order doesn't exist, add a new ClientOrder
        if (!exists) {
            clientOrders.add(new ClientOrders(img, productName, size, price, quantityy, addons));
        }

        refreshAddToCart();
    }


    public void clearCart() {
    	  clientOrders.clear();
    	  refreshAddToCart();

    }


 // Method to refresh the cart panel after changes
    public void refreshAddToCart() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                updateCartPanel();
                calculateTotal(); // Make sure this runs in the background thread
                updateBuyButtonActions();
                return null;
            }

            @Override
            protected void done() {
                cartPanelCenter.revalidate();
                cartPanelCenter.repaint();
                // Format with two decimal places
                totalLabel.setText(String.format("Total: PHP %.2f", currentTotal));
            }
        };

        worker.execute();
    }

    // === Function that uses refreshAddToCart() ===

    private void updateCartPanel() {
        cartPanelCenter.removeAll();
        for (ClientOrders order : clientOrders) {
            cartPanelCenter.add(cartItemsPanel(order));
        }
    }

 // Method to calculate the total price of all items in the cart
    private void calculateTotal() {
        currentTotal = 0.0; // Reset to ensure clean calculation
        
        for (ClientOrders order : clientOrders) {
            currentTotal += order.price();
        }
        
        System.out.println("Total order: " + currentTotal);
    }

    private void updateBuyButtonActions() {
        SwingUtilities.invokeLater(() -> {
            if (buyButton != null) {
                for (ActionListener al : buyButton.getActionListeners()) {
                    buyButton.removeActionListener(al);
                }
            }

            if (currentTotal == 0.0) {
                buyButton.addActionListener(e ->
                    JOptionPane.showMessageDialog(null, "Please add some product to cart.")
                );
            } else {
                buyButton.addActionListener(e -> handleCheckout());
            }
        });
    }

    private void handleCheckout() {
        String htmlSummary = buildHtmlSummary();
        String jsonSummary = buildJsonSummary();

        showCheckoutDialog(htmlSummary, jsonSummary);
    }

    private void showCheckoutDialog(String html, String json) {
        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setText(html);
        textPane.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        int result = JOptionPane.showConfirmDialog(
            null, scrollPane, "Do you want to checkout?", JOptionPane.YES_NO_OPTION
        );

        if (result == JOptionPane.YES_OPTION) {
            processOrder(json);
        }
    }

    private void processOrder(String jsonSummary) {
        int[] orderIdHolder = new int[1];

        try (Connection conn = dbCon.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO orders (orderSummary, total) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, jsonSummary);
            pstmt.setDouble(2, currentTotal);
            pstmt.executeUpdate();

            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    orderIdHolder[0] = keys.getInt(1);
                    System.out.println("Order inserted. ID: " + orderIdHolder[0]);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return;
        }

        showThankYouScreen(orderIdHolder[0]);
    }

    private void showThankYouScreen(int orderId) {
        JPanel loadingPanel = new LoadingRotate();
        loadingPanel.setPreferredSize(new Dimension(300, 150));

        // ✅ This is the actual container you should uate
        Container contentPane = Welcome.getWelcomeFrame().getContentPane();

        // Safety check
        if (contentPane == null) {
            System.err.println("Main frame content pane is null, cannot proceed.");
            return;
        }

        // Stop polling and clear cart before showing loading panel
        clearCart();
        refreshAddToCart();
        stopPolling();

        // Show loading panel immediately on EDT
        SwingUtilities.invokeLater(() -> {
            contentPane.removeAll();
            contentPane.add(loadingPanel, BorderLayout.CENTER);
            contentPane.revalidate();
            contentPane.repaint();
        });

        // Background worker to simulate delay and then switch to thank you screen
        SwingWorker<JPanel, Void> worker = new SwingWorker<>() {
            @Override
            protected JPanel doInBackground() throws Exception {
                Thread.sleep(1000); // Simulate delay
                return new ThankYouScreen( orderId);
            }

            @Override
            protected void done() {
                try {
                    JPanel thankYouPanel = get();

                    SwingUtilities.invokeLater(() -> {
                        contentPane.removeAll();
                        contentPane.add(thankYouPanel, BorderLayout.CENTER);
                        contentPane.revalidate();
                        contentPane.repaint();
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        worker.execute();
    }





    private String buildHtmlSummary() {
        StringBuilder message = new StringBuilder(
            "<html><body style='font-family: Segoe UI, sans-serif; color: #2c3e50;'>"
        );
        message.append("<h2>Your Order Summary</h2>");

        for (ClientOrders order : clientOrders) {
            message.append("<div style='margin-bottom: 16px; padding: 16px; background-color: #fff; border: 1px solid #ccc; border-radius: 8px;'>")
                   .append("<p><strong>Product:</strong> ").append(order.productName()).append("</p>")
                   .append("<p><strong>Quantity:</strong> ").append(order.quantity()).append("</p>")
                   .append("<p><strong>Price:</strong> Php ").append(String.format("%.2f", order.price())).append("</p>")
                   .append("<p><strong>Add-ons:</strong> ").append(order.Addons()).append("</p>")
                   .append("<p><strong>Size:</strong> ").append(order.size()).append("</p>")
                   .append("</div>");
        }

        message.append("<hr><p><strong>Total:</strong> Php ")
               .append(String.format("%.2f", currentTotal))
               .append("</p></body></html>");

        return message.toString();
    }

    private String buildJsonSummary() {
        StringBuilder json = new StringBuilder("{\n  \"orders\": [\n");

        for (int i = 0; i < clientOrders.size(); i++) {
            ClientOrders order = clientOrders.get(i);
            json.append("    {\n")
                .append("      \"productName\": \"").append(order.productName()).append("\",\n")
                .append("      \"quantity\": ").append(order.quantity()).append(",\n")
                .append("      \"price\": ").append(order.price()).append(",\n")
                .append("      \"addons\": \"").append(order.Addons()).append("\",\n")
                .append("      \"size\": \"").append(order.size()).append("\"\n")
                .append("    }");

            if (i < clientOrders.size() - 1) {
				json.append(",\n");
			} else {
				json.append("\n");
			}
        }

        json.append("  ],\n  \"totalAmount\": ")
            .append(String.format("%.2f", currentTotal))
            .append("\n}");

        return json.toString();
    }

    //==================================================================================

    //Similar to ajax where it refresh a specific dom
    private void startPolling() {
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });

        scheduler.scheduleAtFixedRate(() -> {
            try {
                // Clone old data before mutation
                List<Product> previousProducts = new ArrayList<>(ClientSideCart.products);

                // Load fresh data (this clears and mutates ClientSideCart.products)
                Welcome.loadProducts();
                Welcome.loadAddOns();

                // Now compare new and previous
                List<Product> currentProducts = new ArrayList<>(ClientSideCart.products);

                SwingUtilities.invokeLater(() -> {
                    if (!currentProducts.equals(previousProducts)) {
                        System.out.println("Product list changed. Refreshing panel...");
                        System.out.println("productPanel reloaded at " + new java.util.Date());
                        refreshProductPanel();
                    } else {
                        System.out.println("No changes detected.");
                    }
                });

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }, 0, 5, TimeUnit.SECONDS);
    }
    

private void stopPolling() {
    if (scheduler != null) {
        scheduler.shutdownNow();
    }
}



   //Refresh the page if theres a changes
private void refreshProductPanel() {
    SwingWorker<List<JPanel>, Void> worker = new SwingWorker<>() {
        @Override
        protected List<JPanel> doInBackground() {
            List<Product> snapshot;
            synchronized(ClientSideCart.products) {
                snapshot = new ArrayList<>(ClientSideCart.products);
            }

            List<JPanel> productCards = new ArrayList<>();
            for (Product product : snapshot) {
                if (product.availability()) {
                    productCards.add(createProductCard(product));
                }
            }
            return productCards;
        }

        @Override
        protected void done() {
            try {
                List<JPanel> productCards = get();

                productPanelCenter.removeAll();

                for (JPanel card : productCards) {
                    productPanelCenter.add(card);
                }

                productPanelCenter.revalidate();
                productPanelCenter.repaint();

               
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    worker.execute();
}



}



