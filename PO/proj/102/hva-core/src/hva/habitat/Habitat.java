package hva.habitat;

import hva.animal.Animal;
import hva.animal.Species;
import hva.employee.Keeper;
import hva.tree.Tree;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Habitat implements Serializable{

    /** Class serial number. */
    @Serial
    private static final long serialVersionUID = 202407081733L;

    /** Habitat's Id. */
    private final String _ID_HABITAT;

    /** Habitat's name. */
    private String _name;

    /** Habitats area. */
    private int _area;

    /** List of trees in the habitat. */
    private Map<String, Tree> _trees = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    /** List of animals in the habitat. */
    private Map<String, Animal> _animals = new HashMap<>();

    /** List of species satisfaction in the habitat */
    private Map<Species, Integer> _speciesInfluence = new HashMap<>();

    /** List of Responsible Keepers for this habitat. */
    private Map<String, Keeper> _responsibleKeepers = new HashMap<>();


    /**
     * Constructor.
     * 
     * @param idHabitat
     * @param name
     * @param area
     */
    public Habitat(String idHabitat, String name, int area){
        _ID_HABITAT = idHabitat;
        _name = name;
        _area = area;
    }

    /**
     * Getter.
     * 
     * @return habitat's Id.
     */
    public String getId(){
        return _ID_HABITAT;
    }

    /**
     * Getter.
     * 
     * @return habitat's area.
     */
    public int getArea(){
        return _area;
    }

    /**
     * Getter.
     * 
     * @return habitat's Tree's map.
     */
    public Map<String, Tree> getTrees(){
        return _trees;
    }

    /**
     * Getter.
     * 
     * @return habitat's animal's map.
     */
    public Map<String, Animal> getAnimals(){
        return _animals;
    }

    /**
     * Counts the number of animals in the habitat.
     * 
     * @return number of animals in the habitat.
     */
    public int getPopulationSize(){
        return _animals.size();
    }

    /**
     * Adds the given animal to the habitat.
     * 
     * @param animal
     */
    public void addAnimal(Animal animal){
        _animals.put(animal.getId(), animal);
    }

    /**
     * Removes an animal from the habitat.
     * 
     * @param animal
     */
    public void removeAnimal(Animal animal){
        if(_animals.containsKey(animal.getId()))
            _animals.remove(animal.getId());
    }

    /**
     * Adds a keeper to the list of responsible keepers for this habitat.
     * 
     * @param keeper
     */
    public void addResponsibleKeeper(Keeper keeper){
        _responsibleKeepers.put(keeper.getId(), keeper);
    }

    /**
     * Setter.
     * 
     * @param species
     * @param influence
     */
    public void setInfluence(Species species, int influence){
        _speciesInfluence.put(species, influence);
    }    

    /**
     * Getter.
     * 
     * @param species
     * @return the influence of the species in the habitat.
     */
    public int getInfluence(Species species){
        if(!_speciesInfluence.containsKey(species))
            return 0;
        return _speciesInfluence.get(species);
    }

    /**
     * Getter.
     * 
     * @return the map of responsible keepers for this habitat.
     */
    public Map<String, Keeper> getResponsibleKeepers(){
        return _responsibleKeepers;
    }

    /**
     * Adds a Tree to the list of trees in the habitat.
     * 
     * @param tree to be added.
     */
    public void addTree(Tree tree){
        _trees.put(tree.getId(), tree);
    }

    /**
     * Setter.
     * 
     * @param area
     */
    public void setArea(int area){
        _area = area;
    }

    /**
     * Convert habitat to string.
     * 
     * @return String representation of the habitat.
     */
    @Override
    public String toString(){   // TODO
        String treeString = "\n";
        if(_trees.size() > 0){
            for (Tree tree : _trees.values()){
                treeString += tree.toString() + "\n";
            }
            treeString = treeString.substring(0, treeString.length() - 1);
        }
        return "HABITAT|" + _ID_HABITAT + "|" + _name + "|" + _area + "|"
        +  _trees.size() + (_trees.size() > 0 ? treeString : "");
    }
}
