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
public class SortForBinVal implements Comparator<Individual>{

    @Override
    public int compare(Individual o1, Individual o2) {
        return isXLargerThanY(o1, o2, 0);    
    }
    
    private int isXLargerThanY(Individual o1, Individual o2, int start){
        if (start == o1.getN())
            return 0;
        if (o1.getBitstring()[start] > o2.getBitstring()[start])
            return 1;
        else if (o1.getBitstring()[start] < o2.getBitstring()[start])
            return -1;
        else { //check following bits to skip
            int delta = 1;
            for (int i = start+1; i < o1.getN(); i++){
                if (o1.getBitstring()[i] == o2.getBitstring()[i]){
                    delta++;
                }
            }
            return isXLargerThanY(o1, o2, start + delta);
        }
            
    }
    
}
