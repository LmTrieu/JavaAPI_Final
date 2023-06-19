package com.example.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Collator;
import java.util.HashMap;
import java.util.*;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
//	{GET [/sales/sort/{id}]}: getSort(1:ACS / 0:DESC)
//	{GET [/sales/search], params [name]}: getCustomer(String)
//		Ex: http://localhost:8080/SalesSpringMVCRestful/sales/search?name=Đạt
//	{PUT [/sales/{id}]}: updateCustomer(int,Customer)
//	{PUT [/sales/cart/{id}]}: updateCart(int,Cart)
//	{DELETE [/sales/cart/{id}]}: deleteCart(int)
//	{DELETE [/sales/{id}]}: deleteCustomer(int)
//	{POST [/sales]}: addCustomer(Customer)
//	{POST [/sales/cart/{id}]: addCart(int,Cart)

    
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<Map<Integer,Object>> getCustomerList(){
//		try (Connection connection = DriverManager.getConnection(dbUrl,dbUsername,dbPassword);
//	             Statement statement = connection.createStatement();
//	             ResultSet resultSet = statement.executeQuery("SELECT * FROM customer")) {
//            Map<Integer, Object> customerlist = new HashMap<>();
//            while (resultSet.next()) {
//                int id = resultSet.getInt("customerid");
//                Map<String, Object> customerMap = new HashMap<>();
//                customerMap.put("customerid", id);
//                customerMap.put("name", resultSet.getString("name"));
//                customerMap.put("tel", resultSet.getInt("tel"));
//                customerlist.put(id,customerMap);
//            }
//            return ResponseEntity.ok(customerlist);
//            
//        } catch (SQLException e) {
//            e.printStackTrace();
//            System.out.println("help");
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
    	try (Connection connection = DriverManager.getConnection(dbUrl,dbUsername,dbPassword);
    			Statement statement = connection.createStatement();
    			Statement statement2 = connection.createStatement();
    			ResultSet rs = statement.executeQuery("SELECT * FROM customer")) {
    		Map<Integer, Object> customerlist = new HashMap<>();   
               	while (rs.next()) { 
               		int id = rs.getInt("customerid");  
               		Cart cart = new Cart(); 
               		Customer cust = new Customer(id,rs.getString("name"),rs.getInt("tel"),cart);
               		try(ResultSet rs2 = statement2.executeQuery("SELECT * FROM customer INNER JOIN cart on customer.customerid = cart.customerid WHERE customer.customerid = "+id+"")){
               			if(rs2.next()) {
               				String a = rs2.getString(5);
                   			cart.setCustomerID(rs2.getString(5));  
                   			cart.setItemname(rs2.getString(6));
                   			cart.setSalesdate(rs2.getDate(7).toString());
                   			cart.setSeller(rs2.getString(8));  
                   			cart.setFee(rs2.getInt(9));
                   			cart.setQuantity(rs2.getInt(10));
                   			cust.setCart(cart);
               			}
//               			cart = new Cart(rs2.getString(5),rs2.getString(6),rs2.getDate(7).toString(),rs2.getString(8),rs2.getInt(9),rs2.getInt(10));
               		}
               		customerlist.put(id,cust); 
               	}
               	return ResponseEntity.ok(customerlist);
    		}	
       	 catch (SQLException e) {
       		 e.printStackTrace();
       		 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
       	}
	}
	
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Customer> getCart(@PathVariable("id") int customerid) {
//    	try (Connection connection = DriverManager.getConnection(dbUrl,dbUsername,dbPassword);
//             PreparedStatement statement = connection.prepareStatement("SELECT * FROM customer INNER JOIN cart on customer.customerid = cart.customerid AND customer.customerid = ?")) {
//            statement.setInt(1, customerid);
//            try (ResultSet rs = statement.executeQuery()) {
//                if (rs.next()) {
//                    Cart cart = new Cart(rs.getString(5),rs.getString(6),rs.getDate(7).toString(),rs.getString(8),rs.getInt(9),rs.getInt(10));
//    				Customer cust = new Customer(rs.getInt(1),rs.getString(2),rs.getInt(3),cart);
//                    return ResponseEntity.ok(cust);
//                } else {
//                    return ResponseEntity.notFound().build(); 
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
    	try (Connection connection = DriverManager.getConnection(dbUrl,dbUsername,dbPassword);
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM customer WHERE customerid = ?")) {
               statement.setInt(1, customerid);
               try (ResultSet rs = statement.executeQuery()) {
                   if (rs.next()) {
                	   Customer cust = new Customer(rs.getInt(1),rs.getString(2),rs.getInt(3));
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
    
    @RequestMapping(value = "sort/{id}", method = RequestMethod.GET)
    public ResponseEntity<Map<Integer,Object>> getSort(@PathVariable("id") int sortKey) {
    	try (Connection connection = DriverManager.getConnection(dbUrl,dbUsername,dbPassword);
	             Statement statement = connection.createStatement();
    			Statement statement2 = connection.createStatement();
	             ResultSet resultSet = statement.executeQuery("SELECT * FROM customer")) {
           Map<Integer, Object> customerlist = new HashMap<>();
//           while (resultSet.next()) {
//               int id = resultSet.getInt("customerid");
//               Map<String, Object> customerMap = new HashMap<>();
//               customerMap.put("customerid", id);
//               customerMap.put("name", resultSet.getString("name"));
//               customerMap.put("tel", resultSet.getInt("tel"));
//               customerlist.put(id,customerMap);
//           }
           while (resultSet.next()) { 
          		int id = resultSet.getInt("customerid");  
          		Cart cart = new Cart();
          		Customer cust = new Customer(id,resultSet.getString("name"),resultSet.getInt("tel"),cart);
          		try(ResultSet rs2 = statement2.executeQuery("SELECT * FROM customer INNER JOIN cart on customer.customerid = cart.customerid WHERE customer.customerid = "+id+"")){
          			if(rs2.next()) { 
              			cart.setCustomerID(rs2.getString(5));  
              			cart.setItemname(rs2.getString(6)); 
              			cart.setSalesdate(rs2.getDate(7).toString());
              			cart.setSeller(rs2.getString(8));  
              			cart.setFee(rs2.getInt(9));
              			cart.setQuantity(rs2.getInt(10));
              			cust.setCart(cart);
          			}
//          			cart = new Cart(rs2.getString(5),rs2.getString(6),rs2.getDate(7).toString(),rs2.getString(8),rs2.getInt(9),rs2.getInt(10));
          		}
          		customerlist.put(id,cust); 
          	}	
           if(sortKey == 1) {
        	   Map<Integer, Object> sortedMapAsc = sortByComparator(customerlist, true);
        	   return ResponseEntity.ok(sortedMapAsc);
           }
           else if(sortKey == 0){
        	   Map<Integer, Object> sortedMapDesc = sortByComparator(customerlist, false);
        	   return ResponseEntity.ok(sortedMapDesc);
           }
           return ResponseEntity.badRequest().build();
       } catch (SQLException e) {
           e.printStackTrace();
           System.out.println("help");
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
       }
	}
    
    @RequestMapping(value = "/search",params = {"name"}, method = RequestMethod.GET)
    public ResponseEntity<Customer> getCustomer(@RequestParam("name") String customername) {
    	try (Connection connection = DriverManager.getConnection(dbUrl,dbUsername,dbPassword);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM customer INNER JOIN cart on customer.customerid = cart.customerid AND customer.name = ?")) {
            statement.setString(1, customername);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Cart cart = new Cart(rs.getString(5),rs.getString(6),rs.getDate(7).toString(),rs.getString(8),rs.getInt(9),rs.getInt(10));
    				Customer cust = new Customer(rs.getInt(1),rs.getString(2),rs.getInt(3),cart);
                    return ResponseEntity.ok(cust);
                }else{
                	PreparedStatement statement2 = connection.prepareStatement("SELECT * FROM customer WHERE customer.name = ?");
                	statement2.setString(1, customername);
                	ResultSet rs2 = statement2.executeQuery();
            		if (rs2.next()) {
            			Customer cust2 = new Customer(rs2.getInt(1),rs2.getString(2),rs2.getInt(3),null);
                		return ResponseEntity.ok(cust2);
            		}
            		return ResponseEntity.notFound().build();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    private static Map<Integer, Object> sortByComparator(Map<Integer, Object> unsortMap, final boolean order)
    {
        List<Entry<Integer, Object>> list = new LinkedList<Entry<Integer, Object>>(unsortMap.entrySet());

        // Sorting the list based on values
        Locale vietnameseLocale = new Locale("vi", "VN");
        final Collator collator = Collator.getInstance(vietnameseLocale);
        Collections.sort(list, new Comparator<Map.Entry<Integer, Object>>() {
            public int compare(Map.Entry<Integer, Object> e1, Map.Entry<Integer, Object> e2) {
                
                Customer a2 = (Customer) e1.getValue();
                Customer b2 = (Customer) e2.getValue();
                
                String name1 = a2.getName();
                String name2 = b2.getName();
                
                if (order) {
                    return collator.compare(name1, name2);
                } else {
                    return collator.compare(name2, name1);
                } 
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<Integer, Object> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<Integer, Object> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }


        return sortedMap;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<String> updateCustomer(@PathVariable("id") int id, @RequestBody Customer customerupt) {
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement statement = connection.prepareStatement("UPDATE customer SET name = ?, tel = ? WHERE customerid = ?")) {
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
    @RequestMapping(value = "/cart/{id}", method = RequestMethod.PUT)
    public ResponseEntity<String> updateCart(@PathVariable("id") int id, @RequestBody Cart cartupt) {
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement statement = connection.prepareStatement("UPDATE cart SET "
             		+ "itemname = ?,"
             		+ "salesDate = ?, "
             		+ "salesPerson = ?, "
             		+ "fee = ?, "
             		+ "quantity = ? WHERE (customerid = ?);")) {
            statement.setString(1, cartupt.getItemname());
            statement.setString(2, cartupt.getSalesdate());
            statement.setString(3, cartupt.getSeller());
            statement.setInt(4, cartupt.getFee());
            statement.setInt(5, cartupt.getQuantity());
            statement.setInt(6, id);
            
            int rowsAffected = statement.executeUpdate(); 
            if (rowsAffected > 0) {
                return ResponseEntity.ok("Cart added successfully.");
            } else {
            	addCart(id, new Cart());
            	try {
                	updateCart(id,cartupt); 
				} catch (Exception e) {  
					return ResponseEntity.notFound().build();
				}
            	return ResponseEntity.ok("Cart updated successfully.");
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
   
    @RequestMapping(method = RequestMethod.POST)
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
    @RequestMapping(value="/cart/{id}",method = RequestMethod.POST)
    public ResponseEntity<String> addCart(@PathVariable("id") int id,@RequestBody Cart newCart) {
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO cart (customerid, itemname, salesDate, salesPerson, fee, quantity) VALUES (?, ?, ?, ?, ?, ?)")) {
        	statement.setString(1, Integer.toString(id));
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
