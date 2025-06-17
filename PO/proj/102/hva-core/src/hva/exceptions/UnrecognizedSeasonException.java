package hva.exceptions;

import java.io.Serial;

public class UnrecognizedSeasonException extends Exception {

    @Serial
    private static final long serialVersionUID = 202407081733L;

    /** Unrecognized season specification. */
    private final String _seasonSpecification;

    public UnrecognizedSeasonException(String seasonSpecification) {
        _seasonSpecification = seasonSpecification;
    }

    public UnrecognizedSeasonException(String seasonSpecification, Exception cause) {
        super(cause);
        _seasonSpecification = seasonSpecification;
    }

    public String getSeasonSpecification() {
        return _seasonSpecification;
    }
    
}
