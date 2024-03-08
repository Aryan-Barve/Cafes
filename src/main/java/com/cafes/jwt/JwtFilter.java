package com.cafes.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;

@Component
public class JwtFilter extends OncePerRequestFilter{

	
	@Autowired
	private JwtUtils jwtUtils;
	
	@Autowired
	private CustomUserDetailsService service;
	
	Claims claims=null;
	private String userName=null;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		if(request.getServletPath().matches("/user/login|/user/signup|/user/forgotpassword")) {
			filterChain.doFilter(request, response);
		}else {
			String authorizationHeader =request.getHeader("Authorization");
			String token = null;
			
			if(authorizationHeader!=null && authorizationHeader.startsWith("Bearer ")) {
				token=authorizationHeader.substring(7);
				userName=jwtUtils.extractUserName(token); 
				claims=jwtUtils.extractAllClaims(token);
			
			}
			
			if(userName!=null && SecurityContextHolder.getContext().getAuthentication()==null) {
				UserDetails userDetails= service.loadUserByUsername(userName);
				if(jwtUtils.validateToken(token, userDetails)) {
					UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(userDetails, null,userDetails.getAuthorities());
					upat.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(upat);
				}
			}
			filterChain.doFilter(request, response);
		}
	}
	
	public boolean isAdmin() {
		return "admin".equalsIgnoreCase((String) claims.get("role"));
	}
    
	public boolean isUser() {
		return "user".equalsIgnoreCase((String) claims.get("role"));
	}
	
	public String getCurrentUser() {
		return userName;
	}
}
