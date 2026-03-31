package exception;

public class WalletBlockedException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public WalletBlockedException(String message) {
        super(message);
    }
}