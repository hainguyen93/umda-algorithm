import java.util.stream.*;

class EvaluatorForLeadingOnes implements Evaluator {

@Override
public int eval(Individual ind){
        int los = 0;
        for (int bit: ind.getBitstring()) {
                if (bit == 0) break;
                los++;
        }
        return los;
}

}
