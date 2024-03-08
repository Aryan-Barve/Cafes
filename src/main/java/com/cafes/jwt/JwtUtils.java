package com.cafes.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtUtils {
	
	private String secret = "AryanBarve";

	// method 1
	public String extractUserName(String token) {
	    return extractClaims(token, Claims::getSubject);
	}

	// method 2
	public Date extractExpiration(String token) {
	    return extractClaims(token, Claims::getExpiration);
	}

	// method 3
	public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
	    final Claims claims = extractAllClaims(token);
	    return claimsResolver.apply(claims);
	}

	// method 4
	public Claims extractAllClaims(String token) {
	    return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}

	// method 5
	private Boolean isTokenExpired(String token) {
	    return extractExpiration(token).before(new Date());
	}

	// method 6
	public Boolean validateToken(String token, UserDetails userDetails) {
	    final String userName = extractUserName(token);
	    return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	// method 7
	public String generateToken(String userName, String role) {
	    Map<String, Object> claims = new HashMap<>();
	    claims.put("role", role);
	    return createToken(claims, userName);
	}

	// method 8
	private String createToken(Map<String, Object> claims, String subject) {

	    return Jwts.builder()
	            .setClaims(claims)
	            .setSubject(subject)
	            .setIssuedAt(new Date(System.currentTimeMillis()))
	            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
	            .signWith(SignatureAlgorithm.HS256, secret).compact();
	}


}
