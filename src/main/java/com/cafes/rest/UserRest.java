package com.cafes.rest;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cafes.wrapper.UserWrapper;

@RequestMapping(path="/user")
public interface UserRest {

	@PostMapping(path="/signup")
	public ResponseEntity<String> signUp(@RequestBody Map<String,String> requestMap);
	
	@PostMapping(path="/login")
	public ResponseEntity<String> login(@RequestBody Map<String,String> requestMap);
	
	@GetMapping(path="/hello")
	public ResponseEntity<String> hello();
	
	@GetMapping(path="/getAllUsers")
	public ResponseEntity<List<UserWrapper>> getAllUsers() ;
	
	@PutMapping(path="/update")
	public ResponseEntity<String> update(@RequestBody Map<String,String> requestMap);
	
	@GetMapping(path="/checkToken")
	public ResponseEntity<String> checkToken();
	
	@PostMapping(path="/changePassword")
	public ResponseEntity<String> changePassword(@RequestBody Map<String,String> requestMap);
	
	@PostMapping(path="/forgotpassword")
	public ResponseEntity<String> forgotPassword(@RequestBody Map<String,String> requestMap);
	
	

}
