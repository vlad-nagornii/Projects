package hva.tree.evergreenSeason;
import hva.tree.BiologicalCycle;
import hva.tree.Evergreen;

public class Summer extends EvergreenSeason{   

    public Summer(Evergreen tree) {
        super(tree);
        tree.setSeasonalEffort(1);
        tree.setBiologicalCycle(BiologicalCycle.COMFOLHAS);
    }

    @Override
    public void advanceSeason(){
        _tree.setSeason(new Autumn(_tree));
        _tree.increaseAge();
    }
}

