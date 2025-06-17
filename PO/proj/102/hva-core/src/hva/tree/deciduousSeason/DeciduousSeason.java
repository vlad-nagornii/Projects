package hva.tree.deciduousSeason;
import hva.tree.Deciduous;
import java.io.Serializable;

public abstract class DeciduousSeason implements Serializable{
    protected Deciduous _tree;
 
    public DeciduousSeason(Deciduous tree) { _tree = tree; }

    public abstract void advanceSeason();

}
