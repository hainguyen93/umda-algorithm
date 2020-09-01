class SCPath extends Problem {

public SCPath(int size){
        super("SCPath", size, 2*size);
        evaluator = new EvaluatorForSCPath();
        cmp = new SortForSCPath(evaluator);
}
}
