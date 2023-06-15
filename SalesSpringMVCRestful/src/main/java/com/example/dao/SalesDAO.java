package com.example.dao;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.table.DefaultTableModel;
import com.example.model.Customer;

public interface SalesDAO {
	public List<Customer> search(String searchName);
	public void sortCustomerList(DefaultTableModel tableModel, AtomicBoolean isSorted);
	public List<Customer> getAllCustomers();
}
