import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class WelcomePage {
    private JFrame WelcomeFrame;
    private JPanel WelcomePanel;
    private JLabel WelcomeLabel;
    private JComboBox<String> WelcomeComboBox;
    private JButton WelcomeDoneButton;

    public WelcomePage() {
        WelcomeFrame = new JFrame("Welcome To Milktea Shop");
        WelcomeFrame.setSize(900, 600);
        WelcomeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        WelcomeFrame.setLocationRelativeTo(null);

        WelcomePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        WelcomeLabel = new JLabel("WELCOME TO MILKTEA SHOP");
        WelcomeLabel.setFont(new Font("Rockwell", Font.BOLD, 25));
        WelcomeLabel.setForeground(new Color(11, 56, 95));

        String[] options = {"Admin", "Dashboard", "Client"};
        WelcomeComboBox = new JComboBox<>(options);
        WelcomeComboBox.setForeground(new Color(11, 56, 95));

        WelcomeDoneButton = new JButton("Done");
        WelcomeDoneButton.setForeground(Color.WHITE);
        WelcomeDoneButton.setBackground(new Color(11, 56, 95));

        // Adding ActionListener to the Done button
        WelcomeDoneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedOption = (String) WelcomeComboBox.getSelectedItem();
                
                if (selectedOption.equals("Client")) {
     
                	
                	JLabel loadingLabel = new JLabel("Loading...", SwingConstants.CENTER);
                	loadingLabel.setFont(new Font("Arial", Font.BOLD, 18));
                	WelcomeFrame.getContentPane().removeAll();
                	WelcomeFrame.getContentPane().add(loadingLabel);
                	WelcomeFrame.revalidate();
                	WelcomeFrame.repaint();

                	SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                	    @Override
                	    protected Void doInBackground() throws Exception {
                	        // Simulate loading time (optional)
                	        Thread.sleep(1000); // remove this if not needed

                	        return null;
                	    }

                	    @Override
                	    protected void done() {
                	        WelcomeFrame.getContentPane().removeAll();
                	        WelcomeFrame.getContentPane().add(new clientSideCart());
                	        WelcomeFrame.revalidate();
                	        WelcomeFrame.repaint();
                	    }
                	    
                	    
                	};
                	worker.execute();  // Start the worker

            
                /*	System.out.print("asdasd");
                	clientSideCart.products.add(new Product(1, "Robert", 100,110,120,"product1.png",true));
                	clientSideCart.products.add(new Product(2, "Anjo", 200,210,220,"product2.png",true));
                	clientSideCart.products.add(new Product(3, "Delos", 300,310,320,"product1.png",true));
                	clientSideCart.products.add(new Product(4, "Reyes", 400,410,420,"product1.png",true));
                	clientSideCart.products.add(new Product(5, "Pogi", 500,510,520,"product1.png",true));
                	
                	clientSideCart.addOns.add(new AddOns(1, "Pearl",10,true));
                	clientSideCart.addOns.add(new AddOns(2, "Nata de Coco",20,true));
                	clientSideCart.addOns.add(new AddOns(3, "Crushed Oreos",30,true));
                	clientSideCart milkTeaShop = new clientSideCart();
                	
                    AddProductConsole addProductConsole = new AddProductConsole(milkTeaShop);
                    addProductConsole.addProduct(); // This will add a product via console input*/
                	
                }
                
                else if (selectedOption.equals("Admin")) {
     
                	
                	JLabel loadingLabel = new JLabel("Loading...", SwingConstants.CENTER);
                	loadingLabel.setFont(new Font("Arial", Font.BOLD, 18));
                	WelcomeFrame.getContentPane().removeAll();
                	WelcomeFrame.getContentPane().add(loadingLabel);
                	WelcomeFrame.revalidate();
                	WelcomeFrame.repaint();

                	SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                	    @Override
                	    protected Void doInBackground() throws Exception {
                	        // Simulate loading time (optional)
                	        Thread.sleep(1000); // remove this if not needed

                	        return null;
                	    }

                	    @Override
                	    protected void done() {
                	        WelcomeFrame.getContentPane().removeAll();
                	        WelcomeFrame.getContentPane().add(new Login(WelcomeFrame));
                	        WelcomeFrame.revalidate();
                	        WelcomeFrame.repaint();
                	    }
                	    
                	    
                	};
                	worker.execute();  // Start the worker

            
                	
                }
                
            }
        });



        // Layout adjustments
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        WelcomePanel.add(WelcomeLabel, gbc);

        gbc.gridy = 1;
        WelcomePanel.add(WelcomeComboBox, gbc);

        gbc.gridy = 2;
        WelcomePanel.add(WelcomeDoneButton, gbc);

        WelcomeFrame.add(WelcomePanel, BorderLayout.CENTER);
        WelcomeFrame.setVisible(true);
    }

    public static void main(String[] args) {
    	
    	  try (Connection conn = dbCon.getConnection()) {
              Statement stmt = conn.createStatement();
              ResultSet rs = stmt.executeQuery("SELECT * FROM Product");

              System.out.println("Product Table Data:");

              while (rs.next()) {
                  int id = rs.getInt("id");
                  String productName = rs.getString("productName");
                  double small = rs.getDouble("small");
                  double medium = rs.getDouble("medium");
                  double large = rs.getDouble("large");
                  String img = rs.getString("img");
                  Boolean availability = rs.getBoolean("availability");
                  clientSideCart.products.add(new Product(id, productName, small,medium,large,img,availability));

                  System.out.println("ID: " + id + ", Name: " + productName + ", Price: " + small);
              }
              
          } catch (SQLException e) {
              e.printStackTrace();
          }

    

    	
    	clientSideCart.addOns.add(new AddOns(1, "Pearl",10,true));
    	clientSideCart.addOns.add(new AddOns(2, "Nata de Coco",20,true));
    	clientSideCart.addOns.add(new AddOns(3, "Crushed Oreos",30,true));
    	 new WelcomePage();
      
    }
}
