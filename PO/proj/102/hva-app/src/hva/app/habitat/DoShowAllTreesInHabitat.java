package hva.app.habitat;

import java.nio.file.ProviderNotFoundException;

import hva.Hotel;
import hva.exceptions.UnknownHabitatException;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

import hva.app.exceptions.UnknownHabitatKeyException;
import hva.exceptions.UnknownHabitatException;


class DoShowAllTreesInHabitat extends Command<Hotel> {

    DoShowAllTreesInHabitat(Hotel receiver) {
        super(Label.SHOW_TREES_IN_HABITAT, receiver);
        addStringField("idHabitat", Prompt.habitatKey());
        }

    @Override
    protected void execute() throws CommandException {
        try{
            _display.popup(_receiver.showAllTreesInHabitat(stringField("idHabitat")));
        } catch (UnknownHabitatException e) {
            throw new UnknownHabitatKeyException(stringField("idHabitat"));
        }
    }
}
 