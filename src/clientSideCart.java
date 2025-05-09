
import org.apache.batik.swing.JSVGCanvas;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.event.ActionEvent;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.plaf.basic.BasicComboBoxUI;
import java.io.File;
import java.util.HashSet;
import java.util.Set;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;


public class clientSideCart extends JPanel {
    private DefaultListModel<String> cartModel;
    private JList<String> cartList;
    private JLabel totalLabel;
    private int totalPrice = 0;
    public static List<AddOns> addOns = new ArrayList<>();
    public static List<Product> products = new ArrayList<>();
    public static List<ClientOrders> clientOrders = new ArrayList<>();
    //CartPanel
    public static JPanel cartPanel = new JPanel();
    //productPanel
    public static JPanel productPanel = new JPanel();
    public static JPanel productPanelCenter ;
    public static JPanel cartPanelCenter = new JPanel();
    
    public clientSideCart(	) {
      
        setSize(900, 600);
      
        setLayout(new BorderLayout());

        // Cart Panel
        cartPanel.setLayout(new BorderLayout());

   
        cartPanel.setBackground(new Color(200, 190, 180));
        
        
        JSVGCanvas cartIcon = new JSVGCanvas();
     // Set preferred size (e.g., 32x32 or whatever fits your UI)
        cartIcon.setPreferredSize(new Dimension(32, 32)); // 👈 scale to desired size


     
      
        File svgFile = new File("resources/icons/cart-fill.svg");
        cartIcon.setURI(svgFile.toURI().toString());

  

        JLabel cartLabel = new JLabel("Cart");
        cartLabel.setFont(  new Font("Arial", Font.BOLD, 24));
        cartLabel.setBorder(new EmptyBorder(9,9,9,9));
      

        // Create JPanel and add label and canvas
        JPanel cartPanelNorth = new JPanel();
        cartPanelNorth.setBackground(Color.WHITE);
   
      
        cartPanelNorth.setBorder(new LineBorder(Color.decode("#123458"), 2));

        cartPanelNorth.add(cartIcon);
        cartPanelNorth.add(cartLabel);
    

        // Add the panel to your main panel or frame
        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.add(cartPanelNorth, BorderLayout.NORTH);
     
        cartPanelCenter.setLayout(new BoxLayout(cartPanelCenter, BoxLayout.Y_AXIS));

        // Wrap cartPanelCenter inside JScrollPane
        JScrollPane scrollPane = new JScrollPane(cartPanelCenter);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(400,600));


        // Add JScrollPane instead of cartPanelCenter
        cartPanel.add(scrollPane, BorderLayout.CENTER);

        for (ClientOrders clientOrder:clientOrders) {
        	
        	  cartPanelCenter.add(cartItemsPanel( clientOrder));

        }
            


        totalLabel = new JLabel("Total: PHP 0");
        JPanel bottomPanel = new JPanel();
        JButton emptyButton = new JButton("Empty");
        JButton buyButton = new JButton("Buy");
        
        emptyButton.setBackground(Color.RED);
        emptyButton.setForeground(Color.WHITE);
        emptyButton.addActionListener(e -> clearCart());
        
        buyButton.setBackground(Color.GREEN);
        buyButton.setForeground(Color.WHITE);
        buyButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Purchase Complete!"));
        
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
        
        JLabel productPanelNorthText = new JLabel("Milk Tea");
        productPanelNorthText.setFont(new Font("Arial", Font.BOLD, 24)); // Set font size and style
        productPanelNorthText.setForeground(Color.WHITE); // Set text color
        productPanelNorth.add(productPanelNorthText);
        
        
        productPanelCenter = new JPanel();
        productPanelCenter.setBackground(Color.white);
        productPanelCenter.setBorder(new EmptyBorder(20, 20, 20, 20));
  
       
        productPanel.add(productPanelCenter,BorderLayout.CENTER);
        
       
      for (Product product:products) {
    	  if(product.availability) {
    		  productPanelCenter.add(createProductCard(product));
    	  }
    
    	  
      }
        
        JScrollPane productPanelCenterScrollPane = new JScrollPane(productPanelCenter);
        productPanelCenterScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

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
                    columns = 4;  // Large screen
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
        
