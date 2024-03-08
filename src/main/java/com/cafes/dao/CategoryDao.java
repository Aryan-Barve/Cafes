package com.cafes.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cafes.pojo.Category;

public interface CategoryDao extends JpaRepository<Category,Integer>{

	List<Category> getAllCategory();
  
}
