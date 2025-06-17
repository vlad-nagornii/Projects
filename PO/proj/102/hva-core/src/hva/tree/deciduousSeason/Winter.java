package hva.tree.deciduousSeason;
import hva.tree.BiologicalCycle;
import hva.tree.Deciduous;


public class Winter extends DeciduousSeason{

    public Winter(Deciduous tree) { 
        super(tree);
        tree.setSeasonalEffort(0);
        tree.setBiologicalCycle(BiologicalCycle.SEMFOLHAS);
    }

    @Override
    public void advanceSeason(){
        _tree.setSeason(new Spring(_tree));
        _tree.increaseAge();
    }
}
