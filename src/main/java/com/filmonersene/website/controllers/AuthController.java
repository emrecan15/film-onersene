package com.filmonersene.website.controllers;


import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.filmonersene.website.dtos.request.LoginRequest;
import com.filmonersene.website.repositories.UserRepository;
import com.filmonersene.website.security.JwtUtil;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
	
	private final AuthenticationManager authenticationManager;
	private final UserRepository userRepository;
	private final JwtUtil jwtUtil;
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response){
		try {
		Authentication authentication= authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword()));
		
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		
		String token = jwtUtil.generateToken(userDetails.getUsername());
		
		ResponseCookie jwtCookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(false) // HTTPS kullanıyorsan true 
                .path("/")
                .maxAge(24 * 60 * 60) // 1 gün
                .sameSite("Lax") 
                .build();
		
        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

		
		return ResponseEntity.ok(Map.of("username",userDetails.getUsername(),"roles",userDetails.getAuthorities()));
		
		}
		catch (BadCredentialsException e) {
			return ResponseEntity.status(401).body("Invalid Credentials");
		}
		catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Authentication Failed.");
		}
		
	}
	
	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletResponse response)
	{
		ResponseCookie deleteCookie = ResponseCookie.from("token","")
				.httpOnly(true)
				.secure(false)
				.path("/")
	            .maxAge(0) // Çerezi sil
	            .sameSite("Lax")
	            .build();
		response.setHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
		
		return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
	}
	
	@GetMapping("/status")
	public ResponseEntity<?> getStatus(HttpServletRequest request,HttpServletResponse response)
	{
		
		String jwt = jwtUtil.parseJwt(request);
		
		if (jwt == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token yok");
	    }
		
		try {
			String username = jwtUtil.extractEmail(jwt);
			
		if(!jwtUtil.isTokenValid(jwt, username)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token geçersiz");
		}
		
		return ResponseEntity.ok(Map.of(
	            "authenticated", true,
	            "username", username
	        ));
		
		/*
		if(jwt != null && jwtUtil.isTokenValid(jwt, jwtUtil.extractEmail(jwt)))
		{
			 String username = jwtUtil.extractEmail(jwt);
		        return ResponseEntity.ok(Map.of(
		            "authenticated", true,
		            "username", username
		        ));
		}
		return ResponseEntity.ok(Map.of("authenticated", false));
		*/
		
		}
		catch (ExpiredJwtException e) {
			// Süresi dolmuşsa çerezi sil
	        Cookie cookie = new Cookie("token", null);
	        cookie.setMaxAge(0);
	        cookie.setPath("/");
	        cookie.setHttpOnly(true);
	        response.addCookie(cookie);

	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token süresi dolmuş, yeniden giriş yap");
		}
		catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token hatalı: " + e.getMessage());
	    }
	}
	
}
