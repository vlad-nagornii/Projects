package hva.exceptions; 

import java.io.Serial;

public class VaccineNotValidForSpeciesException extends Exception {

    /** Class serial number. */
    @Serial
    private static final long serialVersionUID = 202407081733L;

    private final String ID_VACCINE;
    private final String ID_ANIMAL;

    public VaccineNotValidForSpeciesException(String idVaccine, String idAnimal) {
        ID_VACCINE = idVaccine;
        ID_ANIMAL= idAnimal;
    }

    public String getIdVaccine() {
        return ID_VACCINE;
    }

    public String getIdAnimal() {
        return ID_ANIMAL;
    }
}
