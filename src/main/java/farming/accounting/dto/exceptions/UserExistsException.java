package farming.accounting.dto.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(HttpStatus.CONFLICT)
public class UserExistsException extends RuntimeException{
	
	public UserExistsException(String login) {
		super("User " + login + "already exists");
	}

}
