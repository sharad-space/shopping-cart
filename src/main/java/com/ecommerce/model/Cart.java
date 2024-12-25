package com.ecommerce.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;

@Entity
public class Cart {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
	private UserDts user;
	@ManyToOne
	private Product product;
	
	private Integer quantity;

	@Transient
	private Double totalPrice;
	
	@Transient
	private Double totalOrderedAmount;

	public Cart() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Cart(Integer id, UserDts user, Product product, Integer quantity, Double totalPrice,Double totalOrderedAmount) {
		super();
		this.id = id;
		this.user = user;
		this.product = product; 
		this.quantity = quantity;
		this.totalPrice = totalPrice;
		this.totalOrderedAmount=totalOrderedAmount;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public UserDts getUser() {
		return user;
	}

	public void setUser(UserDts user) {
		this.user = user;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public Double getTotalOrderedAmount() {
		return totalOrderedAmount;
	}

	public void setTotalOrderedAmount(Double totalOrderedAmount) {
		this.totalOrderedAmount = totalOrderedAmount;
	}
	
	
	
	
}
