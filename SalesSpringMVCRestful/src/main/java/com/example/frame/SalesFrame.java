package com.example.frame;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;

import org.apache.http.client.ClientProtocolException;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import com.example.DateLabelFormatter;
import com.example.dao.SalesDAO;
import com.example.dao.SalesDAOImpl;
import com.example.model.Cart;
import com.example.model.Customer;
import java.awt.Color;
import java.awt.Toolkit;

public class SalesFrame extends JFrame {

	/**
	 *
	 *
	 */
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private LoginFrame loginFrame;
	SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
	public static DefaultTableModel tableModel = new DefaultTableModel();
	public static List<Customer> customer = new ArrayList<>();
	Calendar calendar = Calendar.getInstance();
	public static JTable table;
	private static SalesFrame frame ;
	public JTextField txtfItemName;
	public JTextField txtfFee;
	public UtilDateModel model;
	public final static AtomicBoolean isSorted = new AtomicBoolean(false);
	  
	SalesDAO salesDAO;
	Object[][] data;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    SalesFrame frame = new SalesFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

	public void setLoginFrame(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;
    }

	public static JTable getTable() {
	    return table;
	}

	public void setTable() {
		table = new JTable();
		table.setFont(new Font("Arial", Font.PLAIN, 15));
		try {
			tableModel = SalesDAOImpl.ModelPrep(table, isSorted);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		table.setModel(tableModel);
	}

	public static void updTable(JTable T) {
		table = T;
	}

	public int[] date(int i) throws NullPointerException {
		String dateArr[] = customer.get(i).getCart().getSalesdate().split("-");
		int day = Integer.parseInt(dateArr[0]) ;
		int month = Integer.parseInt(dateArr[1]);
		int year = Integer.parseInt(dateArr[2]);
		int[] temp = {day,month,year};
		return temp;
	}
	/**
	 * Create the frame.
	 * @throws IOException
	 */
	public SalesFrame() {
		setForeground(new Color(51, 255, 153));

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("SaleList");

		new JFrame();
		salesDAO = new SalesDAOImpl();

		setBounds(100, 100, 800, 550);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{791, 0};
		gridBagLayout.rowHeights = new int[]{50, 229, 150, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);

		JPanel panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		getContentPane().add(panel, gbc_panel);
		panel.setLayout(new GridLayout(1, 3, 0, 0));

		JButton btnSearch = new JButton("Search customer");
		btnSearch.setFont(new Font("Tahoma", Font.PLAIN, 18));
		panel.add(btnSearch);

		JButton btnNewCust = new JButton("Add new customer");
		btnNewCust.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AddCustFrame newcustframe = new AddCustFrame(table);
				newcustframe.setVisible(true);

			}
		});

		btnNewCust.setFont(new Font("Tahoma", Font.PLAIN, 18));
		panel.add(btnNewCust);

		// Tạo nút "Sort customer"
		
		JButton btnSort = new JButton("Sort customer");
		btnSort.setFont(new Font("Tahoma", Font.PLAIN, 18));
		panel.add(btnSort);

		// Thêm xử lý sự kiện cho nút "Sort customer"
		btnSort.addActionListener(new ActionListener() {
		    @Override
			public void actionPerformed(ActionEvent e) {
		    	
		    	isSorted.set(!isSorted.get());
		        try {
					tableModel = SalesDAOImpl.ModelPrep(table, isSorted);
					
					// Đảo ngược trạng thái cờ
					
					
				} catch (ClientProtocolException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
		        
				table.setModel(tableModel);
		        table.setRowSelectionInterval(0, 0);
		    }
		});

		JPanel panel_3 = new JPanel();
		GridBagConstraints gbc_panel_3 = new GridBagConstraints();
		gbc_panel_3.fill = GridBagConstraints.BOTH;
		gbc_panel_3.insets = new Insets(0, 0, 5, 0);
		gbc_panel_3.gridx = 0;
		gbc_panel_3.gridy = 1;
		getContentPane().add(panel_3, gbc_panel_3);
		panel_3.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		panel_3.add(scrollPane);

		//Setting up the JTable's model
		setTable();
		scrollPane.setViewportView(table);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new EmptyBorder(0, 0, 0, 0));
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 2;
		getContentPane().add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{111, 370, 0};
		gbl_panel_1.rowHeights = new int[]{29, 29, 29, 29, 29, 0};
		gbl_panel_1.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_panel_1.rowWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);

		JLabel lblItemName = new JLabel("Item name:");
		lblItemName.setFont(new Font("Tahoma", Font.PLAIN, 16));
		GridBagConstraints gbc_lblItemName = new GridBagConstraints();
		gbc_lblItemName.fill = GridBagConstraints.BOTH;
		gbc_lblItemName.insets = new Insets(0, 0, 5, 5);
		gbc_lblItemName.gridx = 0;
		gbc_lblItemName.gridy = 0;
		panel_1.add(lblItemName, gbc_lblItemName);

		txtfItemName = new JTextField();
		GridBagConstraints gbc_txtfItemName = new GridBagConstraints();
		gbc_txtfItemName.insets = new Insets(0, 0, 5, 0);
		gbc_txtfItemName.fill = GridBagConstraints.BOTH;
		gbc_txtfItemName.gridx = 1;
		gbc_txtfItemName.gridy = 0;

		panel_1.add(txtfItemName, gbc_txtfItemName);
		txtfItemName.setColumns(10);

		JLabel lblItemName_1 = new JLabel("Sales date:");
		lblItemName_1.setFont(new Font("Tahoma", Font.PLAIN, 16));
		GridBagConstraints gbc_lblItemName_1 = new GridBagConstraints();

		gbc_lblItemName_1.fill = GridBagConstraints.BOTH;
		gbc_lblItemName_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblItemName_1.gridx = 0;
		gbc_lblItemName_1.gridy = 1;
		panel_1.add(lblItemName_1, gbc_lblItemName_1);


		Properties p = new Properties();
		p.put("text.today", "Today");
		p.put("text.month", "Month");
		p.put("text.year", "Year");

		model = new UtilDateModel();
		JDatePanelImpl datePanel = new JDatePanelImpl(model, p);

		JDatePickerImpl datePicker = new JDatePickerImpl(datePanel,new DateLabelFormatter());

		GridBagConstraints gbc_DatePanel = new GridBagConstraints();
		gbc_DatePanel.insets = new Insets(0, 0, 5, 0);
		gbc_DatePanel.fill = GridBagConstraints.BOTH;
		gbc_DatePanel.gridx = 1;
		gbc_DatePanel.gridy = 1;
		panel_1.add(datePicker, gbc_DatePanel);

		JLabel lblItemName_2 = new JLabel("Sales person:");

		lblItemName_2.setFont(new Font("Tahoma", Font.PLAIN, 16));
		GridBagConstraints gbc_lblItemName_2 = new GridBagConstraints();
		gbc_lblItemName_2.fill = GridBagConstraints.BOTH;
		gbc_lblItemName_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblItemName_2.gridx = 0;
		gbc_lblItemName_2.gridy = 2;
		panel_1.add(lblItemName_2, gbc_lblItemName_2);

		JComboBox CbxSeller = new JComboBox();
		CbxSeller.setModel(new DefaultComboBoxModel(new String[] {"Lương Minh Triều", "Trần Tấn Đạt"}));
		GridBagConstraints gbc_CbxxSeller = new GridBagConstraints();
		gbc_CbxxSeller.insets = new Insets(0, 0, 5, 0);
		gbc_CbxxSeller.fill = GridBagConstraints.HORIZONTAL;
		gbc_CbxxSeller.gridx = 1;
		gbc_CbxxSeller.gridy = 2;
		panel_1.add(CbxSeller, gbc_CbxxSeller);

		JLabel lblItemName_3 = new JLabel("Fee (đ):");
		lblItemName_3.setFont(new Font("Tahoma", Font.PLAIN, 16));
		GridBagConstraints gbc_lblItemName_3 = new GridBagConstraints();
		gbc_lblItemName_3.fill = GridBagConstraints.BOTH;
		gbc_lblItemName_3.insets = new Insets(0, 0, 5, 5);
		gbc_lblItemName_3.gridx = 0;
		gbc_lblItemName_3.gridy = 3;
		panel_1.add(lblItemName_3, gbc_lblItemName_3);

		txtfFee = new JTextField();
		txtfFee.setColumns(10);

		GridBagConstraints gbc_txtfFee = new GridBagConstraints();
		gbc_txtfFee.insets = new Insets(0, 0, 5, 0);
		gbc_txtfFee.fill = GridBagConstraints.BOTH;
		gbc_txtfFee.gridx = 1;
		gbc_txtfFee.gridy = 3;
		panel_1.add(txtfFee, gbc_txtfFee);

		JLabel lblItemName_4 = new JLabel("Quantity:");
		lblItemName_4.setFont(new Font("Tahoma", Font.PLAIN, 16));
		GridBagConstraints gbc_lblItemName_4 = new GridBagConstraints();
		gbc_lblItemName_4.fill = GridBagConstraints.BOTH;
		gbc_lblItemName_4.insets = new Insets(0, 0, 0, 5);
		gbc_lblItemName_4.gridx = 0;
		gbc_lblItemName_4.gridy = 4;
		panel_1.add(lblItemName_4, gbc_lblItemName_4);

		JComboBox CbxQuant = new JComboBox();
		CbxQuant.setModel(new DefaultComboBoxModel(new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"}));
		GridBagConstraints gbc_CbxQuant = new GridBagConstraints();
		gbc_CbxQuant.fill = GridBagConstraints.HORIZONTAL;
		gbc_CbxQuant.gridx = 1;
		gbc_CbxQuant.gridy = 4;
		panel_1.add(CbxQuant, gbc_CbxQuant);

		JPanel panel_2 = new JPanel();
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 3;
		getContentPane().add(panel_2, gbc_panel_2);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[]{0, 0, 0, 0};
		gbl_panel_2.rowHeights = new int[]{41, 0};
		gbl_panel_2.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel_2.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel_2.setLayout(gbl_panel_2);

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				JTable source = (JTable)e.getSource();
	            int row = source.rowAtPoint(e.getPoint());
	            System.out.println(row);
	            Cart temp = customer.get(row).getCart();
	            String s=source.getValueAt(row, 0)+"";
	            System.out.println(s);
	            
	            for (Customer i : customer) {
	            	System.out.println(i.getName());
	            	
					if(i.getName().equals(s)) {
						System.out.println(i.getName());
			            temp = i.getCart();
					}
				}
	        	try {
	        		int[]tdate = date(row);
	        		CbxSeller.setSelectedItem(temp.getSeller());
		    		txtfItemName.setText(temp.getItemname());
		    		datePicker.getModel().setDate(tdate[0],tdate[1]-1,tdate[2]);
		    		datePicker.getModel().setSelected(true);
		    		txtfFee.setText(Integer.toString(temp.getFee()));
		    		CbxQuant.setSelectedItem(Integer.toString(temp.getQuantity()));
				} catch (Exception e2) { 
					CbxSeller.setSelectedItem("");
		    		txtfItemName.setText("");
		    		datePicker.getModel().setSelected(true);
		    		txtfFee.setText("0");
		    		CbxQuant.setSelectedItem("");
				}
			}
		});

		JButton btnSave = new JButton("Save Order");
		btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Cart temp = customer.get(table.getSelectedRow()).getCart();
					temp.setItemname(txtfItemName.getText());
					temp.setSalesdate(datePicker.getJFormattedTextField().getText());
