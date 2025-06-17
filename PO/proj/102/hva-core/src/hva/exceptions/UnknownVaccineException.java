package hva.exceptions;

import java.io.Serial;

public class UnknownVaccineException extends Exception {
    
    /** Class serial number. */
    @Serial
    private static final long serialVersionUID = 202407081733L;   
     
    private final String ID_VACCINE;

    public UnknownVaccineException(String idVaccine) {
        ID_VACCINE = idVaccine;
    }

    public String getId() {
        return ID_VACCINE;
    }
    
}
