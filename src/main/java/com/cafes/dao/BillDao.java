package com.cafes.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.cafes.pojo.Bill;

public interface BillDao extends JpaRepository<Bill,Integer>{
	 @Query("select b from Bill b order by b.id desc")
	List<Bill> getAllBills();
	 @Query("select b from Bill b where b.createdBy=:currentUser order by b.id desc")
	List<Bill> getBillByUserName(String currentUser);
	

}
