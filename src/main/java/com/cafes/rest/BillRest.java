package com.cafes.rest;

import java.util.List;
import java.util.Map;

import org.apache.catalina.connector.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cafes.pojo.Bill;

@RequestMapping(path="/bill")
public interface BillRest {

	@PostMapping(path="/generateReport")
	ResponseEntity<String> generateReport(@RequestBody Map<String,Object> requestMap);
	
	@GetMapping(path="/getBills")
	ResponseEntity<?> getBills();
	
	@DeleteMapping(path="/deleteBill/{id}")
	ResponseEntity<String> deleteBill(@PathVariable Integer id);
	
	@PostMapping(path="/getPdf")
	ResponseEntity<?> getPdf(Map<String,Object> requestMap);
	
}
