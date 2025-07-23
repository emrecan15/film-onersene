package com.filmonersene.website.services.abstracts;

import org.springframework.security.core.userdetails.UserDetails;

import com.filmonersene.website.dtos.request.ChangePasswordRequest;
import com.filmonersene.website.dtos.request.CreateUserRequest;
import com.filmonersene.website.dtos.request.UpdateUserRequest;
import com.filmonersene.website.dtos.response.ChangePasswordResponse;
import com.filmonersene.website.dtos.response.CreateUserResponse;
import com.filmonersene.website.dtos.response.GetUserInfoResponse;
import com.filmonersene.website.dtos.response.UpdateUserResponse;


public interface UserService {
	CreateUserResponse createUser(CreateUserRequest createUserRequest);
	GetUserInfoResponse getUserInfo(UserDetails userDetails);
	UpdateUserResponse updateUser(String username,UpdateUserRequest updateUserRequest);
	ChangePasswordResponse changePassword(UserDetails userDetails,ChangePasswordRequest changePasswordRequest);
}
