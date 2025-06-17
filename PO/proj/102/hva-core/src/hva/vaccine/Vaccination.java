package hva.vaccine;

import hva.animal.Animal;
import hva.employee.Veterinarian; 
import java.io.Serial;
import java.io.Serializable; 

public class Vaccination implements Serializable {

    /** Class serial number. */
    @Serial
    private static final long serialVersionUID = 202407081733L;

    private Vaccine _vaccine;
    private Animal _animal;
    private Veterinarian _veterinarian;
    private Damage _damage;

    /**
     * Constructor.
     * 
     * @param vaccine
     * @param animal
     * @param veterinarian
     */
    public Vaccination(Vaccine vaccine, Veterinarian veterinarian, Animal animal) {

        _vaccine = vaccine;
        _animal = animal;
        _veterinarian = veterinarian;
        _damage = vaccine.calculateDamage(veterinarian, animal);

        vaccine.addVaccination(this);
        animal.addVaccination(this);
        veterinarian.addVaccination(this);

    }

    /**
     * Constructor.
     * 
     * @return Veterinarian.
     */
    public Veterinarian getVeterinarian() {
        return _veterinarian;
    }

    /**
     * Getter for animal.
     * 
     * @return Animal.
     */
    public Animal getAnimal() {
        return _animal;
    }

    /**
     * Getter for damage.
     * 
     * @return Damage.
     */
    public Damage getDamage() {
        return _damage;
    }

    /**
     * Converts vacination to a string.
     * "REGISTO-VACINA|idVaccine|idVeterinarian|idSpecies"
     * 
     * @return String of the vaccination.
     */
    public String toString() {
        return "REGISTO-VACINA|" + _vaccine.getId() + "|"
        + _veterinarian.getId() + "|" + _animal.getSpecies().getId();
    }
}
