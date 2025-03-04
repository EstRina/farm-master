package farming.accounting.service;

import java.time.LocalDateTime;

import farming.accounting.dto.UserRequestDto;
import farming.accounting.dto.UserResponseDto;
import farming.accounting.dto.UserType;

public interface IAccountingManagement {

	UserResponseDto registration(UserRequestDto userDto, UserType userType);
	UserResponseDto removeUser(String login);
	UserResponseDto getUser(String login);
	UserResponseDto editUser(UserResponseDto user, String login);
	
	boolean updatePassword(String login, String password);
	boolean revokeAccount(String login);
	boolean activateAccount(String login);
	
	String getPasswordHash(String login);
	LocalDateTime getActivationDate(String login);
	UserType getUserType(String login);
	
}
