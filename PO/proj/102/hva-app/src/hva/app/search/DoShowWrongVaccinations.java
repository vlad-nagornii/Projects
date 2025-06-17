package hva.app.search;

import hva.Hotel; 
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

import hva.vaccine.Vaccination;
import hva.visitor.RenderEntity;
import hva.visitor.Selector;
import hva.vaccine.Damage;




class DoShowWrongVaccinations extends Command<Hotel> {

    DoShowWrongVaccinations(Hotel receiver) {
        super(Label.WRONG_VACCINATIONS, receiver);
    }

    @Override
    protected void execute() throws CommandException {
        Selector<Vaccination> selector = new Selector<Vaccination>() {
            @Override
            public boolean ok(Vaccination vaccination) {
                return vaccination.getDamage() != Damage.NORMAL;
            }
        };
        _display.popup(_receiver.showWrongVaccinations(selector, new RenderEntity()));
    }

}
