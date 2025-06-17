package hva;

import hva.animal.Animal;
import hva.animal.Species;
import hva.employee.Employee; 
import hva.employee.Keeper;
import hva.employee.Veterinarian;
import hva.exceptions.DuplicateAnimalException;
import hva.exceptions.DuplicateEmployeeException;
import hva.exceptions.DuplicateHabitatException;
import hva.exceptions.DuplicateSpeciesNameException;
import hva.exceptions.DuplicateTreeException;
import hva.exceptions.DuplicateVaccineException;
import hva.exceptions.ImportFileException;
import hva.exceptions.NoResponsibilityFoundException;
import hva.exceptions.UnknownAnimalException;
import hva.exceptions.UnknownEmployeeException;
import hva.exceptions.UnknownHabitatException;
import hva.exceptions.UnknownSpeciesException;
import hva.exceptions.UnknownVaccineException;
import hva.exceptions.UnknownVeterinarianException;
import hva.exceptions.UnrecognizedEntryException;
import hva.exceptions.VaccineNotValidForSpeciesException;
import hva.exceptions.VeterinarianUnauthorizedException;
import hva.habitat.Habitat;
import hva.tree.*;
import hva.vaccine.Damage;
import hva.vaccine.Vaccination;
import hva.vaccine.Vaccine;
import hva.visitor.NormalSatisfaction;
import hva.visitor.RenderEntity;
import hva.visitor.Selector;
import hva.visitor.Visitable;
import hva.visitor.Visitor;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Hotel implements Visitable, Serializable {

    /** Class serial number. */
    @Serial
    private static final long serialVersionUID = 202407081733L;

    /** Current Season */
    private Season _season = Season.PRIMAVERA; 
    
    /** Species on the hotel. */
    private Map<String, Species> _species = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    /** Animals on the hotel. */
    private Map<String, Animal> _animals = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    /** Employees on the hotel. */
    private Map<String, Employee> _employees = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    /** Habitats on the hotel. */
    private Map<String, Habitat> _habitats = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    /** Vaccines on the hotel. */
    private Map<String, Vaccine> _vaccines = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    /** Vaccinations by order of aplience. */
    private List<Vaccination> _vaccinations = new ArrayList<>();

    /** Trees on the hotel */
    private Map<String, Tree> _trees = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    /** Hotel object has been changed */
    private boolean _changed = false;

    /** 
     * Set Hotel to changed. 
     */
    public void changed() {
        setChanged(true);
    }
    
    /** 
     * @return boolean changed status
     */
    public boolean getChanged() {
        return _changed;
    }

    /** 
     * @param changed
     */
    public void setChanged(boolean changed) {
        _changed = changed;
    }

    /**
     * Returns all the employees in the hotel.
     * 
     * @return Map of employees.
     */
    public Map<String, Employee> getEmployees() {
        return _employees;
    }

    public Map<String, Habitat> getHabitats() {
        return _habitats;
    }

    /**
     * Returns all the animals in the hotel.
     * 
     * @returnc Map of animals.
     */
    public Map<String, Animal> getAnimals() {
        return _animals;
    }

    
    /**
     * Read text input file and create domain entities.
     *
     * @param filename name of the text input file
     * @throws ImportFileException
     */
    void importFile(String filename) throws ImportFileException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                importFromFields(line.split("\\|"));
            }
        } catch (DuplicateAnimalException | DuplicateEmployeeException |
                DuplicateVaccineException | DuplicateHabitatException |
                DuplicateTreeException | DuplicateSpeciesNameException |
                UnknownHabitatException | UnknownSpeciesException |
                UnknownEmployeeException | UnrecognizedEntryException |
                NoResponsibilityFoundException e ) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new ImportFileException(filename, e);
        }
    }

    /**
     * Imports the fields from the file and creates the corresponding object.
     * 
     * @param fields
     * @throws DuplicateSpeciesNameException
     * @throws DuplicateHabitatException
     * @throws DuplicateAnimalException
     * @throws DuplicateEmployeeException
     * @throws DuplicateVaccineException
     * @throws DuplicateTreeException
     * @throws UnknownHabitatException
     * @throws UnknownSpeciesException
     * @throws UnknownEmployeeException
     * @throws UnrecognizedEntryException
     * @throws NoResponsibilityFoundException
     */
    public void importFromFields(String[] fields)
            throws DuplicateSpeciesNameException, DuplicateHabitatException,
            DuplicateAnimalException, DuplicateEmployeeException,
            DuplicateVaccineException, DuplicateTreeException,
            UnknownHabitatException, UnknownSpeciesException,
            UnknownEmployeeException, UnrecognizedEntryException,
            NoResponsibilityFoundException {
        String type = fields[0];
        switch (type) {
            case "ESPÉCIE" -> importSpecies(fields);
            case "ÁRVORE" -> importTree(fields);
            case "HABITAT" -> importHabitat(fields);
            case "ANIMAL" -> importAnimal(fields);
            case "TRATADOR", "VETERINÁRIO" -> importEmployee(fields);
            case "VACINA" -> importVaccine(fields);
            default -> throw new UnrecognizedEntryException(type);
        }
        changed();
    }

    /**
     * Imports a new Species from a file.
     * 
     * @param fields [ESPÉCIE, id, nome]
     * @throws DuplicateSpeciesNameException
     */ 
    public void importSpecies(String[] fields)
            throws DuplicateSpeciesNameException{
        registerSpecies(fields[1], fields[2]); 
    }
    
    /**
     * Imports a new Tree from a file.
     * 
     * @param fields [ÁRVORE, id, nome, idade, dificuldade, tipo]
     */
    public void importTree(String[] fields) throws DuplicateTreeException,
            UnknownHabitatException, UnrecognizedEntryException {
        registerTree(fields[1], fields[2], (float) Integer.parseInt(fields[3]),
                Integer.parseInt(fields[4]), fields[5]);
    }

    /**
     * Imports a new Habitat from a file.
     * 
     * @param fields [HABITAT, id, nome, área, [idÁrvore1, ..., idÁrvoreN]]
     * @throws DuplicateHabitatException
     * @throws UnrecognizedEntryException
     */
    public void importHabitat(String[] fields)
            throws DuplicateHabitatException, UnrecognizedEntryException {
        int area = Integer.parseInt(fields[3]);
        registerHabitat(fields[1], fields[2], area);
        if(fields.length > 4){
            for (String idTree : fields[4].split(",")) {
                try {
                    addTreeToHabitat(fields[1], idTree);
                } catch (UnknownHabitatException e) {
                    e.printStackTrace();
                }
            }
        }
    } 

    /**
     * Imports a new Animal from a file.
     * 
     * @param fields [ANIMAL, id, nome, idEspécie, idHabitat]
     * @throws DuplicateAnimalException
     * @throws UnknownHabitatException
     * @throws UnknownSpeciesException
     */
    public void importAnimal(String[] fields)
            throws DuplicateAnimalException, UnknownHabitatException,
            UnknownSpeciesException {
       registerAnimal(fields[1], fields[2], fields[3], fields[4]);
    }

    /**
     * Imports a new Employee from a file.
     * 
     * @param fields [TRATADOR, id, nome, [idHabitat1, ..., idHabitatN]]
     * @throws DuplicateEmployeeException
     * @throws UnrecognizedEntryException
     */
    public void importEmployee(String[] fields) 
            throws UnrecognizedEntryException, DuplicateEmployeeException,
            NoResponsibilityFoundException, UnknownEmployeeException {

        String employeeType = switch (fields[0]) {
            case "VETERINÁRIO" -> "VET";
            case "TRATADOR" -> "TRT";
            default -> throw new UnrecognizedEntryException(fields[0]);
        };
        registerEmployee(fields[1], fields[2], employeeType);
        if (fields.length > 3) {
            for (String idResponsibility : fields[3].split(",")) {
                addResponsibility(fields[1], idResponsibility);
            }
        }
   }

    /**
     * Imports a new Vaccine from a file.
     * 
     * @param fields [VACINA, id, nome, [idEspécie1, ..., idEspécieN]]
     * @throws DuplicateVaccineException
     * @throws UnknownSpeciesException
     * @throws UnrecognizedEntryException
     */
    public void importVaccine(String[] fields) 
            throws DuplicateVaccineException, UnknownSpeciesException,
            UnrecognizedEntryException {
        switch(fields.length) {
            case 3 -> registerVaccine(fields[1], fields[2], "");
            case 4 -> registerVaccine(fields[1], fields[2], fields[3]);
            default -> throw new UnrecognizedEntryException(fields[0]);
        };
    }


    /**
     * Adds an species to the _species Map in the Hotel.
     * Sets Hotel to changed.
     * 
     * @param species
     */
    public void addSpecies(Species species){
        _species.put(species.getId(), species);
        changed();
    }

    /**
     * Adds a Habitat to the _habitats map.
     * Sets the Hotel to changed.
     * 
     * @param habitat
     */
    public void addHabitat(Habitat habitat){
        _habitats.put(habitat.getId(), habitat);
        changed();
    }

     /**
     * Adds a tree to the _trees map.
     * Sets the Hotel to changed.
     * 
     * @param habitat
     */
    public void addTree(Tree tree){
        _trees.put(tree.getId(), tree);
        changed();
    }
  
    /**
     * Adds an animal to the _animals Map in the Hotel.
     * Sets Hotel to changed.
     * 
     * @param animal
     */
    public void addAnimal(Animal animal){
        _animals.put(animal.getId(), animal);
        changed();
    }

    /**
     * Adds an employee to the _employees Map in the Hotel.
     * Sets the Hotel to changed.
     * 
     * @param employee
     */
    public void addEmployee(Employee employee) {
        _employees.put(employee.getId(), employee);
        changed();
    }

    /**
     * Adds a Vaccine to the _vaccines Map in the Hotel.
     * Sets the Hotel to changed.
     * 
     * @param vaccine
     */
    public void addVaccine(Vaccine vaccine) {
        _vaccines.put(vaccine.getId(), vaccine);
        changed();
    }

    /**
     * Adds a Vaccination to the _vaccinations List in the Hotel.
     * Sets the Hotel to changed().
     * 
     * @param vaccination
     */
    public void addVaccination(Vaccination vaccination) {
        _vaccinations.add(vaccination);
        changed();
    }


    /**
     * Getter.
     * 
     * @return the current season
     */
    public Season getSeason() {
        return _season;
    }

    /**
     * Checks if the habitat exists and returns it.
     * 
     * @param idHabitat
     * @return Habitat with the given id.
     * @throws UnknownHabitatException
     */
    public Habitat getHabitat(String idHabitat) throws UnknownHabitatException {
        if(!_habitats.containsKey(idHabitat)){
            throw new UnknownHabitatException(idHabitat);
        }
        return _habitats.get(idHabitat);
    }

    /**
     * Checks if the tree exists and returns it.
     * 
     * @param idTree
     * @return Tree with the given id.
     */
    public Tree getTree(String idTree) {
        if(!_trees.containsKey(idTree)){
            //????FIX MEthrow new UnknownTreeException(idTree);
        }
        return _trees.get(idTree);
    }

    /**
     * Checks if the employee exists and returns it.
     * 
     * @param idEmployee
     * @return Employee with the given id.
     * @throws UnknownEmployeeException
     */
    public Employee getEmployee(String idEmployee) throws UnknownEmployeeException {
        if(!_employees.containsKey(idEmployee)){
            throw new UnknownEmployeeException(idEmployee);
        }
        return _employees.get(idEmployee);
    }

    /**
     * Checks if the species exists and returns it.
     * 
     * @param idSpecies
     * @return Species with the given id.
     * @throws UnknownSpeciesException
     */
    public Species getSpecies(String idSpecies) throws UnknownSpeciesException {
        if(!_species.containsKey(idSpecies)){
            throw new UnknownSpeciesException(idSpecies);
        }
        return _species.get(idSpecies);
    }

    /**
     * Checks if the animal exists and returns it.
     * 
     * @param idAnimal
     * @return Animal with the given id.
     * @throws UnknownAnimalException
     */
    public Animal getAnimal(String idAnimal) throws UnknownAnimalException {
        if(!_animals.containsKey(idAnimal)){
            throw new UnknownAnimalException(idAnimal);
        }
        return _animals.get(idAnimal);
    }

    /**
     * Checks if the vaccine exists and returns it.
     * 
     * @param idVaccine
     * @return Vaccine with the given id
     */
    public Vaccine getVaccine(String idVaccine) throws UnknownVaccineException {
        if(!_vaccines.containsKey(idVaccine)){
            throw new UnknownVaccineException(idVaccine);
        }
        return _vaccines.get(idVaccine);
    }
    
    /**
     * Advances to the next season.
     * 
     * @return int 0 through 3, corresponing to the season.
     */
    public int advanceSeason() { 
        for (Tree tree : _trees.values()) {
            tree.advanceSeason();
        }
        changed();
        int i = 0;
        
        switch(_season){
            case PRIMAVERA -> { 
                            _season = Season.VERAO;
                            i = 1;
                            }
            case VERAO -> { 
                            _season = Season.OUTONO;
                            i = 2;
                            }
            case OUTONO ->{
                            _season = Season.INVERNO;
                            i = 3;
                            }
            case INVERNO -> {
                            _season = Season.PRIMAVERA;
                            i = 0;
                            }
        }
        return i;
    }

    /**
     * Creates an instance of Species and adds it to the map _species.
     * 
     * @param idSpecies
     * @param name
     * @throws DuplicateSpeciesNameException
     */
    public void registerSpecies(String idSpecies, String name)
            throws DuplicateSpeciesNameException{
        for (Species species : _species.values()) {
            if (species.getName().equals(name)) {
                throw new DuplicateSpeciesNameException(name);
            }
        }
        Species species = new Species(idSpecies, name);
        addSpecies(species);
    }

    /**
     * Registers a new habitat with the given parameters and inserts it in
     * the _habitats map.
     * 
     * @param idHabitat
     * @param name
     * @param area
     * @throws DuplicateHabitatException
     * @throws UnrecognizedEntryException
     */
    public void registerHabitat(String idHabitat, String name, int area)
            throws DuplicateHabitatException, UnrecognizedEntryException {
        if(_habitats.containsKey(idHabitat)){
            throw new DuplicateHabitatException(idHabitat);
        }
        if(area <= 0){
            throw new UnrecognizedEntryException(Integer.toString(area));
        }

        Habitat habitat = new Habitat(idHabitat, name, area);
        addHabitat(habitat);
    }

    /**
     * Registers a new tree with the given parameters and inserts it in the
     *  _trees map.
     * 
     * @param idTree
     * @param name
     * @param age
     * @param difficulty
     * @param treeType
     * @return the new tree.
     * @throws DuplicateTreeException
     * @throws UnrecognizedEntryException
     */      
    public Tree registerTree(String idTree, String name, float age, 
                             int difficulty, String treeType)
            throws DuplicateTreeException, UnrecognizedEntryException {
        if(_trees.containsKey(idTree)){
            throw new DuplicateTreeException(idTree);
        }
        if(age < 0) {
            throw new UnrecognizedEntryException(Integer.toString((int)age));
        }
        Tree tree = switch (treeType) {
            case "PERENE" -> new Evergreen(idTree, name, age, difficulty);
            case "CADUCA" -> new Deciduous(idTree, name, age, difficulty);
            default -> throw new UnrecognizedEntryException(treeType);
        };

        tree.setCurrentSeason(getSeason());
        addTree(tree);
        return tree;
    }

    /**
     * Registers a new animal with the given parameters ~
     * and inserts it in the animal map.
     * 
     * @param idAnimal
     * @param name
     * @param idSpecies 
     * @param idHabitat 
     * @throws DuplicateAnimalException
     * @throws UnknownHabitatException
     * @throws UnknownSpeciesException
     */
    public void registerAnimal(String idAnimal, String name,
                               String idSpecies, String idHabitat)
            throws DuplicateAnimalException, UnknownHabitatException,
            UnknownSpeciesException {
        if(_animals.containsKey(idAnimal)){
            throw new DuplicateAnimalException(idAnimal);
        }
        Species species = getSpecies(idSpecies);
        Habitat habitat = getHabitat(idHabitat);
        Animal animal = new Animal(idAnimal, name, species, habitat);
        addAnimal(animal);
        species.addAnimal(animal);
        habitat.addAnimal(animal);
    }

    /**
     * Registers a new employee with the given parameters
     * and inserts it in the _employees map.
     * 
     * @param idEmployee
     * @param name
     * @param employeeType
     * @throws DuplicateEmployeeException
     * @throws UnrecognizedEntryException
     */
    public void registerEmployee(String idEmployee,
                                 String name, String employeeType)
            throws DuplicateEmployeeException, UnrecognizedEntryException {
        Employee employee = switch (employeeType) {
            case "VET" -> new Veterinarian(idEmployee, name);
            case "TRT" -> new Keeper(idEmployee, name);
            default -> throw new UnrecognizedEntryException(employeeType);
        };
        if(_employees.containsKey(idEmployee)){
            throw new DuplicateEmployeeException(idEmployee);
        }
        addEmployee(employee);
    }

    /**
     * Registers a new vaccine with the given parameters and inserts it in the 
     * _vaccines map.
     * 
     * @param idVaccine
     * @param name
     * @param speciesInput
     * @throws DuplicateVaccineException
     * @throws UnknownSpeciesException
     */
    public void registerVaccine(String idVaccine, String name, String speciesInput)
            throws DuplicateVaccineException, UnknownSpeciesException {
        if(_vaccines.containsKey(idVaccine)){
            throw new DuplicateVaccineException(idVaccine);
        }
        if(speciesInput.isBlank()){
            Vaccine vaccine = new Vaccine(idVaccine, name);
            addVaccine(vaccine);
            return;
        }
        String[] speciesList = speciesInput.split(",");
        for (String idSpecie : speciesList) {
            if(!_species.containsKey(idSpecie.trim())){
                throw new UnknownSpeciesException(idSpecie);
            }
        }
        Vaccine vaccine = new Vaccine(idVaccine, name);
        for (String idSpecie : speciesList) {
            Species species = _species.get(idSpecie.trim());
            vaccine.addTargetSpecies(species);
        }
        addVaccine(vaccine);      
    }
    

    /**
     * Adds a responsibility to an employee.
     * 
     * @param idEmployee
     * @param idResponsibility
     * @throws NoResponsibilityFoundException
     * @throws UnknownEmployeeException
     */
    public void addResponsibility(String idEmployee, String idResponsibility) 
            throws NoResponsibilityFoundException, UnknownEmployeeException {
        try{
            Employee employee = getEmployee(idEmployee);
            if(employee instanceof Keeper){
                Keeper keeper = (Keeper) employee;
                Habitat habitat = getHabitat(idResponsibility);
                keeper.addResponsibility(habitat);
                habitat.addResponsibleKeeper(keeper);
            }
            if(employee instanceof Veterinarian){
                Veterinarian veterinarian = (Veterinarian) employee;
                Species species = getSpecies(idResponsibility);
                veterinarian.addResponsibility(species);
                species.addResponsibleVet(veterinarian);
            }
            changed();
        } catch (UnknownHabitatException | UnknownSpeciesException e) {
            throw new NoResponsibilityFoundException(idEmployee, idResponsibility);
        }
    } 

    /**
     * Removes a responsibility from an employee.
     * 
     * @param idEmployee
     * @param idResponsibility
     * @throws NoResponsibilityFoundException
     * @throws UnknownEmployeeException
     */
    public void removeResponsibility(String idEmployee, String idResponsibility) 
            throws NoResponsibilityFoundException, UnknownEmployeeException {
        try{
            Employee employee = getEmployee(idEmployee);
            if(employee instanceof Keeper){
                Keeper keeper = (Keeper) employee;
                Habitat habitat = getHabitat(idResponsibility);
                keeper.removeResponsibility(habitat.getId());
            }
            if(employee instanceof Veterinarian){
                Veterinarian veterinarian = (Veterinarian) employee;
                Species species = getSpecies(idResponsibility);
                veterinarian.removeResponsibility(species.getId());
            }
            changed();
        } catch (NoResponsibilityFoundException | UnknownHabitatException | 
                UnknownSpeciesException e) {
            throw new NoResponsibilityFoundException(idEmployee, idResponsibility);
        }
    }

    /**
     * Adds the given tree to the given habitat.
     * 
     * @param idHabitat
     * @param idTree
     * @throws UnknownHabitatException
     */
    public String addTreeToHabitat(String idHabitat, String idTree)
            throws UnknownHabitatException {
        Habitat habitat = getHabitat(idHabitat);
        Tree tree = getTree(idTree);
        habitat.addTree(tree);
        changed();
        return tree.toString();
    }

    /**
     * Changes the area of the given habitat.
     * 
     * @param idHabitat
     * @param area
     * @throws UnknownHabitatException
     */
    public void changeHabitatArea(String idHabitat, int area)
            throws UnknownHabitatException, UnrecognizedEntryException {
        Habitat habitat = getHabitat(idHabitat);
        if(area <= 0){
            throw new UnrecognizedEntryException(Integer.toString(area));
        }
        habitat.setArea(area);
        changed();
    }

    /**
     * Transfers an animal to a new habitat.
     * 
     * @param idAnimal
     * @param idHabitat
     * @throws UnknownAnimalException
     * @throws UnknownHabitatException
     */
    public void transferAnimalToHabitat(String idAnimal, String idHabitat) 
            throws UnknownAnimalException, UnknownHabitatException {
        Animal animal = getAnimal(idAnimal);
        Habitat habitat = getHabitat(idHabitat);
        animal.getHabitat().removeAnimal(animal);
        animal.setHabitat(habitat); 
        habitat.addAnimal(animal);
        changed();
    }

    /**
     * Converts to a string all the Habitats in the Hotel and the corresponding
     * Trees.
     * 
     * @return String with all the Habitats
     */
    public String showAllHabitats(){
        String allHabitats = "";
        RenderEntity renderEntity = new RenderEntity();
        for (Habitat habitat : _habitats.values()) {
            allHabitats += renderEntity.visit(habitat);
            allHabitats += "\n";
        }     
        return allHabitats.length() > 0 ? 
        allHabitats.substring(0, allHabitats.length() - 1) : allHabitats;
    }


    /**
     * Converts to a string all the Animals in the Hotel.
     * 
     * @return String with all the animals
     */
    public String showAllAnimals(){
        String allAnimals = "";
        RenderEntity renderEntity = new RenderEntity();
        for (Animal animal : _animals.values()) {
            allAnimals+= renderEntity.visit(animal);
            allAnimals += "\n";
        }
        return allAnimals.length() > 0 ? 
        allAnimals.substring(0, allAnimals.length() - 1) : allAnimals;
    }

    /**
     * Convertes to a string all employees in the hotel.
     * 
     * @return String with all the employees
     */
    public String showAllEmployees() {
        String allEmployees = "";
        RenderEntity renderEntity = new RenderEntity();
        for (Employee employee : _employees.values()) {
            allEmployees += renderEntity.visit(employee);
            allEmployees += "\n";
        }
        return allEmployees.length() > 0 ? 
        allEmployees.substring(0, allEmployees.length() - 1) : allEmployees;
    }

    /**
     * Converts to a string all the vaccines in the Hotel.
     * 
     * @return String with all the vaccines
     */
    public String showAllVaccines(){
        String allVaccines = "";
        RenderEntity renderEntity = new RenderEntity();
        for (Vaccine vaccine : _vaccines.values()) {
            allVaccines += renderEntity.visit(vaccine);
            allVaccines += "\n";
        }
        return allVaccines.length() > 0 ? 
        allVaccines.substring(0, allVaccines.length() - 1) : allVaccines;
    }
    
    /**
     * Shows all the trees in the given habitat.
     * 
     * @param idHabitat
     * @return String with all the trees in the habitat.
     */
    public String showAllTreesInHabitat(String idHabitat) 
            throws UnknownHabitatException {
        Habitat habitat = getHabitat(idHabitat);
        String allTrees = "";
        RenderEntity renderEntity = new RenderEntity();
        for (Tree tree : habitat.getTrees().values()) {
            allTrees += renderEntity.visit(tree);
            allTrees += "\n";
        }
        return allTrees.length() > 0 ? 
        allTrees.substring(0, allTrees.length() - 1) : allTrees;
    }


    /**
     * Converts to a string all the Vaccinations that have been made in the 
     * Hotel.
     * 
     * @return String with all Vaccinations by order of aplience
     */
    public String showVaccinations(){
        String vaccinations = "";
        for (Vaccination currentVaccination : _vaccinations) {
            vaccinations += currentVaccination.toString();
            vaccinations += "\n";
        }
        return vaccinations.length() > 0 ? 
        vaccinations.substring(0, vaccinations.length() - 1) : vaccinations;
    }

    /**
     * Vaccinates an animal with the given vaccine by the given veterinarian and
     * regists vaccination in the hotel.
     * 
     * @param idVaccine
     * @param idVeterinarian
     * @param idAnimal
     * @throws UnknownVeterinarianException
     * @throws VeterinarianUnauthorizedException
     * @throws VaccineNotValidForSpeciesException
     * @throws UnknownAnimalException
     * @throws UnknownEmployeeException
     * @throws UnknownVaccineException 
     */
    public void vaccinateAnimal(String idVaccine, String idVeterinarian,
                                String idAnimal) 
            throws UnknownVeterinarianException, UnknownVaccineException, 
            VaccineNotValidForSpeciesException, UnknownAnimalException,
            UnknownEmployeeException, VeterinarianUnauthorizedException {

        Vaccine vaccine = getVaccine(idVaccine);
        Employee employee = getEmployee(idVeterinarian);
        Animal animal = getAnimal(idAnimal);
        
        if (!(employee instanceof Veterinarian)) {
            throw new UnknownVeterinarianException(idVeterinarian);
        }
        Veterinarian veterinarian = (Veterinarian) employee;
        if (!veterinarian.checkResponsibility(animal.getSpecies().getId())) {
            throw new VeterinarianUnauthorizedException(idVeterinarian, animal.getSpecies().getId());
        }
        Vaccination vaccination = new Vaccination(vaccine, veterinarian, animal);
        addVaccination(vaccination);
        if(vaccination.getDamage() != Damage.NORMAL){
            throw new VaccineNotValidForSpeciesException(vaccine.getId(), animal.getId());
        }
    }

    public void changeHabitatInfluence(String idHabitat,
                                       String idSpecies, String influence) 
            throws UnknownHabitatException, UnknownSpeciesException {
        Habitat habitat = getHabitat(idHabitat);
        Species species = getSpecies(idSpecies);

        int influenceInt = switch (influence) {
            case "POS" -> 20;
            case "NEG" -> -20;
            default -> 0;
        };
        habitat.setInfluence(species, influenceInt);
        changed();
    }

    public double showSatisfactionOfAnimal(String idAnimal)
            throws UnknownAnimalException {
        Animal animal = getAnimal(idAnimal);
        return animal.accept(new NormalSatisfaction());
    }

    public double showSatisfactionOfEmployee(String idEmployee)
            throws UnknownEmployeeException {
        Employee employee = getEmployee(idEmployee);
        return employee.accept(new NormalSatisfaction());
    }
    
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    
    public String showAnimalsInHabitat(Selector<Animal> selector,
                                       RenderEntity renderEntity,
                                       String IdHabitat)
            throws UnknownHabitatException {
        if(!_habitats.containsKey(IdHabitat)){
            throw new UnknownHabitatException(IdHabitat);
        }
        String animalsInHabitat = "";
        for (Animal animal : _animals.values()) {
            if(selector.ok(animal)){
                animalsInHabitat += renderEntity.visit(animal);
                animalsInHabitat += "\n";
            }
        }
        return animalsInHabitat.length() > 0 ?
        animalsInHabitat.substring(0, animalsInHabitat.length() - 1) : animalsInHabitat;
    }

    public String showMedicalActsOnAnimal(Selector<Vaccination> selector,
                                          RenderEntity renderEntity,
                                          String idAnimal) 
            throws UnknownAnimalException {
        if(!_animals.containsKey(idAnimal)){
            throw new UnknownAnimalException(idAnimal);
        }
        String animalVaccinations = "";
        for(Vaccination vaccination : _vaccinations){
            if(selector.ok(vaccination)){
                animalVaccinations += renderEntity.visit(vaccination);
                animalVaccinations += "\n";
            }
        }
        return animalVaccinations.length() > 0 ?
        animalVaccinations.substring(0, animalVaccinations.length() - 1) : animalVaccinations;
    }
    
    public String showWrongVaccinations(Selector<Vaccination> selector,
                                        RenderEntity renderEntity) {
        String wrongVaccinations = "";
        for(Vaccination vaccination : _vaccinations){
            if(selector.ok(vaccination)){
                wrongVaccinations += renderEntity.visit(vaccination);
                wrongVaccinations += "\n";
            }
        }
        return wrongVaccinations.length() > 0 ?
        wrongVaccinations.substring(0, wrongVaccinations.length() - 1) : wrongVaccinations;
    }

    public String showMedicalActsByVeterinarian(Selector<Vaccination> selector,
                                                RenderEntity renderEntity,
                                                String idVeterinarian) 
            throws UnknownVeterinarianException {
        try {
            Employee employee = getEmployee(idVeterinarian);
            if(!(employee instanceof Veterinarian)){
                throw new UnknownVeterinarianException(idVeterinarian);
            }
        } catch (UnknownEmployeeException e) {
            throw new UnknownVeterinarianException(idVeterinarian);
        }

        String vetVaccinations = "";
        for(Vaccination vaccination : _vaccinations){
            if(selector.ok(vaccination)){
                vetVaccinations += renderEntity.visit(vaccination);
                vetVaccinations += "\n";
            }
        }
        return vetVaccinations.length() > 0 ? 
        vetVaccinations.substring(0, vetVaccinations.length() - 1)
        : vetVaccinations;
    }
}