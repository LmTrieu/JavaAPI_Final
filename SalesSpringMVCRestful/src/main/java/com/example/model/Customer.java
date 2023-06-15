package com.example.model;

public class Customer {
	
	private int customerId;
	private String name;
	private int tel;
	Cart cart = null;
	
	public Customer(int customerId, String name, int tel) {
		this.customerId = customerId;
		this.name = name;
		this.tel = tel;
	}
	public Customer(String name, int tel) {
		this.name = name;
		this.tel = tel;
	}
	public Customer(int customerId,String name, int tel, Cart cart) {
		this.customerId = customerId;
		this.name = name;
		this.tel = tel;
		this.cart =cart;
	}
	public Customer(String name, int tel, Cart cart) {
		this.name = name;
		this.tel = tel;
		this.cart =cart;
	}
	/**
	 * @return the cart
	 */
	public Cart getCart() {
		return cart;
	}
	/**
	 * @param cart the cart to set
	 */
	public void setCart(Cart cart) {
		this.cart = cart;
	}
	/**
	 * @return the customerId
	 */
	public int getCustomerId() {
		return customerId;
	}

	/**
	 * @param customerId the customerId to set
	 */
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the tel
	 */
	public int getTel() {
		return tel;
	}

	/**
	 * @param tel the tel to set
	 */
	public void setTel(int tel) {
		this.tel = tel;
	}

//	public Customer get(int rowIndex) {
//		Customer temp = null;
//		temp.customerId = this.customerId;
//		temp.name = this.name;
//		temp.tel = this.tel;
//		return temp;
//	}
	
}
