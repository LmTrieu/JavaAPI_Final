package com.example.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.springframework.beans.factory.annotation.Value;

import com.example.DBService;
import com.example.DatabaseActionException;
import com.example.DatabaseConnector;
import com.example.frame.SalesFrame;
import com.example.model.Cart;
import com.example.model.Customer;


public class SalesDAOImpl implements SalesDAO {

//	static DBService db = new DBService();
//    private static String dbUrl = db.getDbUrl();
//
//    private static String dbUsername = db.getDbUsername();
//
//    private static String dbPassword = db.getDbPassword();
	
    
	private static List<Customer> customerlist = new ArrayList<>();
	static String col[] = {"Name","Telephone"};

	@Override
	public List<Customer> search(String searchName) {
	    List<Customer> searchResults = new ArrayList<>();
	    boolean customerFound = false;

	    for (Customer customer : customerlist) {
	        if (customer.getName().toLowerCase().contains(searchName.toLowerCase())) {
	            searchResults.add(customer);
	            customerFound = true;
	        }
	    }

	    if (!customerFound) {
	        JOptionPane.showMessageDialog(null, "Customer not found.", "Search Result", JOptionPane.ERROR_MESSAGE);
	    }

	    return searchResults;
	}


	@Override
	public void sortCustomerList(DefaultTableModel tableModel, AtomicBoolean isSorted) {
	    JTable table = SalesFrame.getTable();
	    @SuppressWarnings("unchecked")
	    TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>) table.getRowSorter();

	    if (sorter == null) {
	        sorter = new TableRowSorter<>(tableModel);
	        table.setRowSorter(sorter);
	    }

