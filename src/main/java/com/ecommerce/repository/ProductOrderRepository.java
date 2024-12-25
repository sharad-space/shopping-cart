package com.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.ecommerce.model.ProductOrder;

public interface ProductOrderRepository extends JpaRepository<ProductOrder,Integer> {

//	List<ProductOrder> findByUserId(Integer userId);
//	 List<ProductOrder> findByUserId(@Param("userId") Integer userId);
	List<ProductOrder> findByUserDts_Id(Integer userId);

	ProductOrder findByOrderId(String orderId);

}
