package hva.app.search;

import hva.Hotel;
import hva.app.exceptions.UnknownHabitatKeyException;
import hva.exceptions.UnknownHabitatException;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

import hva.visitor.RenderEntity;
import hva.visitor.Selector;
import hva.animal.Animal;

class DoShowAnimalsInHabitat extends Command<Hotel> {

    DoShowAnimalsInHabitat(Hotel receiver) {
        super(Label.ANIMALS_IN_HABITAT, receiver);
        addStringField("idHabitat", hva.app.habitat.Prompt.habitatKey());
    }

    @Override
    protected void execute() throws CommandException {
        try{
            Selector<Animal> selector = new Selector<Animal>() {
                @Override
                public boolean ok(Animal animal) {
                    return animal.getHabitat().getId().equals(stringField("idHabitat"));
                    }
            };
            _display.popup(_receiver.showAnimalsInHabitat(selector,
                                                          new RenderEntity(),
                                                          stringField("idHabitat")));
        } catch (UnknownHabitatException e) {
            throw new UnknownHabitatKeyException(stringField("idHabitat"));
        }
    }
}
