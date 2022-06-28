package exceptions;

public class PortfolioExistsException extends Exception {
    public PortfolioExistsException(String errorMessage) {
        super(errorMessage);
    }
}
