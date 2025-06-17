package hva.animal;

import hva.habitat.Habitat;
import hva.vaccine.Vaccination;
import hva.visitor.Visitable;
import hva.visitor.Visitor;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Animal implements Visitable, Serializable{

    /** Class serial number. */
    @Serial
    private static final long serialVersionUID = 202407081733L;

    /** Animal's Id. */
    private final String _ID_ANIMAL;

    /** Animal's name. */
    private String _name;

    /** Species of the animal. */
    private Species _species;

    /** Current habitat. */
    private Habitat _habitat;

    /** History of vaccines damage to the animal's health. */
    private List<Vaccination> _healthHistory = new ArrayList<>();


    /**
     * Constructor.
     * 
     * @param idAnimal
     * @param name
     * @param idSpecies
     * @param idHabitat
     */
    public Animal(String idAnimal, String name, Species species, Habitat habitat){
        _ID_ANIMAL = idAnimal;
        _name = name;
        _species = species;
        _habitat = habitat;
    }

    /**
     * Getter.
     * 
     * @return animal's Id.
     */
    public String getId(){
        return _ID_ANIMAL;
    }

    /**
     * Getter.
     * 
     * @return Species of the animal.
     */
    public Species getSpecies(){
        return _species;
    }

    /**
     * Getter.
     * 
     * @return current habitat.
     */
    public Habitat getHabitat(){
        return _habitat;
    }

    /**
     * Setter.
     * 
     * @param habitat
     */
    public void setHabitat(Habitat habitat){
        _habitat = habitat;
    }

    /**
     * Adds a new vaccination to the animal's health history.
     * 
     * @param vaccination
     */
    public void addVaccination(Vaccination vaccination){
        _healthHistory.add(vaccination);
    }

    /**
     * Convert animal to string.
     * "ANIMAL|Id|Name|IdSpecies|HealthHistory|IdHabitat"
     * 
     * @return String representation of the animal.
     */
    @Override
    public String toString(){
        return "ANIMAL|" + _ID_ANIMAL + "|" + _name + "|" + _species.getId()
        + "|" + healthHistoryToString() + "|" + _habitat.getId();
    }

    /**
     * Convert health history to string.
     * returns "VOID" if the health history is empty.
     * 
     * @return String representation of the health history.
     */
    public String healthHistoryToString(){
        if(_healthHistory.isEmpty()){
            return "VOID";
        }

        StringBuilder healthHistoryString = new StringBuilder();
        for (Vaccination vaccination : _healthHistory){
            healthHistoryString.append(vaccination.getDamage()).append(",");
        }

        healthHistoryString.setLength(healthHistoryString.length() - 1);
        return healthHistoryString.toString();
    }

    /**
     * Accepts a visitor.
     * 
     * @param visitor
     * @return the result of the visit.
     */
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
