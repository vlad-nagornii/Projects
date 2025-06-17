package hva.exceptions;

import java.io.Serial;

public class UnknownEmployeeException extends Exception {

    /** Class serial number. */
    @Serial
    private static final long serialVersionUID = 202407081733L;

    private final String ID_EMPLOYEE;

    public UnknownEmployeeException(String idEmployee) {
        ID_EMPLOYEE = idEmployee;
    }

    public String getId() {
        return ID_EMPLOYEE;
    }
    
}
