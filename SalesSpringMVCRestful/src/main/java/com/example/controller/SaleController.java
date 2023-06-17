package com.example.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.Cart;
import com.example.model.Customer;

@RestController
@RequestMapping("/sales")

public class SaleController {
	
	@Value("${db.url}")
    private String dbUrl;

    @Value("${db.username}")
    private String dbUsername;

    @Value("${db.password}")
    private String dbPassword;
    
//	LIST OF METHODS
//  {GET [/sales]}: getCustomerList()
//	{GET [/sales/{id}]}: getCart(int)  
//	{PUT [/sales/{id}]}: updateCustomer(int,Customer)
//	{DELETE [/sales/cart/{id}]}: deleteCart(int)
//	{DELETE [/sales/{id}]}: deleteCustomer(int)
//	{POST [/sales/customer]}: addCustomer(Customer)
//	{POST [/sales/cart/{id}]: addCart(int,Cart)
    
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<Map<Integer,Object>> getCustomerList(){
		try (Connection connection = DriverManager.getConnection(dbUrl,dbUsername,dbPassword);
	             Statement statement = connection.createStatement();
	             ResultSet resultSet = statement.executeQuery("SELECT * FROM customer")) {
	            Map<Integer, Object> customerlist = new HashMap<>();
	            while (resultSet.next()) {
	                int id = resultSet.getInt("customerid");
	                Map<String, Object> customerMap = new HashMap<>();
	                customerMap.put("customerid", id);
	                customerMap.put("name", resultSet.getString("name"));
	                customerMap.put("tel", resultSet.getInt("tel"));
	                customerlist.put(id,customerMap);
	            }
	            return ResponseEntity.ok(customerlist);
	        } catch (SQLException e) {
	            e.printStackTrace();
	            System.out.println("help");
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	        }
		
	}
	
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Customer> getCart(@PathVariable("id") int customerid) {
    	try (Connection connection = DriverManager.getConnection(dbUrl,dbUsername,dbPassword);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM customer INNER JOIN cart on customer.customerid = cart.customerid AND customer.customerid = ?")) {
            statement.setInt(1, customerid);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Cart cart = new Cart(rs.getString(5),rs.getString(6),rs.getDate(7).toString(),rs.getString(8),rs.getInt(9),rs.getInt(10));
    				Customer cust = new Customer(rs.getInt(1),rs.getString(2),rs.getInt(3),cart);
                    return ResponseEntity.ok(cust);
                } else {
                    return ResponseEntity.notFound().build();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<String> updateCustomer(@PathVariable("id") int id, @RequestBody Customer customerupt) {
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement statement = connection.prepareStatement("UPDATE customer SET name = '?', tel = ? WHERE customerid = ?")) {
        	System.out.println(customerupt.getName() + customerupt.getTel());
            statement.setString(1, customerupt.getName());
            statement.setInt(2, customerupt.getTel());
            statement.setInt(3, id);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                return ResponseEntity.ok("Customer updated successfully.");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequestMapping(value = "cart/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteCart(@PathVariable("id") int id) {
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement statement2 = connection.prepareStatement("DELETE FROM cart WHERE customerid = ?");
        	 ) {
        	String getmax = "SELECT MAX( `customerid` ) FROM `cart` ;";
            statement2.setInt(1,id);
            Statement set = connection.createStatement();
            ResultSet rs = set.executeQuery(getmax);
    
            int max = id;
            if(rs.next())
            	max = rs.getInt(1);
            
            String resetcart = "ALTER TABLE `cart` AUTO_INCREMENT = "+(max-1)+";";
            int cartrowsAffected = statement2.executeUpdate();
            
            set.executeUpdate(resetcart);
            if (cartrowsAffected > 0) {
        		return ResponseEntity.ok("Customer's cart deleted successfully.");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteCustomer(@PathVariable("id") int id) {
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM customer WHERE customerid = ?");
             PreparedStatement statement2 = connection.prepareStatement("DELETE FROM cart WHERE customerid = ?");
        	 ) {
        	String getmax = "SELECT MAX( `customerid` ) FROM `cart` ;";
            statement.setInt(1, id);
            statement2.setInt(1,id);
            Statement set = connection.createStatement();
            ResultSet rs = set.executeQuery(getmax);
            
            int max = id;
            if(rs.next())
            	max = rs.getInt(1);
            String resetcart = "ALTER TABLE `cart` AUTO_INCREMENT = "+(max-1)+";";
    		String resetcust = "ALTER TABLE `customer` AUTO_INCREMENT = "+(max-1)+";";
            int cartrowsAffected = statement2.executeUpdate(),
        		custrowsAffected = statement.executeUpdate();
            
            set.executeUpdate(resetcart);
			set.executeUpdate(resetcust);
            if (custrowsAffected > 0) {  
            	if(cartrowsAffected > 0)
            		return ResponseEntity.ok("Customer & cart deleted successfully.");
            	else {
            		return ResponseEntity.ok("Customer deleted successfully.");
				}
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
   
    @RequestMapping(value="/customer",method = RequestMethod.POST)
    public ResponseEntity<String> addCustomer(@RequestBody Customer newCustomer) {
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO customer (customerid, name, tel) VALUES (?, ?, ?)")) {
            statement.setInt(1, newCustomer.getCustomerId());
            statement.setString(2, newCustomer.getName());
            statement.setInt(3, newCustomer.getTel());
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {	
                return ResponseEntity.ok("Customer added successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @RequestMapping(value="/cart/{id}",method = RequestMethod.POST,consumes = ("application/json; charset=UTF-8"))
    public ResponseEntity<String> addCart(@PathVariable("id") int id,@RequestBody Cart newCart) {
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO cart (customerid, itemname, salesDate, salesPerson, fee, quantity) VALUES (?, ?, ?, ?, ?, ?)")) {
            System.out.println(newCart.getSeller());
        	statement.setString(1, newCart.getCustomerID());
            statement.setString(2, newCart.getItemname());
            statement.setString(3, newCart.getSalesdate());
            statement.setString(4, newCart.getSeller());
            statement.setInt(5, newCart.getFee());
            statement.setInt(6, newCart.getQuantity());
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {	
                return ResponseEntity.ok("Customer's cart added successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
