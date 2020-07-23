import java.util.stream.*;

class EvaluatorForOneMax implements Evaluator {

@Override
public int eval(Individual ind){
        return IntStream.of(ind.getBitstring()).sum();
}

}
