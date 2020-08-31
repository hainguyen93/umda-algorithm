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
class SortForJump implements Comparator<Individual> {

private Evaluator e;

public SortForJump(Evaluator e){
        this.e = e;
}

@Override
public int compare(Individual o1, Individual o2) {
        return e.eval(o2) - e.eval(o1);
}

// private int isXLargerThanY(Individual o1, Individual o2, int start){
//         if (start == o1.getN())
//                 return 0;
//         if (o1.getBitstring()[start] > o2.getBitstring()[start])
//                 return 1;
//         else if (o1.getBitstring()[start] < o2.getBitstring()[start])
//                 return -1;
//         else { //check following bits to skip
//                 return isXLargerThanY(o1, o2, start + 1);
//         }
// }

}
