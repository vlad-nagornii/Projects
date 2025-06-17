package hva.app.main;

import hva.HotelManager;
import pt.tecnico.uilib.forms.Form;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

class DoNewFile extends Command<HotelManager> {

    /** 
     * @param receiver
     */
    DoNewFile(HotelManager receiver) {
        super(Label.NEW_FILE, receiver);
    }

    @Override
    protected final void execute() throws CommandException {
        if (_receiver.hasChanged() && Form.confirm(Prompt.saveBeforeExit())) {
            DoSaveFile cmd = new DoSaveFile(_receiver);
            cmd.execute();
          }
          _receiver.reset();
    }
}

