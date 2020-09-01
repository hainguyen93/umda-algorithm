/**
 * Deceiving Leading Blocks
 **/
class DLB extends Problem {

public DLB(int size){
        super("DLB", size, size);
        evaluator = new EvaluatorForDLB();
        cmp = new SortForDLB(evaluator);
}
}
