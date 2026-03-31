package exception;

public class TransactionFailedException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public TransactionFailedException(String message) {
        super(message);
    }

    public TransactionFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}