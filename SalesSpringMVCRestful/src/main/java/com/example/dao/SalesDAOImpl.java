package com.example.dao;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.jdatepicker.JDateComponent; 

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
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

	public static List<Customer> search(String searchName) throws ClientProtocolException, IOException {
		
	    List<Customer> searchResults = new ArrayList<>();

	    HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet("http://localhost:8080/SalesSpringMVCRestful/sales/search?name="+searchName+"");
		
        HttpResponse response = httpClient.execute(request);

        HttpEntity responseEntity = response.getEntity();
        String responseBody = EntityUtils.toString(responseEntity);
        
//        Customer customerMap = new Gson().fromJson(responseBody, Customer.class);
        
        Type type = new TypeToken<Map<String, Customer>>() {}.getType();
        try {
        	Map<Integer, Customer> customerMap = new Gson().fromJson(responseBody, type);
        	searchResults = new ArrayList<>(customerMap.values());
		} catch (Exception e) {
			Customer customerMap = new Gson().fromJson(responseBody, Customer.class);
			searchResults.add(customerMap);
		}
        
	    if (searchResults.isEmpty()) {
	        JOptionPane.showMessageDialog(null, "Customer not found.", "Search Result", JOptionPane.ERROR_MESSAGE);
	    }

	    return searchResults;
	}

//	@Override
//	public List<Customer> getAllCustomers() {
//	    List<Customer> customerList = new ArrayList<>(customerlist);
//	    return customerList;
//	}

	public static List<Customer> updateCartDAO() throws ClientProtocolException, IOException {

		customerlist.clear();
		
		HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet("http://localhost:8080/SalesSpringMVCRestful/sales");
		
        HttpResponse response = httpClient.execute(request);

        HttpEntity responseEntity = response.getEntity();
        String responseBody = EntityUtils.toString(responseEntity);
        
        Type type = new TypeToken<Map<String, Customer>>() {}.getType();
        Map<String, Customer> customerMap = new Gson().fromJson(responseBody, type);

        customerlist = new ArrayList<>(customerMap.values());

		return customerlist;
	}	
	public static DefaultTableModel ModelPrep(JTable Table,AtomicBoolean isSorted) throws ClientProtocolException, IOException {
		if(isSorted.get()) {
			SalesFrame.customer = SalesDAOImpl.sortCustomerList(Table, isSorted);
		}
		else if(!isSorted.get()){
			SalesFrame.customer = SalesDAOImpl.sortCustomerList(Table, isSorted);
		}
		else {
			SalesFrame.customer = SalesDAOImpl.updateCartDAO();
		}
		DefaultTableModel tableModel = new DefaultTableModel(col, 0);
		for (Customer temp : customerlist) {
			Object[] t = {temp.getName(), temp.getTel()};
			tableModel.addRow(t);
		}
		return tableModel;
	}
	public static void UpdateSQL(JTable table) throws ClientProtocolException, IOException {
	    int selectedRowIndex = table.getSelectedRow();
	    Customer customer = SalesFrame.customer.get(selectedRowIndex);
	    
	    customer.setName((String)table.getValueAt(selectedRowIndex, 0));

	    customer.setTel(Integer.valueOf(table.getValueAt(selectedRowIndex, 1).toString()));

	    Cart cart = customer.getCart();
	    int id = customer.getCustomerId();
	    HttpClient httpClient = HttpClientBuilder.create().build();
	    
	    if (customer.getCustomerId() != 0) {
	    	// Cập nhật khách hàng đã tồn tại
	    	HttpPut putcust = new HttpPut("http://localhost:8080/SalesSpringMVCRestful/sales/"+id+"");
		    String RequestPutCust = "{ \"name\": \""+customer.getName()+
									"\", \"tel\": "+customer.getTel()+"}";
		    
		    StringEntity entitycust = new StringEntity(RequestPutCust,ContentType.create("application/json", "UTF-8"));
		    putcust.setEntity(entitycust); 
            
            HttpResponse responsecust = httpClient.execute(putcust);
            
	        // Cập nhật đơn hàng đã tồn tại
            HttpPut putcart = new HttpPut("http://localhost:8080/SalesSpringMVCRestful/sales/cart/"+id+"");

            // Create the JSON body
            String requestBody = "{" +
                    "    \"itemname\": \""+cart.getItemname()+"\"," +
                    "    \"salesdate\": \""+cart.getSalesdate()+"\"," +
                    "    \"fee\": "+cart.getFee()+"," +
                    "    \"quantity\": "+cart.getQuantity()+"," +
                    "    \"seller\": \""+cart.getSeller()+"\"" +
                    "}";
            StringEntity entitycart = new StringEntity(requestBody,ContentType.create("application/json", "UTF-8"));
            putcart.setEntity(entitycart); 
            
            HttpResponse responsecart = httpClient.execute(putcart);

            HttpEntity responseEntity = responsecart.getEntity();
	    } else {
	        // Thêm mới đơn hàng
            HttpPost postcart = new HttpPost("http://localhost:8080/SalesSpringMVCRestful/sales");

            // Create the JSON body
            String requestBody = "{ \"name\": \""+customer.getName()+
            					"\", \"tel\": "+customer.getTel()+"}";
            
            StringEntity entity = new StringEntity(requestBody,ContentType.create("application/json", "UTF-8"));
            postcart.setEntity(entity);
            
            HttpResponse response = httpClient.execute(postcart);

            HttpEntity responseEntity = response.getEntity();
            String responseBody = EntityUtils.toString(responseEntity);
	    }
	}
	public static void DeleteRow(JTable table) {
	    int i = table.getSelectedRow();
	    int id = SalesFrame.customer.get(i).getCustomerId();
    	HttpClient httpClient = HttpClientBuilder.create().build();
        HttpDelete postcust = new HttpDelete("http://localhost:8080/SalesSpringMVCRestful/sales/"+id+"");

		try {
			HttpResponse response = httpClient.execute(postcust);
			HttpEntity responseEntity = response.getEntity();
	        String responseBody = EntityUtils.toString(responseEntity);
		} catch (IOException e) {
			e.printStackTrace();
		}   
	}
	
	public static List<Customer> sortCustomerList(JTable table, AtomicBoolean isSorted) {
	    
		int sort;
		if(isSorted.get())
			sort = 1;
		else 
			sort = 0;
		
		customerlist.clear();
		
		HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet("http://localhost:8080/SalesSpringMVCRestful/sales/sort/"+sort+"");
        
		try {
			HttpResponse response = httpClient.execute(request);
			HttpEntity responseEntity = response.getEntity();
	        String responseBody = EntityUtils.toString(responseEntity);
	        Type type = new TypeToken<Map<String, Customer>>() {}.getType();
	        Map<String, Customer> customerMap = new Gson().fromJson(responseBody, type);

	        customerlist = new ArrayList<>(customerMap.values());
	        
		} catch (IOException e) {
			e.printStackTrace();
			
		}
		return customerlist; 
	}
	
	@Override
	public List<Customer> getAllCustomers() {
		// TODO Auto-generated method stub
		return null;
	}

}
