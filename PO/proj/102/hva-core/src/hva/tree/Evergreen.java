package hva.tree;

import hva.Season;
import hva.tree.evergreenSeason.*;

public class Evergreen extends Tree{
    // PERENE 

    private EvergreenSeason _season;

    public Evergreen(String idTree, String name, float age, int difficulty){
        super(idTree, name, age, difficulty);
    }
   
    @Override
    public void setCurrentSeason(Season season){        
        switch (season) {
            case PRIMAVERA -> setSeason(new Spring(this));
            case VERAO -> setSeason(new Summer(this));
            case OUTONO -> setSeason(new Autumn(this));
            case INVERNO -> setSeason(new Winter(this));
        }
    }

    public void setSeason(EvergreenSeason season){
        _season = season;
    }

    @Override
    public void advanceSeason() { 
        _season.advanceSeason();
    }

    @Override
    public String toString(){
        return super.toString() + "|PERENE" + "|" + this.getBiologicalCycle();
    }
}
