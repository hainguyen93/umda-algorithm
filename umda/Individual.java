/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umda;

import java.util.Comparator;
import java.util.Random;

/**
 *
 * @author pthnguyen
 */
public class Individual{
    
    private int[] bitstring;
    private int n;
    
    public Individual(int n){
        this.n = n;
        bitstring = new int[n];
    }
    
    public void sample(double[] p){
        Random rand = new Random();
        for (int i = 0; i < n; i++){
            double r = rand.nextDouble();
            bitstring[i] = (r <= p[i]) ? 1 : 0; 
        }
    }
    
    // onemax
    public int eval(){
        int sum = 0;
        for (int bit : bitstring){
            sum += bit;
        }
        return sum;
    }
    
    public int getN(){
        return n;
    }
    
    public int[] getBitstring(){
        return bitstring;
    }
}
