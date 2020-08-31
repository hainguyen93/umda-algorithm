import java.util.Comparator;
import java.util.Collections;
import java.util.ArrayList;

class Population {

private ArrayList<Individual> array;
private int lambda;  //lambda
private int n;
private int mu;
private Comparator<Individual> cmp;

public Population(Model startModel, int lambda,
                  int mu, int n, Comparator<Individual> cmp){
        this.lambda = lambda;
        this.mu = mu;
        this.n = n;
        this.cmp = cmp;
        array = new ArrayList<Individual>();
        samplePop(startModel);  // sample initial population
}

// get current level
public int getCurrentLevel(){
        int currentLevel = -1;
        int[] numOnes = getNumberOfOnes();
        for (int i=0; i<getN(); i++) {
                if (numOnes[i] != getMU()) break;
                currentLevel++;
        }
        return currentLevel;
}

// get number of ones among mu fittest individuals
public int[] getNumberOfOnes(){
        int[] numOnes = new int[getN()];
        for (int i = 0; i < getMU(); i++) {
                int[] bitstring = getArray().get(i).getBitstring();
                for (int j = 0; j < getN(); j++) {
                        numOnes[j] += bitstring[j];
                }
        }
        return numOnes;
}

public void sortPop(){
        Collections.sort(getArray(), getCMP());
}

public Comparator<Individual> getCMP(){
        return cmp;
}

public int getLambda(){
        return lambda;
}

public int getMU(){
        return mu;
}

public int getN(){
        return n;
}

// public int getCurrentLevel(){
//         return currentLevel;
// }

public ArrayList<Individual> getArray(){
        return array;
}

// get the individual with a particular rank
public Individual getAnIndividual(int rank){
        return getArray().get(rank);
}

public void samplePop(Model model){
        for (int i = 0; i < getLambda(); i++) {
                Individual individual = new Individual(getN());
                individual.sample(model.getVector());
                getArray().add(individual);
        }
}

public void printPop(int size){
        if (size <= 0) size = 0;
        if (size >= getLambda()) size = getLambda();
        for (int i=0; i<size; i++) {
                for (int bit : getArray().get(i).getBitstring()) {
                        System.out.print(bit);
                }
                System.out.println();
        }
        // for (Individual o : population) {
        //         for (int i : o.getBitstring())
        //                 System.out.print(i);
        //         System.out.println();
        // }
}

}
