package hva.app.vaccine;

import hva.Hotel;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import hva.exceptions.DuplicateVaccineException;
import hva.exceptions.UnknownSpeciesException;
import hva.exceptions.UnrecognizedEntryException;
import hva.app.exceptions.DuplicateVaccineKeyException;
import hva.app.exceptions.UnknownSpeciesKeyException;

class DoRegisterVaccine extends Command<Hotel> {

    DoRegisterVaccine(Hotel receiver) {
        super(Label.REGISTER_VACCINE, receiver);
        addStringField("idVaccine", Prompt.vaccineKey());
        addStringField("name", Prompt.vaccineName());
        addStringField("species", Prompt.listOfSpeciesKeys());      
    }

    @Override
    protected final void execute() throws CommandException {
        try {
            _receiver.registerVaccine(
                stringField("idVaccine"),
                stringField("name"),
                stringField("species"));
        } catch (DuplicateVaccineException e) {
            throw new DuplicateVaccineKeyException(stringField("idVaccine"));
        } catch (UnknownSpeciesException e){
            throw new UnknownSpeciesKeyException(e.getId());
        }
    }
}
