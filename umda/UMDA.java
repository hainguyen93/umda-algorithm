/*
 * Implementation of the Univariate Marginal Distribution Algorithm in Java
 */
package umda;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

/**
 * @author pthnguyen
 */
public class UMDA {
    
    private int n;
    private int mu;
    private int lambda;
    private int opt;
    private ArrayList<Individual> population;
    private double[] model;
    
    public UMDA(int n, int mu, int lambda, int opt){
        this.n = n;
        this.mu = mu;
        this.lambda = lambda;
        this.opt = opt;
        population = new ArrayList<Individual>();
        model = new double[n];
        initModel();
        samplePop();
    }
    
    public void initModel(){
        for (int i = 0; i < getN(); i++){
            model[i] = 0.5;
        }
    }
    
    public double[] getModel(){
        return model;
    }
    
    public int getLambda(){
        return lambda;
    }
       
    public void samplePop(){
        for (int i = 0; i < getLambda(); i++){
            Individual ind = new Individual(n);
            ind.sample(getModel());
            population.add(ind);
        }
    }
    
    public ArrayList<Individual> getPop(){
        return population;
    }
    
    public void updateModel(){
        int[] numOnes = new int[getN()];
        for (int i = 0; i < getMU(); i++){
            int[] bitstring = population.get(i).getBitstring();
            for (int j = 0; j < getN(); j++){
                numOnes[j] += bitstring[j];
            }   
        }
        
        for (int i = 0; i < getN(); i++){
            double newMarginal = ((double) numOnes[i]) / getMU(); 
            model[i] = (newMarginal <= 1.0/n) ? 1.0/n : ((newMarginal >= 1-1.0/n) ? 1-1.0/n : newMarginal);
        }
        
    }
    
    public boolean isStopCondFulfilled(){
        return (population.get(0).eval() == opt);
    }
    
    public int getN(){
        return n;
    }
    
    public int getMU(){
        return mu;
    }
    
    public void sortPop(Comparator<Individual> cmp){
        Collections.sort(population, cmp);
    }
    
   
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        UMDA umda = new UMDA(100, 10, 50, 100);
        Comparator<Individual> cmp = new SortForBinVal();
        umda.sortPop(cmp);
        int iteration = 1;
        while (!umda.isStopCondFulfilled()){
            System.out.printf("%d \t %d \n", iteration, umda.getPop().get(0).eval());
            umda.updateModel();
            umda.samplePop();
            umda.sortPop(cmp);
            iteration++;
        }
        System.out.println(iteration);
    }
}



