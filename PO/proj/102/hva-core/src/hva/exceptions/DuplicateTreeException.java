package hva.exceptions;

import java.io.Serial;

public class DuplicateTreeException extends Exception{
    @Serial
    private static final long serialVersionUID = 202407081733L;

    private final String ID;

    public DuplicateTreeException(String id) {
        ID = id;
    }

    public String getId() {
        return ID;
    }
}
