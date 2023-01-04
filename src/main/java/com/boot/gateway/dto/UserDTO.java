package com.boot.gateway.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;


import java.util.List;

@Data
@ToString
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

	private long id;
	private String firstName;
	private String lastName;
	private String password;
	private String phoneNumber;
	private String email;
	private List<String> roles;
	private boolean isActivated;

}