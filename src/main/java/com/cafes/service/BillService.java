package com.cafes.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

public interface BillService {

	ResponseEntity<String> generateReport(Map<String, Object> requestMap);

	ResponseEntity<?> getBills();

	ResponseEntity<String> deleteBill(Integer id);

	ResponseEntity<?> getPdf(Map<String, Object> requestMap);

}
