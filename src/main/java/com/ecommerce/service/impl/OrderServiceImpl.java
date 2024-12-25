package com.ecommerce.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.model.Cart;
import com.ecommerce.model.OrderAdress;
import com.ecommerce.model.OrderRequest;
import com.ecommerce.model.ProductOrder;
import com.ecommerce.repository.CartRepo;
import com.ecommerce.repository.ProductOrderRepository;
import com.ecommerce.service.OrderService;
import com.ecommerce.utils.CommonUtils;
import com.ecommerce.utils.OrderStatus;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private ProductOrderRepository productOrderRepository;
	@Autowired
	private CartRepo cartRepo;
	@Autowired
	CommonUtils commonUtils;
	@Override
	public void saveOrder(Integer userId,OrderRequest orderRequest) throws Exception {
		 List<Cart> carts = cartRepo.findByUserId(userId);
		 for (Cart cart : carts) {
			 
			 ProductOrder order=new ProductOrder();
			 order.setOrderId(UUID.randomUUID().toString());
			 order.setOrderDate(new Date());
			 order.setProduct(cart.getProduct());
			 order.setPrice(cart.getProduct().getDiscountPrice());
			 order.setQuantity(cart.getQuantity());
			 order.setUserDts(cart.getUser());
			 order.setStatus(OrderStatus.IN_PROGRESS.getName());
			 order.setPaymentType(orderRequest.getPaymentType());
			 
			 OrderAdress address=new OrderAdress();
			 address.setFirstName(orderRequest.getFirstName());
			 address.setLastNmae(orderRequest.getLastName());
			 address.setEmail(orderRequest.getEmail());
			 address.setMobileNo(orderRequest.getMobileNo());
			 address.setAddress(orderRequest.getAddress());
			 address.setCity(orderRequest.getCity());
			 address.setState(orderRequest.getState());
			 address.setPincode(orderRequest.getPincode());
			 
			 
			 order.setOrderAdress(address);
			 
			 ProductOrder saveOrder = productOrderRepository.save(order);
			 
			 commonUtils.sendMailForProductOrder(saveOrder, "Success");
			 
			
		}
		 
		
	}
	@Override
	public List<ProductOrder> getOrderByUser(Integer userId) {
     List<ProductOrder> orders= productOrderRepository.findByUserDts_Id(userId);
		return orders;
	}
	@Override
	public ProductOrder updateOrderStatus(Integer id, String status) {
	  Optional<ProductOrder> findbyId = productOrderRepository.findById(id);
	  if (findbyId.isPresent()) {
		ProductOrder productOrder = findbyId.get();
		productOrder.setStatus(status);
		ProductOrder updateOrder = productOrderRepository.save(productOrder);
		return updateOrder;
	}
		return null;
	}
	@Override
	public List<ProductOrder> getAllOrder() {
		List<ProductOrder> all = productOrderRepository.findAll();
		return all;
	}
	@Override
	public ProductOrder getOrderByOrderId(String orderId) {
		ProductOrder byOrderId = productOrderRepository.findByOrderId(orderId);
		return byOrderId;
	}

}
