package farming.accounting.dto.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(HttpStatus.CONFLICT)
public class AccountActivateException extends RuntimeException {

	public AccountActivateException(String login) {
		super("Account with login " + login + "is already active");
	}
}
