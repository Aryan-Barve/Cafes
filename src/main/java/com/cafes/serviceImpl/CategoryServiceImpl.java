package com.cafes.serviceImpl;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.cafes.constants.CafeConstants;
import com.cafes.dao.CategoryDao;
import com.cafes.jwt.JwtFilter;
import com.cafes.pojo.Category;
import com.cafes.service.CategoryService;
import com.cafes.utils.CafeUtils;
import com.google.common.base.Strings;
@Service
public class CategoryServiceImpl implements CategoryService{

	@Autowired
	CategoryDao categoryDao;
	
	@Autowired
	JwtFilter jwtFilter;
	
	@Override
	public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
		try {
			if(jwtFilter.isAdmin()) {
				if(validateCategoryMap(requestMap,false)) {
					categoryDao.save(getcategoryFromMap(requestMap, false));
					return CafeUtils.getResponseEntity("Category Added Sucessfully",HttpStatus.OK);
				}
			}else {
				return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMTHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private boolean validateCategoryMap(Map<String, String> requestMap, boolean validateId) {
		if(requestMap.containsKey("name")) {
			if(requestMap.containsKey("id") && validateId){
				return true; // update record ke liye chalegi
			}else if(!validateId) {
				return true; // add record chalegi
			}
		}
		return false;
	}
	
	private Category getcategoryFromMap(Map<String, String> requestMap, boolean isAdd) {
		Category category = new Category();
		if(isAdd) {
			category.setId(Integer.parseInt(requestMap.get("id")));
		}
		category.setName(requestMap.get("name"));
		return category;
	}

	@Override
	public ResponseEntity<?> getAllCategory(String filterValue) {
		try {
			if(!Strings.isNullOrEmpty(filterValue) && filterValue.equalsIgnoreCase("true")) {
				return new ResponseEntity<>(categoryDao.getAllCategory(),HttpStatus.OK);
			}
			return new ResponseEntity<>(categoryDao.findAll(),HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMTHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
		try {
			if(jwtFilter.isAdmin()) {
				if(validateCategoryMap(requestMap, true)) {
					Optional optional= categoryDao.findById(Integer.parseInt(requestMap.get("id")));
					if(!optional.isEmpty()) {
						categoryDao.save(getcategoryFromMap(requestMap, true));
						return  CafeUtils.getResponseEntity("Category Updated Successfully.",HttpStatus.OK);
					}else {
						return CafeUtils.getResponseEntity("Category Id does not exist",HttpStatus.OK);
					}
				}
				return CafeUtils.getResponseEntity(CafeConstants.BAD_REQUEST,HttpStatus.BAD_REQUEST);
			}else {
				return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return CafeUtils.getResponseEntity(CafeConstants.SOMTHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
