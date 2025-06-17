package hva.app.vaccine;

import hva.Hotel;
import hva.app.exceptions.UnknownAnimalKeyException;
import hva.app.exceptions.UnknownVeterinarianKeyException;
import hva.app.exceptions.UnknownVaccineKeyException;
import hva.app.exceptions.VeterinarianNotAuthorizedException;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import hva.exceptions.UnknownAnimalException;
import hva.exceptions.UnknownEmployeeException;
import hva.exceptions.UnknownVaccineException;
import hva.exceptions.UnknownVeterinarianException;
import hva.exceptions.UnrecognizedEntryException;
import hva.exceptions.VeterinarianUnauthorizedException;
import hva.exceptions.VaccineNotValidForSpeciesException;

class DoVaccinateAnimal extends Command<Hotel> {

    DoVaccinateAnimal(Hotel receiver) {
        super(Label.VACCINATE_ANIMAL, receiver);
        addStringField("idVaccine", Prompt.vaccineKey());
        addStringField("idVeterinarian", Prompt.veterinarianKey());
        addStringField("idAnimal", hva.app.animal.Prompt.animalKey());
    }

    @Override
    protected final void execute() throws CommandException {
        try{
            _receiver.vaccinateAnimal(
            stringField("idVaccine"),
            stringField("idVeterinarian"),
            stringField("idAnimal"));
        } catch (UnknownVeterinarianException | UnknownEmployeeException e) {
            throw new UnknownVeterinarianKeyException(
                                        stringField("idVeterinarian"));
        } catch (VeterinarianUnauthorizedException e) {
            throw new VeterinarianNotAuthorizedException(
                                        stringField("idVeterinarian"),
                                        e.getIdSpecies());
        } catch (VaccineNotValidForSpeciesException e) {
            _display.popup(Message.wrongVaccine(
                                        stringField("idVaccine"),
                                        stringField("idAnimal")));
        } catch (UnknownVaccineException e) {
            throw new UnknownVaccineKeyException(stringField("idVaccine"));
        } catch (UnknownAnimalException e) {
            throw new UnknownAnimalKeyException(stringField("idAnimal"));
        }
    }

}
