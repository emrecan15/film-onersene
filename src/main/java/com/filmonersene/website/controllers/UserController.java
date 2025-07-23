package com.filmonersene.website.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.filmonersene.website.dtos.request.ChangePasswordRequest;
import com.filmonersene.website.dtos.request.CreateUserRequest;
import com.filmonersene.website.dtos.request.UpdateUserRequest;
import com.filmonersene.website.dtos.response.ChangePasswordResponse;
import com.filmonersene.website.dtos.response.CreateUserResponse;
import com.filmonersene.website.dtos.response.GetUserInfoResponse;
import com.filmonersene.website.dtos.response.UpdateUserResponse;
import com.filmonersene.website.services.abstracts.UserService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

	private final UserService userService;
	
	@PostMapping("/register")
	public ResponseEntity<CreateUserResponse> createUser(@RequestBody @Valid CreateUserRequest createUserRequest)
	{
		return ResponseEntity.ok(userService.createUser(createUserRequest));
	}
	
	@PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
	@GetMapping("/userinfo")
	public ResponseEntity<GetUserInfoResponse> getUserInfo(@AuthenticationPrincipal UserDetails userDetails){
		System.out.println("Roller: " + userDetails.getAuthorities());
		return ResponseEntity.ok(userService.getUserInfo(userDetails));
	}
	
	@PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
	@PostMapping("/update")
	public ResponseEntity<UpdateUserResponse> updateUser(@AuthenticationPrincipal UserDetails userDetails ,@RequestBody @Valid UpdateUserRequest userRequest){
		String username = userDetails.getUsername();
		return ResponseEntity.ok(userService.updateUser(username, userRequest));
	}
	
	@PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
	@PostMapping("/change-password")
	public ResponseEntity<ChangePasswordResponse> changePassword(@AuthenticationPrincipal UserDetails userDetails, @RequestBody @Valid ChangePasswordRequest changePasswordRequest)
	{
		return ResponseEntity.ok(userService.changePassword(userDetails, changePasswordRequest));
	}
	
	
	
	
	
	
}
