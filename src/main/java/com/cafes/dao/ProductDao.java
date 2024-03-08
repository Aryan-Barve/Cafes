package com.cafes.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.cafes.pojo.Product;
import com.cafes.wrapper.ProductWrapper;

public interface ProductDao extends JpaRepository<Product,Integer>{
	@Query("SELECT new com.cafes.wrapper.ProductWrapper(p.id, p.name, p.description, p.price, p.status,p.category.id,p.category.name) FROM Product p")
	List<ProductWrapper> getAllProduct();
	
	@Modifying
    @Transactional
    @Query("UPDATE Product p SET p.status = :status WHERE p.id = :id")
	void updateProductStatus(String status, Integer id);
	
	@Query("SELECT new com.cafes.wrapper.ProductWrapper(p.id, p.name, p.description, p.price, p.status,p.category.id,p.category.name) FROM Product p where p.category.id=:id and p.status='true' ")
	List<ProductWrapper>  getByCategory(Integer id);

	@Query("SELECT new com.cafes.wrapper.ProductWrapper(p.id, p.name, p.description, p.price) FROM Product p where p.id=:id and p.status='true' ")
	ProductWrapper getProductById(Integer id);
	

}
