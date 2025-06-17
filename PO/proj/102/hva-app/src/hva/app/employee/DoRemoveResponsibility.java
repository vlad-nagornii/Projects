package hva.app.employee;

import hva.Hotel;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

import hva.exceptions.NoResponsibilityFoundException;
import hva.exceptions.UnknownEmployeeException;
import hva.app.exceptions.NoResponsibilityException;
import hva.app.exceptions.UnknownEmployeeKeyException;

class DoRemoveResponsibility extends Command<Hotel> {

    DoRemoveResponsibility(Hotel receiver) {
        super(Label.REMOVE_RESPONSABILITY, receiver);
        addStringField("idEmployee", Prompt.employeeKey());
        addStringField("idResponsibility", Prompt.responsibilityKey());        
    }

    @Override
    protected void execute() throws CommandException {
        try {
            _receiver.removeResponsibility(
                stringField("idEmployee"), 
                stringField("idResponsibility"));
        } catch (UnknownEmployeeException e) {
            throw new UnknownEmployeeKeyException(stringField("idEmployee"));
        } catch (NoResponsibilityFoundException e) {
            throw new NoResponsibilityException(stringField("idEmployee"), 
                                                stringField("idResponsibility"));
        }
    }

}
