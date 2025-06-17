package hva.app.animal;

import hva.Hotel;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

import hva.exceptions.UnknownAnimalException;
import hva.exceptions.UnknownHabitatException;
import hva.app.exceptions.UnknownAnimalKeyException;
import hva.app.exceptions.UnknownHabitatKeyException;

class DoTransferToHabitat extends Command<Hotel> {

    DoTransferToHabitat(Hotel hotel) {
        super(Label.TRANSFER_ANIMAL_TO_HABITAT, hotel);
        addStringField("idAnimal", Prompt.animalKey());
        addStringField("idHabitat", hva.app.habitat.Prompt.habitatKey());
    }

    @Override
    protected final void execute() throws CommandException {
        try {
            _receiver.transferAnimalToHabitat(
                stringField("idAnimal"),
                stringField("idHabitat"));
        } catch (UnknownAnimalException e) {
            throw new UnknownAnimalKeyException(stringField("idAnimal"));
        } catch (UnknownHabitatException e) {
            throw new UnknownHabitatKeyException(stringField("idHabitat"));
        }
    }
}
