/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.Comparator;

/**
 *
 * @author pthnguyen
 */
public class SortForLeadingOnes implements Comparator<Individual> {

private Evaluator e;

public SortForLeadingOnes(Evaluator e){
        this.e = e;
}

@Override
public int compare(Individual o1, Individual o2) {
        // EvaluatorForLeadingOnes e =  new EvaluatorForLeadingOnes();
        return e.eval(o2) - e.eval(o1);
}

// private int eval(Individual o){
//   int los = 0;
//   for (int bit: o.getBitstring()){
//     if (bit == 0) break;
//     los++;
//   }
//   return los;
//     // int fitness = 0;
//     // int indx = 0;
//     // while (indx < o.getN() && o.getBitstring()[indx] != 0){
//     //     fitness++;
//     //     indx++;
//     // }
//     // return fitness;
// }

}
