class BinVal extends Problem {

public BinVal(int size){
        super("BinVal", size, size);
        evaluator = new EvaluatorForLeadingOnes();
        cmp = new SortForBinVal();
}
}
