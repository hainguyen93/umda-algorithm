/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umda;

import java.util.Comparator;

/**
 *
 * @author pthnguyen
 */
public class SortForOneMax implements Comparator<Individual>{

    @Override
    public int compare(Individual o1, Individual o2) {
        return eval(o2) - eval(o1);
    }
    
    private int eval(Individual o){
        int sum = 0;
        for (int i : o.getBitstring())
            sum += i;
        return sum;
    }
}
