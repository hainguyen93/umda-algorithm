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

    @Override
    public int compare(Individual o1, Individual o2) {
        return eval(o2) - eval(o1);
    }

    private int eval(Individual o){
        int fitness = 0;
        int indx = 0;
        while (indx < o.getN() && o.getBitstring()[indx] != 0){
            fitness++;
            indx++;
        }
        return fitness;
    }

}
