package hs.representation;

@SuppressWarnings("serial")
public class IllegalMoveException extends RuntimeException {

	public IllegalMoveException() {
		super();
	}

	public IllegalMoveException(String message) {
		super(message);
	}

	public IllegalMoveException(Throwable cause) {
		super(cause);
	}

	public IllegalMoveException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalMoveException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
