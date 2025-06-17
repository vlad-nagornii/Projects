package hva.exceptions;

import java.io.Serial;

public class NoResponsibilityFoundException extends Exception {

    /** Class serial number. */
    @Serial
    private static final long serialVersionUID = 202407081733L;

    private final String ID_EMPLOYEE;
    private final String ID_RESPONSIBILITY;

    public NoResponsibilityFoundException(String idEmployee, String idResponsibility) {
        ID_EMPLOYEE = idEmployee;
        ID_RESPONSIBILITY = idResponsibility;
    }

    public String getIdEmployee() {
        return ID_EMPLOYEE;
    }

    public String getIdResponsibility() {
        return ID_RESPONSIBILITY;
    }
    
}
