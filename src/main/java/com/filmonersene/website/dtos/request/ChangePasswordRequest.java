package com.filmonersene.website.dtos.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {
	
	private String currentPassword;
	
	@Pattern(
		    regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_])[A-Za-z\\d@$!%*?&_]{8,64}$",
		    message = "Şifre en az bir büyük harf, bir küçük harf, bir rakam ve bir özel karakter içermeli."
		)
	@Size(min=8,max=64,message = "Şifre 8 ile 64 karakter arasında olmalı.")
	private String newPassword;
}
