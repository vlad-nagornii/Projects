package hva.visitor;

import hva.Hotel;
import hva.animal.Animal;
import hva.employee.Employee;
import hva.employee.Keeper;
import hva.employee.Veterinarian;
import hva.habitat.Habitat;
import hva.tree.Tree;
import hva.vaccine.Vaccination;
import hva.vaccine.Vaccine;
import java.io.Serializable;

public class RenderEntity implements Visitor<String>, Serializable{
    @Override
    public String visit(Animal animal){
        return animal.toString();
    }
    @Override
    public String visit(Habitat habitat){
        return habitat.toString();
    } 
    @Override 
    public String visit(Veterinarian veterinarian){
        return veterinarian.toString();
    }
    @Override
    public String visit(Keeper keeper){
        return keeper.toString();
    }
    @Override
    public String visit(Hotel hotel){
        return hotel.toString();
    }
    @Override
    public String visit(Vaccination vaccination){
        return vaccination.toString();
    }
    @Override
    public String visit(Employee employee){
        return employee.toString();
    }
    @Override
    public String visit(Vaccine vaccine){
        return vaccine.toString();
    }

    @Override
    public String visit(Tree tree){
        return tree.toString();
    }
}

