import java.util.Comparator;

class Problem {

protected int size;
protected String name;
protected int optimalValue;
protected Evaluator evaluator;
protected Comparator<Individual> cmp;

//constructor
public Problem(String name, int size, int optimalValue)
throws NonPositiveProblemSizeException {
        if (size <= 0) {
                throw new NonPositiveProblemSizeException("Problem instance "+
                                                          "size must be positive!");
        }
        this.name = name;
        this.size = size;
        this.optimalValue = optimalValue;
        evaluator = null;
        cmp = null;
}

// non-abstract method
public void setName(String newName){
        name = newName;
}

public String getName(){
        return name;
}

public int getSize(){
        return size;
}

public int getOptimalValue(){
        return optimalValue;
}

public Evaluator getEvaluator(){
        return evaluator;
}

public Comparator<Individual> getCMP(){
        return cmp;
}

public String toString(){
        return "Problem Name: " + getName()
               + "\n\tProblem Size: " + getSize()
               + "\n\tOptimal Value: " + getOptimalValue()
               + "\n\tEvaluator: " + getEvaluator().getClass().getName()
               + "\n\tComparator: " + getCMP().getClass().getName();
}
}
