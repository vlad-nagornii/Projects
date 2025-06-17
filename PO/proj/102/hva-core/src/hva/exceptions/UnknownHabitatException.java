package hva.exceptions;

import java.io.Serial;

public class UnknownHabitatException extends Exception{

    /** Class serial number. */
    @Serial
    private static final long serialVersionUID = 202407081733L;

    private final String ID;

    public UnknownHabitatException(String id) {
        ID = id;
    }

    public String getId() {
        return ID;
    }
}
