package exceptions;

public class PortfolioNotFoundException extends Exception {
    public PortfolioNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
