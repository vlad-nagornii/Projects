package hva.app.habitat;

import hva.Hotel;
import hva.exceptions.UnknownAnimalException;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

import hva.app.exceptions.UnknownHabitatKeyException;
import hva.app.exceptions.UnknownSpeciesKeyException;
import hva.exceptions.UnknownHabitatException;
import hva.exceptions.UnknownSpeciesException;

class DoChangeHabitatInfluence extends Command<Hotel> {

    DoChangeHabitatInfluence(Hotel receiver) {
        super(Label.CHANGE_HABITAT_INFLUENCE, receiver);
        addStringField("idHabitat", Prompt.habitatKey());
        addStringField("idSpecies", hva.app.animal.Prompt.speciesKey()	);
        addOptionField("influence", Prompt.habitatInfluence(), new String[] { "POS", "NEG", "NEU"});
    }

    @Override
    protected void execute() throws CommandException {
        try{
            _receiver.changeHabitatInfluence(
                stringField("idHabitat"), 
                stringField("idSpecies"), 
                stringField("influence"));
        } catch (UnknownHabitatException e) {
            throw new UnknownHabitatKeyException(stringField("idHabitat"));
        } catch (UnknownSpeciesException e) {
            throw new UnknownSpeciesKeyException(stringField("idSpecies"));
        }
    }

}
