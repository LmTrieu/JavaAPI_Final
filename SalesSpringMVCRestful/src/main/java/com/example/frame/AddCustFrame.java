package com.example.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.example.dao.*;
import com.example.model.*;

public class AddCustFrame extends JFrame {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;

	/**
	 * Create the frame.
	 */
	public AddCustFrame(final JTable table) {
		setForeground(new Color(128, 128, 128));
		setType(Type.UTILITY);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 402, 260);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JLabel lblNewLabel = new JLabel("Add New Customer");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblNewLabel, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		JLabel lblNewLabel_1 = new JLabel("Full Name:");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNewLabel_1.setBounds(42, 26, 101, 19);
		panel.add(lblNewLabel_1);
		
		JLabel lblNewLabel_1_1 = new JLabel("Telephone:");
		lblNewLabel_1_1.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNewLabel_1_1.setBounds(42, 77, 101, 19);
		panel.add(lblNewLabel_1_1);
		
		textField = new JTextField();
		textField.setBounds(52, 44, 245, 31);
		panel.add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(52, 100, 245, 31);
		panel.add(textField_1);
		
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.SOUTH);
		
		JButton btnNewButton = new JButton("Create");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String a = textField.getText();
				String b = textField_1.getText();
				Object[] row = {a,b};
			    DefaultTableModel model = (DefaultTableModel) SalesFrame.table.getModel();
			    model.addRow(row);
			    Date d = new java.sql.Date(System.currentTimeMillis());
			    for (int i = 0; i < table.getRowCount(); i++) {
			    	System.out.println(table.getValueAt(i, 0).toString());
					if(a.equals(table.getValueAt(i, 0).toString())) {
						table.setRowSelectionInterval(i, i);
						System.out.println(table.getSelectedRow());
						SalesFrame.customer.add(new Customer(a,Integer.parseInt(b),new Cart(null, null, d.toString(), null, 0, 0)));
						SalesDAOImpl.UpdateSQL(table);				    	
					}
				}
			    dispose();
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panel_1.add(btnNewButton);
		
		JButton btnCancek = new JButton("Cancel");
		btnCancek.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnCancek.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panel_1.add(btnCancek);
	}
}
