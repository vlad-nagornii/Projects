package hva.tree.evergreenSeason;
import hva.tree.BiologicalCycle;
import hva.tree.Evergreen;

public class Autumn extends EvergreenSeason{

    public Autumn(Evergreen tree) { 
        super(tree);
        tree.setSeasonalEffort(1);
        tree.setBiologicalCycle(BiologicalCycle.COMFOLHAS);
     }

    @Override
    public void advanceSeason(){
        _tree.setSeason(new Winter(_tree));
        _tree.increaseAge();
    }
}
