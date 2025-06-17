package hva.employee;

import hva.exceptions.NoResponsibilityFoundException;
import hva.habitat.Habitat;
import hva.visitor.Visitor;
import java.util.Map;
import java.util.TreeMap;

public class Keeper extends Employee{

    /** Set of habitats that the keeper is responsible for. */
    private Map<String, Habitat> _responsibleHabitats = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);


    /**
     * Constructor.
     * 
     * @param id
     * @param name
     */
    public Keeper(String idEmployee, String name) {
        super(idEmployee, name);
    }

    /**
     * Getter.
     * 
     * @return the map of habitats that the keeper is responsible for.
     */
    public Map<String, Habitat> getResponsibleHabitats() {
        return _responsibleHabitats;
    }

    /**
     * Add a new responsibility to the keeper.
     * 
     * @param idHabitat
     */
    public void addResponsibility(Habitat habitat) {
        _responsibleHabitats.put(habitat.getId(), habitat);        
    }

    /**
     * Remove a responsibility from the keeper.
     * 
     * @param idHabitat
     * @throws NoResponsibilityFoundException
     */
    public void removeResponsibility(String idHabitat) 
            throws NoResponsibilityFoundException {
        if(!_responsibleHabitats.containsKey(idHabitat)) {
            throw new NoResponsibilityFoundException(super.getId(), idHabitat);
        }
        _responsibleHabitats.remove(idHabitat);
    }

    /**
     * Convert keeper to string.
     * "TRT|Id|Name|Habitat1,Habitat2,..."
     * 
     * @return String representation of the keeper.
     */
    public String toString() { 
        return "TRT|" + getId() + "|" + getName() + 
        (_responsibleHabitats.isEmpty() ? "" :  "|" + 
         String.join(",", _responsibleHabitats.keySet()));
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
