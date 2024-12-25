package com.ecommerce.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.ecommerce.model.Cart;
import com.ecommerce.model.Product;
import com.ecommerce.model.UserDts;
import com.ecommerce.repository.CartRepo;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepo;
import com.ecommerce.service.CartService;


@Service
public class CartServiceImpl implements CartService {
	
	@Autowired
	private CartRepo cartRepo;
    
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private UserRepo userRepo;
	
	@Override
	public Cart saveCart(Integer productId, Integer userId) {
		UserDts userDts = userRepo.findById(userId).get();
		Product product = productRepository.findById(productId).get();
		Cart cartStatus = cartRepo.findByProductIdAndUserId(productId, userId);
		
		Cart cart=null;
		if(ObjectUtils.isEmpty(cartStatus)) {
			cart=new Cart();
			cart.setProduct(product);
			cart.setUser(userDts);
			cart.setQuantity(1);
			cart.setTotalPrice(1*product.getDiscountPrice());
		}else {
			cart=cartStatus;
			cart.setQuantity(cart.getQuantity()+1);
			cart.setTotalPrice(cart.getQuantity()*cart.getProduct().getDiscountPrice());
		}
		
		Cart saveCart = cartRepo.save(cart);
		return saveCart;
	}

	@Override
	public List<Cart> getCartsByUser(Integer userId) {
		
		List<Cart> carts= cartRepo.findByUserId(userId);
		List<Cart> updateCarts=new ArrayList<Cart>();
		Double totalOrderedPrice=0.0;
		for (Cart cart : carts) {
		Double	totalPrice=(cart.getProduct().getDiscountPrice()*cart.getQuantity());
		cart.setTotalPrice(totalPrice);
		totalOrderedPrice+=totalPrice;
		cart.setTotalOrderedAmount(totalOrderedPrice);
		updateCarts.add(cart);
		
		}
		
		return updateCarts;
	}

	@Override
	public Integer getCountCart(Integer userId) {
		Integer countByUserId = cartRepo.countByUserId(userId);
		return countByUserId;
	}

	@Override
	public void updateQuantity(String sy, Integer cid) {
		 Cart cart = cartRepo.findById(cid).get();
		 Integer q;
		 if(sy.equalsIgnoreCase("de")) {
			q= cart.getQuantity()-1;
			if (q<=0) {
				cartRepo.delete(cart);
				
			}else {
				 cart.setQuantity(q);
				 cartRepo.save(cart);
			}
		 }else {
		q=cart.getQuantity()+1;
		 cart.setQuantity(q);
		 cartRepo.save(cart);
		}
		
		
		
	}

}
