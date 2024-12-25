package com.ecommerce.service;

import java.util.List;

import com.ecommerce.model.OrderRequest;
import com.ecommerce.model.ProductOrder;


public interface OrderService {
	
	public void saveOrder(Integer userId,OrderRequest orderRequest) throws Exception;
	
	public List<ProductOrder> getOrderByUser(Integer userId);
	
	public ProductOrder updateOrderStatus(Integer id, String status);
	
	public List<ProductOrder> getAllOrder();
	
	public ProductOrder getOrderByOrderId(String orderId);

}
