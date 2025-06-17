package hva.tree.deciduousSeason;
import hva.tree.BiologicalCycle;
import hva.tree.Deciduous;

public class Summer extends DeciduousSeason{   

    public Summer(Deciduous tree) {
        super(tree);
        tree.setSeasonalEffort(2);
        tree.setBiologicalCycle(BiologicalCycle.COMFOLHAS);
    }

    @Override
    public void advanceSeason(){
        _tree.setSeason(new Autumn(_tree));
        _tree.increaseAge();
    }
}

