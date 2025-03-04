package farming.accounting.dto.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(HttpStatus.CONFLICT)
public class PasswordValidException extends RuntimeException{
	
	public PasswordValidException(String password) {
		super("Password " + password + "is not valid");
	}

}
