package hva.app.habitat;

import hva.Hotel;
import hva.app.exceptions.DuplicateTreeKeyException;
import hva.app.exceptions.UnknownHabitatKeyException;
import hva.exceptions.DuplicateTreeException;
import hva.exceptions.UnknownHabitatException;
import hva.exceptions.UnrecognizedEntryException;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

class DoAddTreeToHabitat extends Command<Hotel> {

    DoAddTreeToHabitat(Hotel receiver) {
        super(Label.ADD_TREE_TO_HABITAT, receiver);
        addStringField("idHabitat", Prompt.habitatKey());
        addStringField("idTree", Prompt.treeKey());
        addStringField("name", Prompt.treeName());
        addIntegerField("age", Prompt.treeAge());
        addIntegerField("difficulty", Prompt.treeDifficulty());
        addOptionField("type", Prompt.treeType(), new String[] { "CADUCA", "PERENE" });
    
    }

    @Override
    protected void execute() throws CommandException {
        try{
            _receiver.registerTree(
                stringField("idTree"),
                stringField("name"),
                integerField("age"),
                integerField("difficulty"),
                stringField("type"));
            _display.popup(_receiver.addTreeToHabitat(
                        stringField("idHabitat"),
                        stringField("idTree")));
        } catch (DuplicateTreeException e) {
            throw new DuplicateTreeKeyException(stringField("idTree"));
        } catch (UnknownHabitatException e) {
            throw new UnknownHabitatKeyException(stringField("idHabitat"));
        } catch (UnrecognizedEntryException e) {
            e.printStackTrace();
        }
    }
}
