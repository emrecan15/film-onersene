package com.filmonersene.website.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {
		
	
	@NotBlank(message = "Kullanıcı adı boş olamaz")
	@Size(min=3,max=15,message="Kullanıcı adı en az 3 karakter, en fazla 15 karakter olabilir.")
	private String username;
	
	@NotBlank(message = "Ad boş olamaz")
	@Size(min=2,max=30,message = "Ad en az 2 karakter, en fazla 30 karakter olabilir.")
	private String name;
	@NotBlank(message = "Soyisim boş olamaz")
	private String surname;
	
	@NotBlank(message = "Email boş olamaz")
    @Email(message = "Geçerli bir email giriniz")
	@Size(min=2,max=30,message = "Soyad en az 2 karakter, en fazla 30 karakter olabilir.")
	private String email;
	

    @NotBlank(message = "Şifre boş olamaz")
    @Size(min = 8,max=64, message = "Şifre en az 8 karakter olmalı")
	private String password;
	
	
}
