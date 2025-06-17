package hva.app.search;

import hva.Hotel;
import hva.app.exceptions.UnknownAnimalKeyException;
import hva.exceptions.UnknownAnimalException;
import hva.vaccine.Damage;
import hva.vaccine.Vaccination;
import hva.visitor.RenderEntity;
import hva.visitor.Selector;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

class DoShowMedicalActsOnAnimal extends Command<Hotel> {

    DoShowMedicalActsOnAnimal(Hotel receiver) {
        super(Label.MEDICAL_ACTS_ON_ANIMAL, receiver);
        addStringField("idAnimal", hva.app.animal.Prompt.animalKey());
    }

    @Override
    protected void execute() throws CommandException {
        try{
            Selector<Vaccination> selector = new Selector<Vaccination>() {
                @Override
                public boolean ok(Vaccination vaccination) {
                    return vaccination.getAnimal().getId().equals(stringField("idAnimal"));
                }
            };

            _display.popup(_receiver.showMedicalActsOnAnimal(selector, new RenderEntity(), stringField("idAnimal")));
            
        } catch (UnknownAnimalException e){
                throw new UnknownAnimalKeyException(stringField("idAnimal"));
            }
        }

}
