package hva.tree.deciduousSeason;
import hva.tree.BiologicalCycle;
import hva.tree.Deciduous;


public class Spring extends DeciduousSeason{

    public Spring(Deciduous tree) { 
        super(tree); 
        tree.setSeasonalEffort(1); 
        tree.setBiologicalCycle(BiologicalCycle.GERARFOLHAS);
    }
    
    @Override
    public void advanceSeason(){
        _tree.setSeason(new Summer(_tree));
        _tree.increaseAge();
    }
}
