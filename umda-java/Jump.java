class Jump extends Problem {
private int gapK;

public Jump(int size, int gapK)
throws NonPositiveProblemSizeException, InvalidJumpGap {
        super("Jump", size, size + 1);
        if ((gapK < 0)||(gapK>=size)) {
                throw new InvalidJumpGap("Jump Gap must be within [0,n-1]!");
        }
        this.gapK = gapK;
        evaluator = new EvaluatorForJump(gapK);
        cmp = new SortForJump(evaluator);
}

public int getGapK(){
        return gapK;
}

public void setGapK(int newGapK){
        gapK = newGapK;
}

public String toString(){
        return super.toString() + "\n\tGap K: " + getGapK();
}
}
