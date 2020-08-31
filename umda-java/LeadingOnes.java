class LeadingOnes extends Problem {

public LeadingOnes(int size){
        super("LeadingOnes", size, size);
        evaluator = new EvaluatorForLeadingOnes();
        cmp = new SortForLeadingOnes(evaluator);
}
}
