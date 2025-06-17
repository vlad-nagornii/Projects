package hva.visitor;

import hva.Hotel;
import hva.animal.Animal;
import hva.animal.Species;
import hva.employee.Employee;
import hva.employee.Keeper;
import hva.employee.Veterinarian;
import hva.habitat.Habitat;
import hva.tree.Tree;
import hva.vaccine.Vaccination;
import hva.vaccine.Vaccine;
import java.io.Serial;
import java.io.Serializable;


public class NormalSatisfaction implements Visitor<Double>, Serializable{

    /** Class serial number. */
    @Serial
    private static final long serialVersionUID = 202407081733L;

    /**
     * Calculates the satisfaction of an animal.
     * 
     * @param animal the animal to calculate the satisfaction.
     * @return the satisfaction of the animal.
     */
    @Override
    public Double visit(Animal animal) {
        Habitat habitat = animal.getHabitat();
        double numSameSpecies = -1;
        double numDifferentSpecies = 0;
        for (Animal animalSameHabitat : habitat.getAnimals().values()) {
            if (animalSameHabitat.getSpecies().equals(animal.getSpecies())) {
                numSameSpecies++;
            }
            else {
                numDifferentSpecies++;
            }
        }
        return 20 + 3 * numSameSpecies - 2 * numDifferentSpecies +
               habitat.getArea() / habitat.getPopulationSize() +
               habitat.getInfluence(animal.getSpecies());
        }

    /**
     * Calculates the satisfaction of an employee.
     * 
     * @param veterinarian the veterinarian to calculate the satisfaction.
     * @return the satisfaction of the employee.
     */
    @Override
    public Double visit(Veterinarian veterinarian) {
        double work = 0;
        for(Species specie : veterinarian.getResponsibleSpecies().values()) {
            work += specie.getAnimals().size() / specie.countResponsibleVets();
        }
        return 20 - work;
    }
    
    /**
     * Calculates the satisfaction of an employee.
     * 
     * @param keeper the employee to calculate the satisfaction.
     * @return the satisfaction of the employee.
     */
    @Override
    public Double visit(Keeper keeper) {
        double work = 0;
        float workInHabitat = 0;
        for(Habitat habitat : keeper.getResponsibleHabitats().values()) {
            workInHabitat = habitat.getArea() + 3 * habitat.getPopulationSize();
            for(Tree tree : habitat.getTrees().values()) {
                workInHabitat += tree.cleaningEffort();
            }
            work += workInHabitat / habitat.getResponsibleKeepers().size();
        }     
        return 300 - work;
    }

    /**
     * Calculates the satisfaction of the hotel.
     * 
     * @param hotel the hotel to calculate the satisfaction.
     * @return the satisfaction of the hotel.
     */
    @Override
    public Double visit(Hotel hotel) {
        double globalSatisfaction = 0;
        for (Animal animal : hotel.getAnimals().values()) {
            globalSatisfaction += animal.accept(new NormalSatisfaction());
        }
        for (Employee employee : hotel.getEmployees().values()) {
            globalSatisfaction += employee.accept(new NormalSatisfaction());
        }
        return globalSatisfaction;
    }
    @Override
    public Double visit(Habitat habitat) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override 
    public Double visit(Employee employee) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public Double visit(Vaccination vaccination) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public Double visit(Vaccine vaccine) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public Double visit(Tree tree) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
