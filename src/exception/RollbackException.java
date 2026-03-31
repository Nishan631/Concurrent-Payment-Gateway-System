package exception;

public class RollbackException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public RollbackException(String message) {
        super(message);
    }

    public RollbackException(String message, Throwable cause) {
        super(message, cause);
    }
}