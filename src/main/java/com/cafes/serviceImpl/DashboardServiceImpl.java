package com.cafes.serviceImpl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.cafes.dao.BillDao;
import com.cafes.dao.CategoryDao;
import com.cafes.dao.ProductDao;
import com.cafes.service.DashboardService;
@Service
public class DashboardServiceImpl implements DashboardService{
	@Autowired
	ProductDao productDao;
	
	@Autowired
	CategoryDao categoryDao;
	
	@Autowired
	BillDao billDao;
	
	@Override
	public ResponseEntity<Map<String, Object>> getCount() {
		Map<String, Object> mapCount = new HashMap<>();
		mapCount.put("product", productDao.count());
		mapCount.put("category",categoryDao.count());
		mapCount.put("bill",billDao.count());
		
		return new ResponseEntity<Map<String,Object>>(mapCount,HttpStatus.OK);
	}

}
