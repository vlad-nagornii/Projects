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

public interface Visitor<T> {
    public T visit(Animal animal);
    public T visit(Veterinarian veterinarian);
    public T visit(Keeper keeper);
    public T visit(Hotel hotel);
    public T visit(Habitat habitat);
    public T visit(Employee employee);
    public T visit(Vaccination vaccination);
    public T visit(Vaccine vaccine);
    public T visit(Tree tree);
}


