package com.cafes.rest;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cafes.wrapper.ProductWrapper;

@RequestMapping(path="/product")
public interface ProductRest {

	@PostMapping(path="/add")
	public ResponseEntity<String> addNewProduct(@RequestBody Map<String,String> requestMap);
	
	@GetMapping(path="/get")
	public ResponseEntity<?> getAllProduct();
	
	@PostMapping(path="/update")
	public ResponseEntity<String> updateProduct(@RequestBody Map<String,String> requestMap);
	
	@DeleteMapping(path="/delete/{id}")
	public ResponseEntity<String> deleteProduct(@PathVariable Integer id); 
	
	@PutMapping(path="/updatestatus")
	public ResponseEntity<String> updateStatus(@RequestBody Map<String,String> requestMap);
	
	@GetMapping(path="/getByCategory/{id}")
	public ResponseEntity<?> getByCategory(@PathVariable Integer id);
	
	@GetMapping(path="/getById/{id}")
	public ResponseEntity<?> getProductById(@PathVariable Integer id);
	
	
	
}