	    List<RowSorter.SortKey> sortKeys = new ArrayList<>();
	    sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING)); // Sắp xếp theo cột tên (cột 0) theo thứ tự tăng dần

	    if (isSorted.get()) {
	        sortKeys.set(0, new RowSorter.SortKey(0, SortOrder.DESCENDING)); // Nếu đã sắp xếp rồi, đảo ngược thứ tự sắp xếp
	    }

	    sorter.setSortKeys(sortKeys);
	    sorter.sort();

	    // Kiểm tra xem danh sách khách hàng ban đầu đã được lưu trước đó
	    if (customerlist != null) {
	        // Khôi phục lại danh sách khách hàng ban đầu
	        tableModel.setRowCount(0);
	        for (Customer customer : customerlist) {
	            Object[] rowData = { customer.getName(), customer.getTel() };
	            tableModel.addRow(rowData);
	        }
	    }

	    // Lưu danh sách khách hàng ban đầu sau khi sắp xếp
	    customerlist = new ArrayList<>();
	    for (int i = 0; i < tableModel.getRowCount(); i++) {
	        String name = (String) tableModel.getValueAt(i, 0);
	        int tel = (int) tableModel.getValueAt(i, 1);
	        Customer customer = new Customer(name, tel);
	        customerlist.add(customer);
	    }
	}



	@Override
	public List<Customer> getAllCustomers() {
	    List<Customer> customerList = new ArrayList<>(customerlist);
	    return customerList;
	}

	public static List<Customer> updateCartDAO() {
		try(Connection connection = DatabaseConnector.getConnection()) {
			PreparedStatement dm = connection.prepareStatement("SELECT * FROM customer INNER JOIN cart on customer.customerid = cart.customerid;");
			ResultSet rs = dm.executeQuery();
			customerlist.clear();
			while(rs.next()){
				Cart cart = new Cart(rs.getString(5),rs.getString(6),rs.getDate(7).toString(),rs.getString(8),rs.getInt(9),rs.getInt(10));
				Customer temp = new Customer(rs.getInt(1),rs.getString(2),rs.getInt(3),cart);
				customerlist.add(temp);
			}
		return customerlist;
		} catch (SQLException e) {
			throw new DatabaseActionException(e); 
		}
	}	
	public static DefaultTableModel ModelPrep() {
		SalesFrame.customer = SalesDAOImpl.updateCartDAO();
		DefaultTableModel tableModel = new DefaultTableModel(col, 0);
		for (Customer temp : customerlist) {
			Object[] t = {temp.getName(), temp.getTel()};
			tableModel.addRow(t);
		}
		return tableModel;
	}
	public static void UpdateSQL(JTable table) {
	    int selectedRowIndex = table.getSelectedRow();
	    Customer customer = SalesFrame.customer.get(selectedRowIndex);
	    Cart cart = customer.getCart();

	    if (customer.getCustomerId() != 0) {
	        // Cập nhật đơn hàng đã tồn tại
	        try (Connection connection = DatabaseConnector.getConnection()) {
	            String query = "UPDATE `cart` SET itemname = ?, salesDate = ?, salesPerson = ?, fee = ?, quantity = ? WHERE customerId = ?";
	            PreparedStatement statement = connection.prepareStatement(query);
	            statement.setString(1, cart.getItemname());
	            statement.setString(2, cart.getSalesdate());
	            statement.setString(3, cart.getSeller());
	            statement.setInt(4, cart.getFee());
	            statement.setInt(5, cart.getQuantity());
	            statement.setInt(6, customer.getCustomerId());

	            statement.executeUpdate();
	        } catch (SQLException e) {
	            throw new DatabaseActionException(e);
	        }
	    } else {
	        // Thêm mới đơn hàng
	        try (Connection connection = DatabaseConnector.getConnection()) {
	        	String query = "INSERT INTO `customer` (name, tel) VALUES (?, ?)";
	            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
	            statement.setString(1, customer.getName());
	            statement.setInt(2, customer.getTel());

	            statement.executeUpdate();
	            // Lấy customerId được sinh tự động
	            ResultSet generatedKeys = statement.getGeneratedKeys();
	            if (generatedKeys.next()) {
	                int customerId = generatedKeys.getInt(1);
	                customer.setCustomerId(customerId);

	                // Thêm mới đơn hàng
	                query = "INSERT INTO `cart` (customerId, itemname, salesDate, salesPerson, fee, quantity) VALUES (?, ?, ?, ?, ?, ?)";
	                statement = connection.prepareStatement(query);
	                statement.setInt(1, customerId);
	                statement.setString(2, cart.getItemname());
	                statement.setString(3, cart.getSalesdate());
	                statement.setString(4, cart.getSeller());
	                statement.setInt(5, cart.getFee());
	                statement.setInt(6, cart.getQuantity());

	                statement.executeUpdate();
	            }
	        } catch (SQLException e) {
	            throw new DatabaseActionException(e);
	        }
	    }
	}
	public static void DeleteRow(JTable table) {
	    int i = table.getSelectedRow();
	    Customer cs = SalesFrame.customer.get(i);
	    Cart c = cs.getCart();
	    int max = i;
	    String getmax = "SELECT MAX( `customerid` ) FROM `cart` ;";
        String query = "DELETE FROM `cart` WHERE (`customerid` = '"+c.getCustomerID()+"')";
		String query2 = "DELETE FROM `customer` WHERE (`customerid` = '"+cs.getCustomerId()+"')";
	    try (Connection connection = DatabaseConnector.getConnection()) {
            Statement statement = connection.createStatement();
            Statement get = connection.createStatement();
            ResultSet rs = get.executeQuery(getmax);
            if(rs.next()) {
            	max = rs.getInt(1);
                System.out.println(max);
            }

            String resetcart = "ALTER TABLE `cart` AUTO_INCREMENT = "+(max-1)+";";
    		String resetcust = "ALTER TABLE `customer` AUTO_INCREMENT = "+(max-1)+";";

            statement.executeUpdate(query);
            statement.executeUpdate(query2);
			statement.executeUpdate(resetcart);
			statement.executeUpdate(resetcust);
        } catch (SQLException e) {
            throw new DatabaseActionException(e);
        }
	}

}