        revalidate(); 
        repaint(); 
        setVisible(true);
    }
    
    //Thjs is the product card with event handling
    
    private JPanel createProductCard(Product product) {
    	final double[] selectedPrice = {product.small};
        final double[] priceIncrease = { selectedPrice[0] };
    	JLabel priceLabel = new JLabel("PHP "+String.format("%.2f",product.small));
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
        
        JButton dropdownButton = new JButton("Select Add-Ons");
        dropdownButton.setBackground(Color.decode("#123458"));
        dropdownButton.setForeground(Color.white); 
        JPopupMenu popupMenu = new JPopupMenu();
        
        // Checkbox items
        int checkBoxMenuItemsIndex = 0;
        JCheckBoxMenuItem[] checkBoxMenuItems = new JCheckBoxMenuItem[addOns.size()];
        
        for (AddOns addon : addOns) {
        checkBoxMenuItems[checkBoxMenuItemsIndex] = new JCheckBoxMenuItem(addon.AddOnName + " - PHP " + String.format("%.2f", addon.AddOnPrice));
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
        ImageIcon imageIcon = new ImageIcon("resources/milkTeaImages/"+product.img); // Relative path to the image
        Image image = imageIcon.getImage(); // Get the Image object
        
        // Calculate the scaling factor to maintain aspect ratio
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

        // Scale the image to fit while maintaining aspect ratio
        Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        ImageIcon scaledImageIcon = new ImageIcon(scaledImage); // Create a new ImageIcon with the scaled image
        
        // Create a JLabel to hold the scaled image
        JLabel imageLabel = new JLabel(scaledImageIcon);
        panel.add(imageLabel, BorderLayout.CENTER);

        JLabel nameLabel = new JLabel(product.productName);
 
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
                    	priceIncrease[0]+= addon.AddOnPrice;
                        if (!checkBoxMenuItemsSelectedNames.toString().contains(addon.AddOnName)) {
                        	checkBoxMenuItemsSelectedNames.append(addon.AddOnName).append(", ");
                        }
                    	
                    } else if (!source.isSelected()) {
                     
                        
                        if(priceIncrease[0]>selectedPrice[0]) {
                        	priceIncrease[0]-= addon.AddOnPrice;
                        }
                        
                        // Remove the deselected item from selectedNames
                        String nameToRemove = addon.AddOnName + ", ";
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
                            selectedPrice[0] = product.small;
                     
                            break;
                        case "Medium":
                            selectedPrice[0] = product.medium;
                            break;
                        case "Large":
                            selectedPrice[0] = product.large;
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
                
                System.out.println(product.img+" "+product.productName+" " +sizeBox.getSelectedItem() + " " + priceIncrease[0]+ " " + 1 + " " + orderAddons);

                String size = (String) sizeBox.getSelectedItem();
                
                addToCart(
                		  product.img, 
                		  product.productName, 
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
        // Pre-define colors and dimensions to avoid repeated creation
        final Color TRANSPARENT = new Color(0, 0, 0, 0);
        final Color BLUE_COLOR = Color.decode("#123458");
        final Dimension PANEL_SIZE = new Dimension(500, 100);
        final Font BOLD_FONT = new Font("Arial", Font.BOLD, 15);
        
        // Create the main wrapper panel that will be returned and shown immediately
        final JPanel itemPanelWrapper = new JPanel(new BorderLayout());
        itemPanelWrapper.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        itemPanelWrapper.setMinimumSize(PANEL_SIZE);
        itemPanelWrapper.setMaximumSize(PANEL_SIZE);
        
     // Create a loading panel to show while content is loading
        final JPanel loadingPanel = new JPanel(new GridBagLayout());
        loadingPanel.setPreferredSize(PANEL_SIZE);

        // Create a progress bar for the loading animation
        final JProgressBar mainLoadingBar = new JProgressBar();
        mainLoadingBar.setIndeterminate(true);
        mainLoadingBar.setPreferredSize(new Dimension(200, 20));
        mainLoadingBar.setForeground(BLUE_COLOR);

        // Create a label for the loading text
        final JLabel loadingLabel = new JLabel("Loading item...");
        loadingLabel.setForeground(BLUE_COLOR);
        loadingLabel.setFont(BOLD_FONT);

        // Add loading components to the loading panel
        GridBagConstraints loadingGbc = new GridBagConstraints();
        loadingGbc.gridx = 0;
        loadingGbc.anchor = GridBagConstraints.CENTER;
        loadingGbc.insets = new Insets(10, 0, 10, 0); // Add spacing between components

        // Add label
        loadingGbc.gridy = 0;
        loadingPanel.add(loadingLabel, loadingGbc);

        // Add progress bar
        loadingGbc.gridy = 1;
        loadingPanel.add(mainLoadingBar, loadingGbc);

        // Ensure loading panel is centered inside its container
        itemPanelWrapper.setLayout(new BorderLayout());
        itemPanelWrapper.add(loadingPanel);

        
        // Create the actual content panel that will be populated asynchronously
        final JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setVisible(false); // Initially hidden while loading
        
        // Build all components in a background thread
        SwingWorker<JPanel, Void> panelBuilder = new SwingWorker<JPanel, Void>() {
            @Override
            protected JPanel doInBackground() throws Exception {
                // Simulate loading time (remove in production)
                Thread.sleep(200);
                
                // Create the final content panel
                final JPanel itemPanel = new JPanel(new BorderLayout());
                
                // === NORTH PANEL ===
                final JPanel itemPanelNorth = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
                itemPanelNorth.setOpaque(false);
                
                // Format price string only once
                final String formattedPrice = String.format("PHP %.2f", clientOrder.price);
                
                // Add components without creating unnecessary variables
                itemPanelNorth.add(new JLabel(clientOrder.productName));
                itemPanelNorth.add(new JLabel(formattedPrice));
                itemPanel.add(itemPanelNorth, BorderLayout.NORTH);
                
                // === CENTER PANEL ===
                final JPanel itemPanelCenter = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
                
                // Quantity controls
           
                final JButton deductButton = createButton("-", BOLD_FONT, Color.white, BLUE_COLOR);
                deductButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Your logic here
                        System.out.println("Deduct button clicked");
                        
                        deductButton.setEnabled(false);
               
                        
                        // Perform deletion asynchronously to avoid UI freezes
                        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                        	@Override
                        	protected Void doInBackground() {
                        	    for (int i = 0; i < clientOrders.size(); i++) {
                        	        if (clientOrders.get(i).id() == clientOrder.id()) {
                        	            ClientOrders order = clientOrders.get(i);
                        	            int currentQuantity = order.quantity();

                        	            int newQuantity = currentQuantity - 1;

                        	            if (newQuantity > 0) {
                        	                double basePrice = order.price() / (double) currentQuantity;
                        	                double newPrice = order.price() - basePrice;

                        	                order.setQuantity(newQuantity);
                        	                order.setPrice(newPrice);

                        	                clientOrders.set(i, order);
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
                                // Update UI in the EDT after background task completes
           
                                refreshAddToCart();
                            }
                        };
                        worker.execute();
                      
                    }
                });

                final JLabel quantity = new JLabel(""+clientOrder.quantity);
                quantity.setForeground(BLUE_COLOR);
                final JButton increaseButton = createButton("+", BOLD_FONT, Color.white, BLUE_COLOR);
                
                increaseButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Your logic here
                        System.out.println("increase button clicked");
                        
                        increaseButton.setEnabled(false);
               
                        
                        // Perform deletion asynchronously to avoid UI freezes
                        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                        	@Override
                        	protected Void doInBackground() {
                        	    for (int i = 0; i < clientOrders.size(); i++) {
                        	        if (clientOrders.get(i).id() == clientOrder.id()) {
                        	            ClientOrders order = clientOrders.get(i);
                        	            int currentQuantity = order.quantity();

                        	            int newQuantity = currentQuantity + 1;

                        	
                        	                double basePrice = order.price() / (double) currentQuantity;
                        	                double newPrice = order.price() + basePrice;

                        	                order.setQuantity(newQuantity);
                        	                order.setPrice(newPrice);

                        	                clientOrders.set(i, order);
                        	  

                        	            break;
                        	        }
                        	    }
                        	    return null;
                        	}


                            
                            @Override
                            protected void done() {
                                // Update UI in the EDT after background task completes
           
                                refreshAddToCart();
                            }
                        };
                        worker.execute();
                      
                    }
                });
                
                itemPanelCenter.add(deductButton);
                itemPanelCenter.add(quantity);
                itemPanelCenter.add(increaseButton);
                
                // Delete button and spinner
                final JButton deleteButton = new JButton("");
                deleteButton.setBackground(Color.white);
                
                // Delete button icon panel
                final JPanel deleteIconPanel = new JPanel(new BorderLayout());
                deleteIconPanel.setSize(20, 20);
                deleteIconPanel.setOpaque(false);
                deleteIconPanel.setBackground(TRANSPARENT);
                
                // Load icon asynchronously (will be added later in the EDT)
                final ImageIcon trashIcon = createTrashIcon();
                
                // Create an icon-based JLabel instead of using SVG for better compatibility
                final JLabel deleteIcon = new JLabel(trashIcon);
                deleteIcon.setSize(20, 20);
                deleteIconPanel.add(deleteIcon, BorderLayout.CENTER);
                
                // Add icon to button
                deleteButton.add(deleteIconPanel);
                itemPanelCenter.add(deleteButton);
    
                // Delete action
                deleteButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Show loading indicator
                        deleteButton.setEnabled(false);
               
                        
                        // Perform deletion asynchronously to avoid UI freezes
                        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                            @Override
                            protected Void doInBackground() {
                                // Use more efficient removal technique
                                clientOrders.removeIf(order -> order.id() == clientOrder.id);
                                return null;
                            }
                            
                            @Override
                            protected void done() {
                                // Update UI in the EDT after background task completes
           
                                refreshAddToCart();
                            }
                        };
                        worker.execute();
                    }
                });
                
                // Add delete icon click listener
                deleteIcon.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent evt) {
                        deleteButton.doClick();
                    }
                });
                
                itemPanel.add(itemPanelCenter, BorderLayout.CENTER);
                
                // === WEST PANEL (IMAGE) ===
                final JPanel itemPanelWrapperWest = new JPanel(new GridBagLayout());
                
                // Load and scale image (this is done in background thread already)
                final ImageIcon scaledImageIcon = loadAndScaleImage(clientOrder.img);
                final JLabel imageLabel = new JLabel(scaledImageIcon);
                
                // Create and configure constraints once
                final GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.anchor = GridBagConstraints.CENTER;
                gbc.fill = GridBagConstraints.NONE;
                gbc.insets = new Insets(10, 10, 10, 10);
                
                itemPanelWrapperWest.add(imageLabel, gbc);
                
                // === SOUTH PANEL ===
                final JPanel itemPanelSouth = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
                itemPanelSouth.add(new JLabel("Size: " + clientOrder.size));
                itemPanelSouth.add(new JLabel("Add ons: " + clientOrder.Addons));
                itemPanelSouth.setOpaque(false);
                
                itemPanel.add(itemPanelSouth, BorderLayout.SOUTH);
                
                // Put it all together
                contentPanel.add(itemPanelWrapperWest, BorderLayout.WEST);
                contentPanel.add(itemPanel, BorderLayout.CENTER);
                
                return contentPanel;
            }
            
            @Override
            protected void done() {
                try {
                    // Get the completed panel and show it
                    get(); // Will re-throw any exceptions that occurred
                    
                    // Remove loading panel and show content
                    itemPanelWrapper.remove(loadingPanel);
                    itemPanelWrapper.add(contentPanel, BorderLayout.CENTER);
                    contentPanel.setVisible(true);
                    
                    // Force UI update
                    itemPanelWrapper.revalidate();
                    itemPanelWrapper.repaint();
                } catch (Exception ex) {
                    // Handle error (show error message instead of loading)
                    ex.printStackTrace();
                    loadingLabel.setText("Error loading item");
                    mainLoadingBar.setVisible(false);
                }
            }
        };
        
        // Start the async loading process
        panelBuilder.execute();
        
        return itemPanelWrapper;
    }

    // Helper method to create buttons with consistent settings


    // Helper method to load and scale an image
    private ImageIcon loadAndScaleImage(String imagePath) {
        // Load image
        ImageIcon imageIcon = new ImageIcon("resources/milkTeaImages/" + imagePath);
        Image image = imageIcon.getImage();
        
        // Fixed dimensions to avoid recalculation
        final int panelWidth = 50;
        final int panelHeight = 50;
        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);

        // Calculate scaling only once
        double aspectRatio = (double) imageWidth / imageHeight;
        int newWidth = panelWidth;
        int newHeight = (int) (panelWidth / aspectRatio);
        
        if (newHeight > panelHeight) {
            newHeight = panelHeight;
            newWidth = (int) (panelHeight * aspectRatio);
        }

        // Use SCALE_FAST for better performance when appropriate
        Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_FAST);
        return new ImageIcon(scaledImage);
    }

    // Helper method to create a trash icon (as an alternative to SVG)
    private ImageIcon createTrashIcon() {
        // Try to load the existing trash icon first
        try {
            File svgFile = new File("resources/icons/trash3-fill.svg");
            // If SVG file exists, attempt to use it
            if (svgFile.exists()) {
                try {
                    BufferedImage image = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2d = image.createGraphics();
                    
                    // Create a simple trash icon as a fallback if SVG loading fails
                    g2d.setColor(Color.RED);
                    g2d.drawRect(3, 2, 14, 2);
                    g2d.drawRect(5, 4, 10, 14);
                    g2d.drawLine(8, 6, 8, 16);
                    g2d.drawLine(12, 6, 12, 16);
                    
                    g2d.dispose();
                    return new ImageIcon(image);
                } catch (Exception e) {
                    // SVG conversion failed, use fallback icon
                    return createFallbackTrashIcon();
                }
            } else {
                // SVG file doesn't exist, use fallback icon
                return createFallbackTrashIcon();
            }
        } catch (Exception e) {
            return createFallbackTrashIcon();
        }
    }

    // Create a simple trash icon as a fallback
    private ImageIcon createFallbackTrashIcon() {
        BufferedImage image = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        // Enable anti-aliasing for smoother lines
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Set color to match the theme
        g2d.setColor(Color.decode("#123458"));
        
        // Draw trash can outline
        g2d.drawRect(3, 2, 14, 2);  // Top of trash can
        g2d.drawRect(5, 4, 10, 14); // Body of trash can
        
        // Draw lines for the trash can details
        g2d.drawLine(8, 6, 8, 16);  // Left line inside trash
        g2d.drawLine(12, 6, 12, 16); // Right line inside trash
        
        g2d.dispose();
        return new ImageIcon(image);
    }

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


    // Helper method to execute async operations with loading indicators
    private <T> void executeAsync(
            JComponent loadingIndicator,
            JComponent disableWhileLoading,
            BackgroundTask<T> backgroundTask,
            CompletionHandler<T> onComplete) {
        
        // Show loading indicator
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(true);
        }
        
        // Disable component while loading if needed
        if (disableWhileLoading != null) {
            disableWhileLoading.setEnabled(false);
        }
        
        SwingWorker<T, Void> worker = new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                return backgroundTask.execute();
            }
            
            @Override
            protected void done() {
                try {
                    // Get the result and pass to completion handler
                    T result = get();
                    
                    // Hide loading indicator
                    if (loadingIndicator != null) {
                        loadingIndicator.setVisible(false);
                    }
                    
                    // Re-enable component
                    if (disableWhileLoading != null) {
                        disableWhileLoading.setEnabled(true);
                    }
                    
                    // Call the completion handler
                    if (onComplete != null) {
                        onComplete.handleCompleted(result);
                    }
                } catch (Exception ex) {
                    // Handle errors
                    ex.printStackTrace();
                    
                    // Hide loading indicator
                    if (loadingIndicator != null) {
                        loadingIndicator.setVisible(false);
                    }
                    
                    // Re-enable component
                    if (disableWhileLoading != null) {
                        disableWhileLoading.setEnabled(true);
                    }
                }
            }
        };
        
        // Start the worker
        worker.execute();
    }

    private synchronized void addToCart(String img, String productName, String size, double price, int quantityy, String addons) {
        System.out.println("The add to cart is working");

        // Loop through the list of clientOrders to check if the productName, size, and Addons already exist
        boolean exists = false;
        for (int i = 0; i < clientOrders.size(); i++) {
            ClientOrders order = clientOrders.get(i);
            if (order.productName.equals(productName) &&
                order.size.equals(size) &&
                order.Addons.equals(addons)) {

                // Update the quantity or replace the entire object
                order.quantity += quantityy;
                
             // Assume `order.unitPrice` holds the original price for one item
                order.price = price * order.quantity;


                // OR if you want to replace the whole object:


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

    
    private void clearCart() {
    	  clientOrders.clear(); 
    	  refreshAddToCart();
 
    }
    
    public void refreshAddToCart() {
        // Use a SwingWorker to handle UI updates
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                cartPanelCenter.removeAll();  // Clear existing cart items

                // Add updated cart item panels
                for (ClientOrders clientOrder : clientOrders) {
                    cartPanelCenter.add(cartItemsPanel(clientOrder));  // This must show updated quantity
                }

                return null;
            }

            @Override
            protected void done() {
                cartPanelCenter.revalidate();  // Recalculate layout
                cartPanelCenter.repaint();     // Redraw panel
            }
        };
        worker.execute();  // Start background task
    }


    
    
    
   //Refresh the page if theres a changes 
    public void refreshProductPanel() {
        productPanelCenter.removeAll(); // Clear existing products
        for (Product product : products) {
            if (product.availability) {
                productPanelCenter.add(createProductCard(product)); // Add product card again
            }
        }
        productPanelCenter.revalidate(); 
        productPanelCenter.repaint(); 
    }
    
    public static void main(String[] args) {

    	/*Creating a fake data*/
    	

    	
     
    	 clientSideCart milkTeaShop = new clientSideCart();
        AddProductConsole addProductConsole = new AddProductConsole(milkTeaShop);
        addProductConsole.addProduct(); // This will add a product via console input
    }
}



