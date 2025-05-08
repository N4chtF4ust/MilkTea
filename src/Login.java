import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Login extends JPanel {
    public Login(JFrame parentFrame) {
    	parentFrame.getContentPane().setBackground(new Color(15, 23, 42)); // Dark navy background
        setPreferredSize(new Dimension(500, 450));
        setBackground(new Color(209, 213, 219)); // Light gray panel
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createLineBorder(new Color(55, 65, 81), 2)); // Dark border
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        
        JLabel welcomeLabel = new JLabel("Welcome to Anjo Milktea");
        welcomeLabel.setFont(new Font("Helvetica", Font.BOLD, 20));
        welcomeLabel.setForeground(new Color(37, 99, 235));
        welcomeLabel.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridy++;
        add(welcomeLabel, gbc);
        
        JLabel titleLabel = new JLabel("USER LOGIN");
        titleLabel.setFont(new Font("Helvetica", Font.BOLD, 30));
        titleLabel.setForeground(new Color(15, 23, 42));
        gbc.gridy++;
        add(titleLabel, gbc);
        
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel userLabel = new JLabel("Username");
        add(userLabel, gbc);
        
        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextField userText = new JTextField(15);
        add(userText, gbc);
        
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel passLabel = new JLabel("Password");
        add(passLabel, gbc);
        
        gbc.gridy++;
        JPasswordField passText = new JPasswordField(15);
        add(passText, gbc);
        
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        JCheckBox rememberMe = new JCheckBox("Remember Me");
        rememberMe.setBackground(new Color(209, 213, 219));
        add(rememberMe, gbc);
        
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JButton loginButton = new JButton("LOG IN");
        loginButton.setBackground(new Color(31, 41, 55));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        add(loginButton, gbc);
        
        gbc.gridy++;
        JLabel signUpLabel = new JLabel("Doesn't Have an Account?");
        signUpLabel.setForeground(new Color(55, 65, 81));
        add(signUpLabel, gbc);
        
        gbc.gridy++;
        JLabel signUpLink = new JLabel("Sign Up Here");
        signUpLink.setForeground(new Color(37, 99, 235));
        signUpLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Clickable sign-up label
        signUpLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
           
             
                
            	JLabel loadingLabel = new JLabel("Loading...", SwingConstants.CENTER);
            	loadingLabel.setFont(new Font("Arial", Font.BOLD, 18));
            	parentFrame.getContentPane().removeAll();
            	parentFrame.getContentPane().add(loadingLabel);
            	parentFrame.revalidate();
            	parentFrame.repaint();

            	SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            	    @Override
            	    protected Void doInBackground() throws Exception {
            	        // Simulate loading time (optional)
            	        Thread.sleep(1000); // remove this if not needed

            	        return null;
            	    }

            	    @Override
            	    protected void done() {
            	    	parentFrame.getContentPane().removeAll();
            	    	parentFrame.getContentPane().add(  new SignUp());
            	    	parentFrame.revalidate();
            	    	parentFrame.repaint();
            	    }
            	    
            	    
            	};
            	worker.execute();  // Start the worker
            }
        });

        add(signUpLink, gbc);

    }

    

}
