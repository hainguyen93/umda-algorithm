/**
 * Deceiving Leading Blocks
 **/
class DLB extends Problem {

public DLB(int size)
throws ProblemSizeNotEvenForDLB, NonPositiveProblemSizeException {
        super("DLB", size, size);
        if (size % 2 != 0) {
                throw new ProblemSizeNotEvenForDLB("Problem instance size must "
                                                   +"be even for DLB Problem!");
        }
        evaluator = new EvaluatorForDLB();
        cmp = new SortForDLB(evaluator);
}
}
