class OneMax extends Problem {

public OneMax(int size){
        super("OneMax", size, size);
        evaluator = new EvaluatorForOneMax();
        cmp = new SortForOneMax(evaluator);
}
}
