package hva.app.employee;

import hva.Hotel;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

import hva.app.exceptions.UnknownEmployeeKeyException;
import hva.exceptions.UnknownEmployeeException;
import java.lang.Math;

class DoShowSatisfactionOfEmployee extends Command<Hotel> {

    DoShowSatisfactionOfEmployee(Hotel receiver) {
        super(Label.SHOW_SATISFACTION_OF_EMPLOYEE, receiver);
        addStringField("idEmployee", Prompt.employeeKey());
    }

    @Override
    protected void execute() throws CommandException {
        try {
            _display.popup(
                Math.round(
                _receiver.showSatisfactionOfEmployee(stringField("idEmployee"))));
        } catch (UnknownEmployeeException e) {
            throw new UnknownEmployeeKeyException(stringField("idEmployee"));
        }
    }

}
