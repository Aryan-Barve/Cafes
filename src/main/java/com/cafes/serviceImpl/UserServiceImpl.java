package com.cafes.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.cafes.constants.CafeConstants;
import com.cafes.dao.UserDao;
import com.cafes.jwt.CustomUserDetailsService;
import com.cafes.jwt.JwtFilter;
import com.cafes.jwt.JwtUtils;
import com.cafes.pojo.User;
import com.cafes.service.UserService;
import com.cafes.utils.CafeUtils;
import com.cafes.utils.EmailUtils;
import com.cafes.wrapper.UserWrapper;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;
@Service
@Slf4j
public class UserServiceImpl implements UserService{
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	CustomUserDetailsService customUserDetailsService;
	
	@Autowired
	JwtUtils jwtUtils;
	
	@Autowired
	JwtFilter jwtFilter;
	
	@Autowired
	EmailUtils emailUtils;

	@Override
	public ResponseEntity<String> signUp(Map<String, String> requestMap) {
	    try {
	        if (validateSignUp(requestMap)) {
	            User user = userDao.findByEmailId(requestMap.get("email"));
	            if (Objects.isNull(user)) {
	                userDao.save(getUserFromMap(requestMap));
	                return CafeUtils.getResponseEntity("SignUp Successful", HttpStatus.OK);
	            } else {
	                return CafeUtils.getResponseEntity("Email Already Exists", HttpStatus.BAD_REQUEST);
	            }
	        } else {
	            return CafeUtils.getResponseEntity(CafeConstants.BAD_REQUEST, HttpStatus.BAD_REQUEST);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return CafeUtils.getResponseEntity(CafeConstants.SOMTHING_WENT_WRONG, HttpStatus.BAD_REQUEST);
	}

	private boolean validateSignUp(Map<String, String> requestMap) {
	    return requestMap.containsKey("name") && requestMap.containsKey("contactNumber")
	            && requestMap.containsKey("email") && requestMap.containsKey("password");
	}

	private User getUserFromMap(Map<String, String> requestMap) {
	    User user = new User();
	    user.setName(requestMap.get("name"));
	    user.setContactNumber(requestMap.get("contactNumber"));
	    user.setEmail(requestMap.get("email"));
	    user.setPassword(requestMap.get("password"));
	    user.setStatus("false");
	    user.setRole("user");
	    return user;
	}

	@Override
	public ResponseEntity<String> login(Map<String, String> requestMap) {
		try {
			Authentication auth= authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password"))
					);
			if(auth.isAuthenticated()) {
				if(customUserDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")) {
					return new ResponseEntity<String>(jwtUtils.generateToken(customUserDetailsService.getUserDetail().getEmail(), customUserDetailsService.getUserDetail().getRole()),HttpStatus.OK);
				}else {
					return new ResponseEntity<String>("Wiat for Admin aprroval",HttpStatus.BAD_REQUEST);
				}
			}
					
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<String>("Bad Credentials",HttpStatus.BAD_REQUEST);
		
	}

	@Override
	public ResponseEntity<List<UserWrapper>> getAllUsers() {
		try {
			if(jwtFilter.isAdmin()) {
				return new ResponseEntity<>(userDao.getAllUsersByAsUserRole(),HttpStatus.OK);
			}else {
				return new ResponseEntity<>(new ArrayList<>(),HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> update(Map<String, String> requestMap) {
		try {
			if(jwtFilter.isAdmin()) {
				Optional<User> optional= userDao.findById(Integer.parseInt(requestMap.get("id")));
				if(!optional.isEmpty()) {
					userDao.updateStatus(requestMap.get("status"),Integer.parseInt(requestMap.get("id")));
					sendMailToAllAdmin(requestMap.get("status"),optional.get().getEmail(),userDao.getAllUsersByAsAdminRole());
					return CafeUtils.getResponseEntity("User Status Has Updated Successfully",HttpStatus.OK);
				}else {
					return CafeUtils.getResponseEntity("User Id does not exixts",HttpStatus.OK);
				}
			}else {
				return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMTHING_WENT_WRONG, HttpStatus.BAD_REQUEST);
	}

	private void sendMailToAllAdmin(String status, String user, List<String> allAdmin) {
		allAdmin.remove(jwtFilter.getCurrentUser());
		if(status!=null && status.equalsIgnoreCase("true")) {
			emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(),"Account Approved/Enabled","USER:- "+user+"\n is approved by \nADMIN:- "+jwtFilter.getCurrentUser(), allAdmin);
		}else {
			emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(),"Account Disabled","USER:- "+user+"\n is Disabled by \nADMIN:- "+jwtFilter.getCurrentUser(), allAdmin);
		}
		
	}

	@Override
	public ResponseEntity<String> checkToken() {
		
		return CafeUtils.getResponseEntity("true",HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
		try {
			User user = userDao.findByEmailId(jwtFilter.getCurrentUser());
			if(!user.equals(null)) {
				if(user.getPassword().equals(requestMap.get("oldPassword"))) {
					user.setPassword(requestMap.get("newPassword"));
					userDao.save(user);
					return CafeUtils.getResponseEntity("Your Password Updated Sucessfully.",HttpStatus.OK);
				}
				return CafeUtils.getResponseEntity("Incorrect Old Password.",HttpStatus.BAD_REQUEST); 
			}
			return CafeUtils.getResponseEntity(CafeConstants.SOMTHING_WENT_WRONG, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMTHING_WENT_WRONG, HttpStatus.BAD_REQUEST);
	}

	@Override
	public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
		try {
			User user = userDao.findByEmailId(requestMap.get("email"));
			if(!Objects.isNull(user) && !Strings.isNullOrEmpty(user.getEmail())) {
				emailUtils.forgotMail(user.getEmail(),"Credentials By Cafe Management System",user.getPassword());
				return CafeUtils.getResponseEntity("We Have Sent You An Email Check Your Mail For Credentials.",HttpStatus.OK);
			}
			return CafeUtils.getResponseEntity("Check Your Mail For Crdentials.",HttpStatus.OK);
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMTHING_WENT_WRONG, HttpStatus.BAD_REQUEST);
	}
   

}
