package hva.app.search;

import hva.Hotel;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

import hva.app.exceptions.UnknownVeterinarianKeyException;
import hva.exceptions.UnknownVeterinarianException;
import hva.vaccine.Vaccination;
import hva.visitor.RenderEntity;
import hva.visitor.Selector;

class DoShowMedicalActsByVeterinarian extends Command<Hotel> {

    DoShowMedicalActsByVeterinarian(Hotel receiver) {
        super(Label.MEDICAL_ACTS_BY_VET, receiver);
        addStringField("idVet", hva.app.employee.Prompt.employeeKey());
    }

    @Override
    protected void execute() throws CommandException {
        try{
            Selector<Vaccination> selector = new Selector<Vaccination>() {
                @Override
                public boolean ok(Vaccination vaccination) {
                    return vaccination.getVeterinarian().getId().equals(stringField("idVet"));
                }
            };

            _display.popup(_receiver.showMedicalActsByVeterinarian(
                                selector,
                                new RenderEntity(),
                                stringField("idVet")));
            
        } catch (UnknownVeterinarianException e) {
            throw new UnknownVeterinarianKeyException(stringField("idVet"));
        }
    }

}
