import java.util.Comparator;

class SortForDLB implements Comparator<Individual> {
private Evaluator e;

public SortForDLB(Evaluator e){
        this.e = e;
}

@Override
public int compare(Individual o1, Individual o2){
        return e.eval(o2) - e.eval(o1);
}

}
