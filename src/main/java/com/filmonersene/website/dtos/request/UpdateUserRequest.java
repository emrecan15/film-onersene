package com.filmonersene.website.dtos.request;


import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {
	
	@Size(min=2,max = 30,message = "İsim en az 2 karakter, en fazla 30 karakter olabilir.")
	private String name;
	
	@Size(min=2,max = 30,message = "Soyisim en az 2 karakter, en fazla 30 karakter olabilir.")
	private String surname;
}
