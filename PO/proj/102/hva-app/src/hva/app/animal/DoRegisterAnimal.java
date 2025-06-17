package hva.app.animal;

import java.util.Scanner;

import pt.tecnico.uilib.forms.Form; 
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

import hva.Hotel;
import hva.exceptions.DuplicateAnimalException;
import hva.exceptions.DuplicateSpeciesNameException;
import hva.exceptions.UnknownHabitatException;
import hva.exceptions.UnknownSpeciesException;
import hva.app.exceptions.DuplicateAnimalKeyException;
import hva.app.exceptions.UnknownHabitatKeyException;

class DoRegisterAnimal extends Command<Hotel> {

    DoRegisterAnimal(Hotel receiver) {
        super(Label.REGISTER_ANIMAL, receiver);
        addStringField("id", Prompt.animalKey());
        addStringField("name", Prompt.animalName());
        addStringField("idSpecies", Prompt.speciesKey());
        addStringField("idHabitat", hva.app.habitat.Prompt.habitatKey());
    }

    @Override
    protected final void execute() throws CommandException {
        try{
            _receiver.registerAnimal(
                stringField("id"),
                stringField("name"),
                stringField("idSpecies"),
                stringField("idHabitat"));
        } catch (DuplicateAnimalException e) {
            throw new DuplicateAnimalKeyException(stringField("id"));
        } catch (UnknownHabitatException e){
            throw new UnknownHabitatKeyException(stringField("idHabitat"));
        } catch (UnknownSpeciesException e){
            doRegisterSpecies();
            try{
                _receiver.registerAnimal(
                    stringField("id"),
                    stringField("name"),
                    stringField("idSpecies"),
                    stringField("idHabitat"));
            } catch (DuplicateAnimalException e1){
                throw new DuplicateAnimalKeyException(stringField("id"));
            } catch (UnknownHabitatException e1){
                throw new UnknownHabitatKeyException(stringField("idHabitat"));
            } catch (UnknownSpeciesException e1){
                e.printStackTrace();
            }
        }
    }

    /**
     * Asks for a speciesName and registers a new Species.
     * If the name is not unique, it asks again.
     */
    public void doRegisterSpecies(){
        Form form = new Form();
            form.addStringField("speciesName", Prompt.speciesName());
            form.parse();
            try {
                _receiver.registerSpecies(stringField("idSpecies"), form.stringField("speciesName"));
            } catch (DuplicateSpeciesNameException e1){
                doRegisterSpecies();
            }   
    }
}
