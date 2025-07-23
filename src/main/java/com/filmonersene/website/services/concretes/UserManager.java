package com.filmonersene.website.services.concretes;

import java.lang.invoke.ConstantBootstraps;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.filmonersene.website.dtos.request.ChangePasswordRequest;
import com.filmonersene.website.dtos.request.CreateUserRequest;
import com.filmonersene.website.dtos.request.LoginRequest;
import com.filmonersene.website.dtos.request.UpdateUserRequest;
import com.filmonersene.website.dtos.response.ChangePasswordResponse;
import com.filmonersene.website.dtos.response.CreateUserResponse;
import com.filmonersene.website.dtos.response.GetUserInfoResponse;
import com.filmonersene.website.dtos.response.LoginResponse;
import com.filmonersene.website.dtos.response.UpdateUserResponse;
import com.filmonersene.website.entities.Role;
import com.filmonersene.website.entities.Tag;
import com.filmonersene.website.entities.User;
import com.filmonersene.website.exceptions.TagNotFoundException;
import com.filmonersene.website.repositories.RoleRepository;
import com.filmonersene.website.repositories.TagRepository;
import com.filmonersene.website.repositories.UserRepository;
import com.filmonersene.website.services.abstracts.UserService;
import com.filmonersene.website.utils.Constants;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserManager implements UserService {
	
	
	private final UserRepository userRepository;
	private final TagRepository tagRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	
	
	//register
	@Override
	@Transactional
	public CreateUserResponse createUser(CreateUserRequest createUserRequest) {
		
		if(userRepository.existsByEmail(createUserRequest.getEmail()) || userRepository.existsByUsername(createUserRequest.getUsername()))
		{
			throw new IllegalStateException("Daha önce kayıt oldunuz. Lütfen giriş yapın.");
		}
		
		User user = new User();
		user.setUsername(createUserRequest.getUsername());
		user.setName(capitalizeName(createUserRequest.getName()));
		user.setEmail(createUserRequest.getEmail());
		user.setSurname(capitalizeName(createUserRequest.getSurname()));
		user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
		
		Tag tag = tagRepository.findById(Constants.DEFAULT_TAG_ID)
				.orElseThrow(() -> new TagNotFoundException("Tag bulunamadı"));
		
		user.setTag(tag);
		
		Role role = roleRepository.findById(Constants.DEFAULT_ROLE_ID)
				.orElseThrow(() -> new RuntimeException("Rol bulunamadı"));
		user.setRole(role);
		userRepository.save(user);

		
		return new CreateUserResponse("Kayıt Başarılı");
	}


	@Override
	public GetUserInfoResponse getUserInfo(UserDetails userDetails) {
		// TODO Auto-generated method stub
		String userName = userDetails.getUsername();
		User user = userRepository.findByEmail(userName)
				.orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı."));
		
		return new GetUserInfoResponse(null,user.getName(),user.getSurname(),user.getUsername(),user.getEmail());
	}


	@Override
	@Transactional
	public UpdateUserResponse updateUser(String email,UpdateUserRequest updateUserRequest) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("Eşleşen kullanıcı bulunamadı."+email));
		if(!isOnlyLetters(updateUserRequest.getName()) || !isOnlyLetters(updateUserRequest.getSurname()))
		{
			throw new IllegalArgumentException("İsim ve soyisim sadece harf içermeli ve boşluk içermemeli.");
		}
		
		
		user.setName(capitalizeName(updateUserRequest.getName().trim()));
		user.setSurname(capitalizeName(updateUserRequest.getSurname().trim()));
		userRepository.save(user);

		
		return new UpdateUserResponse(user.getName(), user.getSurname());
	}
	
	private boolean isOnlyLetters(String input) {
	    return input != null && input.matches("^[A-Za-zÇçĞğİıÖöŞşÜü]+$");
	}
	
	private String capitalizeName(String input) {
	    if (input == null || input.isEmpty()) return input;
	    return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
	}


	@Override
	@Transactional
	public ChangePasswordResponse changePassword(UserDetails userDetails, ChangePasswordRequest changePasswordRequest) {
		String currentPasswordOnUserDetails = userDetails.getPassword(); // hashed password
	    String currentPasswordByRequest = changePasswordRequest.getCurrentPassword(); // plain password

	    if (!passwordEncoder.matches(currentPasswordByRequest, currentPasswordOnUserDetails)) {
	        return new ChangePasswordResponse("Mevcut şifre yanlış.");
	    }

	    User user = userRepository.findByEmail(userDetails.getUsername())
	            .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));

	    String newEncodedPassword = passwordEncoder.encode(changePasswordRequest.getNewPassword());
	    user.setPassword(newEncodedPassword);
	    
	    return new ChangePasswordResponse("Şifre başarıyla değiştirildi.");
		
	}



	
	
	

}
