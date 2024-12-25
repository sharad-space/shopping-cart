package com.ecommerce.model;

import java.util.Date;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;


@Entity
public class ProductOrder {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String orderId;
	
	private Date orderDate;
	
	@ManyToOne
	private Product product;
	
	private Double price;
	
	private Integer quantity;
	
	@ManyToOne
	private UserDts userDts;
	
	private String status;
	
	private String paymentType;
	
	@OneToOne(cascade = CascadeType.ALL)
	private OrderAdress orderAdress;

	public ProductOrder() {
		super();
		
	}

	public ProductOrder(Integer id, String orderId, Date orderDate, Double price, Integer quantity, UserDts userDts,
			String status, String paymentType,Product product,OrderAdress orderAdress) {
		super();
		this.id = id;
		this.orderId = orderId;
		this.orderDate = orderDate;
		this.price = price;
		this.quantity = quantity;
		this.userDts = userDts;
		this.status = status;
		this.paymentType = paymentType;
		this.product=product;
		this.orderAdress=orderAdress;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public UserDts getUserDts() {
		return userDts;
	}

	public void setUserDts(UserDts userDts) {
		this.userDts = userDts;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public OrderAdress getOrderAdress() {
		return orderAdress;
	}

	public void setOrderAdress(OrderAdress orderAdress) {
		this.orderAdress = orderAdress;
	}
	
	
	
	
	
	
	

}
