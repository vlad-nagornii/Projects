package hva.exceptions;

import java.io.Serial;

public class DuplicateSpeciesNameException extends Exception{
    
    /** Class serial number. */
    @Serial
    private static final long serialVersionUID = 202407081733L;

    private final String _name;

    public DuplicateSpeciesNameException(String name) {
        _name = name;
    }

    public String getName() {
        return _name;
    }
}
