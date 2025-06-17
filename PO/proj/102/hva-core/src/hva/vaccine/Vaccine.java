package hva.vaccine;

import hva.animal.Animal;
import hva.animal.Species;
import hva.employee.Veterinarian;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

 

public class Vaccine implements Serializable{

    /** Class serial number. */
    @Serial
    private static final long serialVersionUID = 202407081733L;

    /** vaccine's Id. */
    private final String _ID_VACCINE;

    /** vaccine's name. */
    private String _name;

    /** species that can be vaccinated with this vaccine. */
    private Map<String,Species> _targetSpecies = new HashMap<>();

    /** registry of vaccination (ordered by time of application). */
    private List<Vaccination> _vaccinationHistory = new ArrayList<>();

    /**
     * Constructor.
     * 
     * @param id
     * @param name
     */
    public Vaccine(String idVaccine, String name){
        _ID_VACCINE = idVaccine;
        _name = name;
    }

    /**
     * Getter for the id. 
     * 
     * @return vaccine's Id.
     */
    public String getId(){
        return _ID_VACCINE;
    }

    /**
     * Adds a new species to the list of species that can be vaccinated with
     * this vaccine.
     * 
     * @param species
     */
    public void addTargetSpecies(Species species){
        _targetSpecies.put(species.getId(), species);
    }

    /**
     * Checks if the species is a target species for this vaccine.
     * 
     * @param boolean true if the species is a target species.
     */
    public boolean isTargetSpecies(String idSpecies){
        return _targetSpecies.containsKey(idSpecies);
    }

    /**
     * Adds a vaccination to the registry of vaccinations of this vaccine.
     * 
     * @param vaccination
     */
    public void addVaccination(Vaccination vaccination){
        _vaccinationHistory.add(vaccination);
    }
    
    /**
     * Calculates the damage caused by the application of this vaccine to the animal.
     * 
     * @param veterinarian
     * @param animal
     * @return Damage caused to the animal. [NORMAL, ACIDENTE, CONFUSÃO or ERRO]
     */
    public Damage calculateDamage(Veterinarian veterinarian, Animal animal){
        if(isTargetSpecies(animal.getSpecies().getId())){
            return Damage.NORMAL;
        }
        int maxDamage = 0;
        int bufferDamage = 0;
        String nameSpecies = animal.getSpecies().getName();
        for(Species targeted : _targetSpecies.values()){
            String nameTargeted = targeted.getName();
            bufferDamage = biggestName(nameTargeted, nameSpecies)
                         - getSameChars(nameTargeted, nameSpecies);
            maxDamage = bufferDamage > maxDamage ? bufferDamage : maxDamage;
        }
        if (maxDamage == 0) {
            return Damage.CONFUSÃO;
        } else if (maxDamage <= 4) {
            return Damage.ACIDENTE;
        } else {
            return Damage.ERRO;
        }
    }

    /**
     * Returns the size of biggest name between two names.
     * 
     * @param idSpecies1
     * @param idSpecies2
     * @return String
     */
    public int biggestName(String name1, String name2) {
        int sizeName1 = name1.length();
        int sizeName2 = name2.length();
        return  sizeName1 > sizeName2 ? sizeName1 : sizeName2;
    }

    /**
     * Returns the number of characters shared between two names.
     * 
     * @param idSpecies1
     * @param idSpecies2
     * @return Int number of shared characters.
     */
    public int getSameChars(String idSpecies1, String idSpecies2) {
        Map<Character, Integer> map1 = new HashMap<>();
        Map<Character, Integer> map2 = new HashMap<>();
        int counter = 0;
        for (int i = 0; i < idSpecies1.length(); i++) {
            map1.put(idSpecies1.charAt(i), map1.getOrDefault(idSpecies1.charAt(i), 0) + 1);
        }
        for (int i = 0; i < idSpecies2.length(); i++) {
            map2.put(idSpecies2.charAt(i), map1.getOrDefault(idSpecies2.charAt(i), 0) + 1);
        }
        for (Character c1 : map1.keySet()) {
            if (map2.containsKey(c1)) {
                counter += Math.min(map1.get(c1), map2.get(c1));
            }
        }
        return counter;
    }


    /**
     * Convert vaccine to string.
     * "VACINA|IdVaccine|NameVaccine|NumberOfVaccinations|species1,species2,..."
     * 
     * @return String representation of the vaccine.
     */
    @Override
    public String toString(){
        return "VACINA|" + _ID_VACCINE + "|" + _name + "|" + _vaccinationHistory.size()
        + (_targetSpecies.isEmpty() ? "" : "|" + String.join(",", _targetSpecies.keySet()));
    }
}
