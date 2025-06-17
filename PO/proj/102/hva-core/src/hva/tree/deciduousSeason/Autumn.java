package hva.tree.deciduousSeason;
import hva.tree.BiologicalCycle;
import hva.tree.Deciduous;

public class Autumn extends DeciduousSeason{

    public Autumn(Deciduous tree) { 
        super(tree);
        tree.setSeasonalEffort(5);
        tree.setBiologicalCycle(BiologicalCycle.LARGARFOLHAS);
     }

    @Override
    public void advanceSeason(){
        _tree.setSeason(new Winter(_tree));
        _tree.increaseAge();
    }
}