//					temp.setSeller(txtfSalesPerson.getText());
					temp.setSeller(CbxSeller.getSelectedItem().toString());
					temp.setFee(Integer.parseInt(txtfFee.getText()));
//					temp.setQuantity(Integer.parseInt(txtfQuant.getText()));
					temp.setQuantity(Integer.parseInt((String) CbxQuant.getSelectedItem()));
					

					SalesDAOImpl.UpdateSQL(table);
				} catch (Exception e2) {
			        JOptionPane.showMessageDialog(frame, "Cart was already empty", "Message", JOptionPane.INFORMATION_MESSAGE);
				}

			}
		});

		btnSave.setFont(new Font("Tahoma", Font.PLAIN, 14));
		GridBagConstraints gbc_btnSave = new GridBagConstraints();
		gbc_btnSave.insets = new Insets(0, 0, 0, 5);
		gbc_btnSave.gridx = 0;
		gbc_btnSave.gridy = 0;
		panel_2.add(btnSave, gbc_btnSave);

		JButton btnNewButton_3 = new JButton("Log Out");
		btnNewButton_3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openLoginModule();
			}
		});
		btnNewButton_3.setFont(new Font("Tahoma", Font.PLAIN, 14));
		GridBagConstraints gbc_btnNewButton_3 = new GridBagConstraints();
		gbc_btnNewButton_3.insets = new Insets(0, 0, 0, 5);
		gbc_btnNewButton_3.gridx = 1;
		gbc_btnNewButton_3.gridy = 0;
		panel_2.add(btnNewButton_3, gbc_btnNewButton_3);

		JButton btnDeleteOrder = new JButton("Delete Order");
		btnDeleteOrder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SalesDAOImpl.DeleteRow(table);
				try {
					tableModel = SalesDAOImpl.ModelPrep(table, isSorted);
				} catch (ClientProtocolException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				table.setModel(tableModel);
			}
		});
		btnDeleteOrder.setFont(new Font("Tahoma", Font.PLAIN, 14));
		GridBagConstraints gbc_btnDeleteOrder = new GridBagConstraints();
		gbc_btnDeleteOrder.gridx = 2;
		gbc_btnDeleteOrder.gridy = 0;
		panel_2.add(btnDeleteOrder, gbc_btnDeleteOrder);

		btnSearch.addActionListener(new ActionListener() {
			@Override
		    public void actionPerformed(ActionEvent e) {
		        String searchName = JOptionPane.showInputDialog("Enter customer name:");

		        if (searchName != null) {
		            if (searchName.trim().isEmpty()) {
		                // Nếu tên tìm kiếm rỗng, thực hiện việc hiển thị lại danh sách khách hàng ban đầu
		        		try {
							tableModel = SalesDAOImpl.ModelPrep(table, isSorted);
						} catch (ClientProtocolException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
		        		table.setModel(tableModel);
//		        		scrollPane.setViewportView(table);
		            } else {
						try {
							List<Customer> searchResults = SalesDAOImpl.search(searchName);
							DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
			                tableModel.setRowCount(0);

			                for (Customer customer : searchResults) {
			                    Object[] rowData = { customer.getName(), customer.getTel() };
			                    tableModel.addRow(rowData);
			                }
						} catch (ClientProtocolException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
		            }
		        }
		    }
		});
	}
	private void openLoginModule() {
        this.setVisible(false);
        tableModel.setRowCount(0);
        table.removeAll();
        LoginFrame loginFrame = new LoginFrame();
        loginFrame.setVisible(true);
    }
	
}
