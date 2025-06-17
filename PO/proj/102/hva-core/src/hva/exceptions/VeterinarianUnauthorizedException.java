package hva.exceptions;

import java.io.Serial;

public class VeterinarianUnauthorizedException extends Exception {
        
    /** Class serial number. */
    @Serial
    private static final long serialVersionUID = 202407081733L;

    private final String ID_VET;
    private final String ID_SPECIES;

    public VeterinarianUnauthorizedException(String idVet, String idSpecies) {
        ID_VET = idVet;
        ID_SPECIES = idSpecies;
    }

    public String getIdVet() {
        return ID_VET;
    }

    public String getIdSpecies() {
        return ID_SPECIES;
        }
}
