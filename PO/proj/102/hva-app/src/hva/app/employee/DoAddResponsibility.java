package hva.app.employee;

import hva.Hotel;

import hva.exceptions.UnknownEmployeeException;
import hva.exceptions.NoResponsibilityFoundException;
import hva.app.exceptions.NoResponsibilityException;
import hva.app.exceptions.UnknownEmployeeKeyException;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

class DoAddResponsibility extends Command<Hotel> {

    DoAddResponsibility(Hotel receiver) {
        super(Label.ADD_RESPONSABILITY, receiver);
        addStringField("idEmployee", Prompt.employeeKey());
        addStringField("idResponsibility", Prompt.responsibilityKey());
    }

    @Override
    protected void execute() throws CommandException {
        try {
            _receiver.addResponsibility(
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
