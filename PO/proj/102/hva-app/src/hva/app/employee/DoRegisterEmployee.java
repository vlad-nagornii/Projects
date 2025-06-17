package hva.app.employee;

import hva.Hotel;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

import hva.app.exceptions.DuplicateEmployeeKeyException;
import hva.exceptions.DuplicateEmployeeException;
import hva.exceptions.UnrecognizedEntryException;


class DoRegisterEmployee extends Command<Hotel> {

    DoRegisterEmployee(Hotel hotel) {
        super(Label.REGISTER_EMPLOYEE, hotel);
        addStringField("idEmployee", Prompt.employeeKey());
        addStringField("name", Prompt.employeeName());
        addOptionField("type", Prompt.employeeType(), new String[] { "VET", "TRT" });
    }

    @Override
    protected void execute() throws CommandException {
        try {
            _receiver.registerEmployee(
                stringField("idEmployee"), 
                stringField("name"), 
                stringField("type"));
        } catch (DuplicateEmployeeException e) {
            throw new DuplicateEmployeeKeyException(stringField("idEmployee"));
        } catch (UnrecognizedEntryException e) {
           e.printStackTrace();
        }
    }
}
