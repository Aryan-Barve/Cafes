package com.cafes.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cafes.pojo.User;
import com.cafes.wrapper.UserWrapper;
@Repository
public interface UserDao extends JpaRepository<User,Integer>{
	User findByEmailId(@Param("email") String email);
	
	@Query("SELECT new com.cafes.wrapper.UserWrapper(u.id, u.name, u.contactNumber, u.email, u.status) FROM User u WHERE u.role = 'user'")
    List<UserWrapper> getAllUsersByAsUserRole();
	
	@Query("SELECT u.email FROM User u WHERE u.role = 'admin'")
    List<String> getAllUsersByAsAdminRole();
    
    
	@Modifying
    @Transactional
    @Query("UPDATE User u SET u.status = :status WHERE u.id = :id")
	void updateStatus(String status, Integer id);
	
	
}
