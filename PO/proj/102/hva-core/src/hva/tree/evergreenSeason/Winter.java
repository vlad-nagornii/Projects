package hva.tree.evergreenSeason;
import hva.tree.BiologicalCycle;
import hva.tree.Evergreen;

public class Winter extends EvergreenSeason{

    public Winter(Evergreen tree) { 
        super(tree);
        tree.setSeasonalEffort(2);
        tree.setBiologicalCycle(BiologicalCycle.LARGARFOLHAS);
    }

    @Override
    public void advanceSeason(){
        _tree.setSeason(new Spring(_tree));
        _tree.increaseAge();
    }
}
