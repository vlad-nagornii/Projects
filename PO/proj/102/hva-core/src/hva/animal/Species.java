package hva.animal;

import java.io.Serial;
import java.io.Serializable;

import java.util.Map;
import java.util.HashMap;

import hva.employee.Veterinarian;

public class Species implements Serializable {

    /** Class serial number. */
    @Serial
    private static final long serialVersionUID = 202407081733L;

    /** Specie's Id. */
    private String _ID_SPECIES;

    /** Specie's name. */
    private String _name;

    /** Map of animals of this specie */
    private Map<String, Animal> _animals = new HashMap<>();

    /** Map of veterinarians responsible for this specie */
    private Map<String, Veterinarian> _responsibleVets = new HashMap<>();


    /**
     * Constructor.
     * 
     * @param idSpecies
     * @param name
     */
    public Species(String idSpecies, String name){
        _ID_SPECIES = idSpecies;
        _name = name;
    }

    /**
     * Getter.
     * 
     * @return specie's Id.
     */
    public String getId(){
        return _ID_SPECIES;
    }

    /**
     * Getter.
     * 
     * @return specie's name.
     */
    public String getName(){
        return _name;
    }

    /**
     * Getter.
     * 
     * @return map of animals of this specie.
     */
    public Map<String, Animal> getAnimals(){
        return _animals;
    }

    /**
     * Adds a new animal to the list of animals of this specie.
     * 
     * @param animal to be added.
     */
    public void addAnimal(Animal animal){
        _animals.put(animal.getId(), animal);
    }

    public void addResponsibleVet(Veterinarian vet){
        _responsibleVets.put(vet.getId(), vet);
    }

    /**
     * Returns the number of Veterinarians responsible for this specie.
     * 
     * @return number of Veterinarians responsible for this specie.
     */
    public int countResponsibleVets(){
        return _responsibleVets.size();
    }
}
