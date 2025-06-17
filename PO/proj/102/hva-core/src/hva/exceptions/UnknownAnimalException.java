package hva.exceptions;

import java.io.Serial;

public class UnknownAnimalException extends Exception {
    
    /** Class serial number. */
    @Serial
    private static final long serialVersionUID = 202407081733L;

    private final String ID_ANIMAL;

    public UnknownAnimalException(String idAnimal) {
        ID_ANIMAL = idAnimal;
    }

    public String getId() {
        return ID_ANIMAL;
    }
    
}
