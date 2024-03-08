package com.cafes.jwt;

import java.util.ArrayList;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cafes.dao.UserDao;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService{

	@Autowired
	UserDao userDao;
	
	private com.cafes.pojo.User ud;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	
		ud=userDao.findByEmailId(username);
		if(!Objects.isNull(ud)) {
			return new User(ud.getEmail(),ud.getPassword(),new ArrayList<>());
		}else {
			throw new UsernameNotFoundException("User Not Found Here.");
		}
		
	}
	
	public com.cafes.pojo.User getUserDetail(){
		return ud;
	}

}
