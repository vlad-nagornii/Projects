package hva.tree;

import hva.Season;
import java.io.Serial;
import java.io.Serializable;

import java.lang.Math;


public abstract class Tree implements Serializable{
    
    /** Class serial number. */
    @Serial
    private static final long serialVersionUID = 202407081733L;

    /** Tree's Id. */
    private final String _ID_TREE;

    /** Tree's name. */
    private String _name;

    /** Tree's age */
    private float _age;

    /** Tree's base cleaning difficulty. */
    private int _difficulty = 0;

    /** Tree's seasonal effort. */ 
    private int _seasonalEffort;

    /** Tree's biological cycle. */
    private BiologicalCycle _biologicalCycle;

    /** 
     * Constructor.
     */
    public Tree(String idTree, String name, float age, int difficulty){
        _ID_TREE = idTree;
        _name = name;
        _age = age;
        _difficulty = difficulty;

    }
    
    public String getId(){
        return _ID_TREE;
    }

    public int getDificulty(){
        return _difficulty;
    }

    public BiologicalCycle getBiologicalCycle(){
        return _biologicalCycle;
    }

    /** 
     * Setter.
     * 
     * @param BiologicalCycle biologicalCycle to be set.
     */
    public void setBiologicalCycle(BiologicalCycle biologicalCycle){
        _biologicalCycle = biologicalCycle;
    }

    /**
     * Setter.
     * 
     * @param int seasonalEffort to be set.
     */
    public void setSeasonalEffort(int seasonalEffort){
        _seasonalEffort = seasonalEffort;
    }

    public double cleaningEffort(){
        return _difficulty * _seasonalEffort * Math.log((int) _age + 1);
    }

    public void increaseAge(){
        _age += 0.25;
    }

    public abstract void setCurrentSeason(Season Season);
    public abstract void advanceSeason();

    @Override
    public String toString(){
        return "ÁRVORE|" + _ID_TREE + "|" + _name + "|" + (int) _age + "|" + _difficulty;
    }

}