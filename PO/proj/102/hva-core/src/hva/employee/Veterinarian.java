package hva.employee;

import hva.animal.Species;
import hva.exceptions.NoResponsibilityFoundException;
import hva.vaccine.Vaccination;
import hva.visitor.Visitor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Veterinarian extends Employee {

    /** Set of species that the veterinarian is responsible for. */
    private Map<String,Species> _responsibleSpecies = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    /** Vaccines given. */
    private List<Vaccination> _vaccinationHistory = new ArrayList<>();

    /**
     * Constructor.
     * 
     * @param idEmployee
     * @param name 
     */
    public Veterinarian(String idEmployee, String name) {
        super(idEmployee, name);
    }

    /**
     * Getter.
     * 
     * @return the map of the species the veterinarian is responsible for.
     */
    public Map<String,Species> getResponsibleSpecies() {
        return _responsibleSpecies;
    }

    /**
     * Check if the veterinarian is responsible for a species.
     * 
     * @param idSpecies
     * @return true if the veterinarian is responsible for the species.
     */
    public boolean checkResponsibility(String idSpecies) {
        return _responsibleSpecies.containsKey(idSpecies);
    }

    /**
     * Add a new responsibility to the veterinarian.
     * 
     * @param idSpecies
     */
    public void addResponsibility(Species species) {
        if(checkResponsibility(species.getId())) {
            return;
        }
        _responsibleSpecies.put(species.getId(), species);
    }

    /**
     * Remove a responsibility from the veterinarian.
     * 
     * @param idSpecies
     * @throws NoResponsibilityFoundException
     */
    public void removeResponsibility(String idSpecies)
            throws NoResponsibilityFoundException {
        if(!checkResponsibility(idSpecies)) {
            throw new NoResponsibilityFoundException(super.getId(), idSpecies);
        }
        _responsibleSpecies.remove(idSpecies);
    }

    /**
     * Adds a new vaccination to the veterinarian's history.
     * 
     * @param vaccination
     */
    public void addVaccination(Vaccination vaccination) {
        _vaccinationHistory.add(vaccination);
    }
    
    /**
     * Convert veterinarian to string.
     * "VET|Id|Name|Species1,Species2..."
     * 
     * @return String representation of the veterinarian.
     */ 
    public String toString() { 
        return "VET|" + getId() + "|" + getName() +  
         (_responsibleSpecies.isEmpty() ? "" : "|" 
          + String.join(",", _responsibleSpecies.keySet()));
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
