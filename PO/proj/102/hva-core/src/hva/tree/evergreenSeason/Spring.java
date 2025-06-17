package hva.tree.evergreenSeason;
import hva.tree.BiologicalCycle;
import hva.tree.Evergreen;

public class Spring extends EvergreenSeason{

    public Spring(Evergreen tree) { 
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
