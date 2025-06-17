package hva.employee;

import hva.visitor.Visitable;
import hva.visitor.Visitor;
import java.io.Serializable;

public abstract class Employee implements Visitable, Serializable{

    /** Employee's Id. */
    private final String _ID_EMPLOYEE;

    /** Employee's name. */
    private String _name;

    /**
     * Constructor.
     * 
     * @param idEmployee
     * @param name
     */
    public Employee(String idEmployee, String name) {
        _ID_EMPLOYEE = idEmployee;
        _name = name;
    }

    /**
     * Getter.
     * 
     * @return employee's Id.
     */
    public String getId() {
        return _ID_EMPLOYEE;
    }

    /**
     * Getter.
     * 
     * @return employee's name.
     */
    public String getName() {
        return _name;
    }

    @Override
    public abstract <T> T accept(Visitor<T> vistor);
}
