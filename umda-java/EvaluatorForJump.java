// evaluator for jump function
import java.util.stream.*;

class EvaluatorForJump implements Evaluator {

private int k;

public EvaluatorForJump(int k){
        this.k = k;
}

@Override
public int eval(Individual ind){
        int om = IntStream.of(ind.getBitstring()).sum(); //onemax
        if (om == ind.getN()) {
                return ind.getN() + 1;
        } else if (om <= ind.getN() - k) {
                return k + om;
        } else {
                return ind.getN() - om;
        }
}

}
