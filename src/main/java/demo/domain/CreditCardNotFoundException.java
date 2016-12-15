package demo.domain;


public class CreditCardNotFoundException extends RuntimeException {

    public CreditCardNotFoundException() {
        super("Credit card not found");
    }
}
