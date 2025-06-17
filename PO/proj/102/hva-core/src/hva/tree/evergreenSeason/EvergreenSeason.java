package hva.tree.evergreenSeason;
import hva.tree.Evergreen;
import java.io.Serializable;

public abstract class EvergreenSeason implements Serializable{
    protected Evergreen _tree;
 
    public EvergreenSeason(Evergreen tree) { _tree = tree; }

    public abstract void advanceSeason();

}
