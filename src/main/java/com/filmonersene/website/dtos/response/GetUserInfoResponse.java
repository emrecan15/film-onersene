package com.filmonersene.website.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetUserInfoResponse {
	private String photoUrl;
	private String name;
	private String surname;
	private String userName;
	private String email;
	
}
