package com.boot.gateway.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@ToString
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

	private long id;

	@Size(min = 3, message = "Min First name size is 3 characters!")
	@Size(max = 30, message = "Max First name size is 30 characters!")
	private String firstName;

	@Size(min = 3, message = "Min Last name size is 3 characters!")
	@Size(max = 30, message = "Max Last name size is 30 characters!")
	private String lastName;

	@Size(min = 8, message = "Min Password size is 8 characters!")
	@Size(max = 30, message = "Max Password size is 30 characters!")
	private String password;

	@Pattern(regexp="^(?=[07]{2})(?=[0-9]{10}).*", message = "Invalid Phone Number!")
	private String phoneNumber;

	@Email(regexp="^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "Invalid Email!")
	private String email;

	private String role;

	private boolean isActivated;

}