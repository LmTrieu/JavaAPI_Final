package com.example.frame;

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
    private JButton loginButton;
    private JButton cancelButton;

    public LoginFrame() {
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
        usernameField = new JTextField(20);
        usernameField.setText("admin");

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
        passwordField = new JPasswordField(20);
        passwordField.setText("123456");

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


        setSize(352, 244);

        loginButton.addKeyListener(this);
        cancelButton.addKeyListener(this);
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
if (e.getKeyCode() == KeyEvent.VK_ENTER) {
    		if (e.getSource() == cancelButton) {
                System.exit(0);
            } else if (e.getSource() == loginButton) {
                performLogin();
            }
        } 
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Do nothing
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
				try {
			        dispose();
			        SalesFrame salesFrame = new SalesFrame();
			        salesFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
            } else if (statusCode == 400) {
            	JOptionPane.showMessageDialog(null, "Response: " + "Username or Password contain special character" + " (" + statusCode + ")");
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