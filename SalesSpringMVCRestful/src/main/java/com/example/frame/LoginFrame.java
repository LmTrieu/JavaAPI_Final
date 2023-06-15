package com.example.frame;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.example.dao.UserDAO;
import com.example.dao.UserDAOImpl;
import com.example.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

public class LoginFrame extends JFrame implements ActionListener, KeyListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField usernameField;
    private JPasswordField passwordField;
    private UserDAO userDAO;
    private JButton loginButton;
    private JButton cancelButton;
    private JLabel lblForgotPassword;

    public LoginFrame() {
        userDAO = new UserDAOImpl();
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
        usernameField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
        passwordField = new JPasswordField(20);

        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Tahoma", Font.BOLD, 13));
        cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Tahoma", Font.BOLD, 13));
        loginButton.addActionListener(this);
        cancelButton.addActionListener(this);

        usernameLabel.setBounds(46, 53, 80, 30);
        usernameField.setBounds(136, 53, 160, 30);
        passwordLabel.setBounds(46, 94, 80, 30);
        passwordField.setBounds(136, 94, 160, 30);
        loginButton.setBounds(56, 135, 80, 30);
        cancelButton.setBounds(205, 135, 80, 30);

        getContentPane().add(usernameLabel);
        getContentPane().add(usernameField);
        getContentPane().add(passwordLabel);
        getContentPane().add(passwordField);
        getContentPane().add(loginButton);
        getContentPane().add(cancelButton);

        JLabel lblNewLabel = new JLabel("Login Form");
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblNewLabel.setBounds(46, 22, 250, 14);
        getContentPane().add(lblNewLabel);

        lblForgotPassword = new JLabel("Forgot Password");
        lblForgotPassword.setFont(new Font("Tahoma", Font.BOLD, 13));
        lblForgotPassword.setForeground(Color.BLUE);
        lblForgotPassword.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblForgotPassword.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                forgotPasswordClicked();
            }
        });
        
        // Register key binding for Ctrl + F
        int condition = JComponent.WHEN_IN_FOCUSED_WINDOW;
        InputMap inputMap = getRootPane().getInputMap(condition);
        ActionMap actionMap = getRootPane().getActionMap();
        String keyStroke = "ctrl F";
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK), keyStroke);
        actionMap.put(keyStroke, new AbstractAction() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public void actionPerformed(ActionEvent e) {
                forgotPasswordClicked();
            }
        });

        lblForgotPassword.setBounds(116, 176, 120, 20);
        getContentPane().add(lblForgotPassword);

        setSize(352, 244);

        loginButton.addKeyListener(this);
        cancelButton.addKeyListener(this);
        lblForgotPassword.addKeyListener(this);
    }

    private void forgotPasswordClicked() {
        String username;
        User user;

        do {
            username = JOptionPane.showInputDialog(LoginFrame.this, "Enter your username:");
            if (username != null) {
                user = userDAO.getPasswordByUsername(username);
                if (user == null) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Invalid username.");
                }
            } else {
                return;
            }
        } while (user == null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        JPasswordField newPasswordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();

        panel.add(new JLabel("New Password:"));
        panel.add(newPasswordField);
        panel.add(new JLabel("Confirm Password:"));
        panel.add(confirmPasswordField);

        int option;
        String newPassword;
        String confirmPassword;
        do {
            option = JOptionPane.showOptionDialog(LoginFrame.this, panel, "Change Password",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);

            if (option == JOptionPane.OK_OPTION) {
                newPassword = String.valueOf(newPasswordField.getPassword());
                confirmPassword = String.valueOf(confirmPasswordField.getPassword());

                if (!newPassword.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Passwords do not match.");
                }
            } else {
                return;
            }
        } while (!newPassword.equals(confirmPassword));

        if (option == JOptionPane.OK_OPTION) {
            String hashedPassword = hashPassword(newPassword);
            user.setPassword(hashedPassword);
            boolean updated = userDAO.updateUser(user);
            if (updated) {
                JOptionPane.showMessageDialog(LoginFrame.this, "Password changed successfully.");
            } else {
                JOptionPane.showMessageDialog(LoginFrame.this, "Failed to change password.");
            }
        }
    }

    private String hashPassword(String newPassword) {
        return DigestUtils.md5Hex(newPassword);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            performLogin();
        } else if (e.getSource() == cancelButton) {
            System.exit(0);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_F) {
            lblForgotPassword.setForeground(Color.BLUE);
            forgotPasswordClicked();
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (e.getSource() == usernameField || e.getSource() == passwordField) {
                performLogin();
            } else if (e.getSource() == cancelButton) {
                System.exit(0);
            } else if (e.getSource() == loginButton) {
                performLogin();
            }
        } else if (e.getKeyCode() == KeyEvent.VK_TAB) {
            if (e.getSource() == usernameField) {
                passwordField.requestFocus();
            } else if (e.getSource() == passwordField) {
                if (loginButton.isEnabled()) {
                    loginButton.requestFocus();
                } else {
                    cancelButton.requestFocus();
                }
            } else if (e.getSource() == loginButton) {
                if (cancelButton.isEnabled()) {
                    cancelButton.requestFocus();
                } else {
                    usernameField.requestFocus();
                }
            } else if (e.getSource() == cancelButton || e.getSource() == lblForgotPassword) {
                usernameField.requestFocus();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_TAB && e.getSource() == lblForgotPassword) {
            lblForgotPassword.setForeground(Color.BLUE);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Do nothing
    }

    private void performLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost("http://localhost:8080/SalesSpringMVCRestful/authenticate");

            User user = new User(username, password);

            String jsonUser = user.toJson();

            StringEntity entity = new StringEntity(jsonUser);
            entity.setContentType("application/json");
            request.setEntity(entity);

            HttpResponse response = httpClient.execute(request);

            HttpEntity responseEntity = response.getEntity();
            String responseBody = EntityUtils.toString(responseEntity);

            // Kiểm tra mã trạng thái HTTP
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                // Xử lý đăng nhập thành công
                JOptionPane.showMessageDialog(null, "Response: " + responseBody + " (" + statusCode + ")");
            } else if (statusCode == 400) {
            	JOptionPane.showMessageDialog(null, "Response: " + responseBody + " (" + statusCode + ")");
            } else {
                JOptionPane.showMessageDialog(null, "Response: " + responseBody + " (" + statusCode + ")");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

	public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    LoginFrame frame = new LoginFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
