package com.cafes.serviceImpl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.cafes.constants.CafeConstants;
import com.cafes.dao.ProductDao;
import com.cafes.jwt.JwtFilter;
import com.cafes.pojo.Category;
import com.cafes.pojo.Product;
import com.cafes.service.ProductService;
import com.cafes.utils.CafeUtils;
import com.cafes.wrapper.ProductWrapper;

@Service
public class ProductServiceImpl implements ProductService{

	@Autowired
	JwtFilter jwtFilter;
	
	@Autowired
	ProductDao productDao;
	
	@Override
	public ResponseEntity<String> addNewProduct(Map<String, String> requestMap) {
		try {
			if(jwtFilter.isAdmin()) {
				if(validateProductMap(requestMap,false)) {
					productDao.save(getProductFromMap(requestMap,false));
					return CafeUtils.getResponseEntity("Product Added Sucessfully",HttpStatus.OK);
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

	private Product getProductFromMap(Map<String, String> requestMap, boolean isAdd) {
		Category category = new Category();
		category.setId(Integer.parseInt(requestMap.get("categoryId")));
		
		Product product = new Product();
		if(isAdd) {
			product.setId(Integer.parseInt(requestMap.get("id")));
		}else {
			product.setStatus("true");
		}
		product.setCategory(category);
		product.setName(requestMap.get("name"));
		product.setDescription(requestMap.get("description"));
		product.setPrice(Integer.parseInt(requestMap.get("price")));
		
		return product;
	}

	//......................
	private boolean validateProductMap(Map<String, String> requestMap, boolean validateId) {
		if(requestMap.containsKey("name")) {
			if(requestMap.containsKey("id") && validateId){
				return true; // update record ke liye chalegi
			}else if(!validateId) {
				return true; // add record chalegi
			}
		}
		return false;
	}

	@Override
	public ResponseEntity<?> getAllProduct() {
		try {
			return new ResponseEntity<>(productDao.getAllProduct(),HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMTHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> updateProduct(Map<String, String> requestMap) {
		try {
			if(jwtFilter.isAdmin()) {
				if(validateProductMap(requestMap,true)) {
					Optional<Product> optional=productDao.findById(Integer.parseInt(requestMap.get("id")));
					if(!optional.isEmpty()) {
						Product product = getProductFromMap(requestMap, true);
						product.setStatus(optional.get().getStatus());
						productDao.save(product);
						return CafeUtils.getResponseEntity("Product Updated Successfully",HttpStatus.OK);
					}else {
						return CafeUtils.getResponseEntity("Product not found",HttpStatus.OK);
					}
				}else {
					return CafeUtils.getResponseEntity(CafeConstants.BAD_REQUEST,HttpStatus.BAD_REQUEST);
				}
			}else {
				return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMTHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> deleteProduct(Integer id) {
		try {
			if(jwtFilter.isAdmin()) {
				Optional<Product> optional = productDao.findById(id);
				if(!optional.isEmpty()) {
					productDao.deleteById(id);
					return CafeUtils.getResponseEntity("Product Deleted Successfully",HttpStatus.OK); 
				}
				return CafeUtils.getResponseEntity("Product does not exists",HttpStatus.OK);
			}else {
				return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMTHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> updateStatus(Map<String, String> requestMap) {
		try {
			if(jwtFilter.isAdmin()) {
				Optional<Product> optional = productDao.findById(Integer.parseInt(requestMap.get("id")));
				if(!optional.isEmpty()) {
					productDao.updateProductStatus(requestMap.get("status"),Integer.parseInt(requestMap.get("id")));
					return CafeUtils.getResponseEntity("Product Updated Sucessfully",HttpStatus.OK);
				}
				return CafeUtils.getResponseEntity("Product does not exists",HttpStatus.OK);
			}else {
				return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMTHING_WENT_WRONG,HttpStatus.BAD_REQUEST);
	}

	@Override
	public ResponseEntity<?> getByCategory(Integer id) {
		try {
			
			List<ProductWrapper> pwl = productDao.getByCategory(id);
			if(pwl!=null) {
				return  new ResponseEntity<>(pwl,HttpStatus.OK);
			}else {
				System.out.print("Chala Bhai");
				return CafeUtils.getResponseEntity("No Product exist For This Category Id",HttpStatus.OK);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMTHING_WENT_WRONG,HttpStatus.BAD_REQUEST);
	}

	@Override
	public ResponseEntity<?> getProductById(Integer id) {
		try {
			ProductWrapper pw = productDao.getProductById(id);
			if(pw!=null) {
				return new ResponseEntity<>(pw,HttpStatus.OK);
			}else {
				return CafeUtils.getResponseEntity("Product does not exist",HttpStatus.OK);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMTHING_WENT_WRONG,HttpStatus.BAD_REQUEST);
	}

}
