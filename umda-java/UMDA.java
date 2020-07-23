/*
 * Implementation of the Univariate Marginal Distribution Algorithm in Java
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author pthnguyen
 */
public class UMDA {

private int n;
private int mu;
private int lambda;
private int opt;
private int k;      //random variable k
private int currentLevel;      //current level
private ArrayList<Individual> population;
private double[] model;
private Evaluator evaluator;

// constructor
public UMDA(int n, int mu, int lambda, int opt, Evaluator evaluator){
        this.n = n;
        this.mu = mu;
        this.lambda = lambda;
        this.opt = opt;
        this.k = -1;
        this.currentLevel = -1;
        this.evaluator = evaluator;
        population = new ArrayList<Individual>();
        model = new double[n];
        initModel();
        samplePop();
}

// initialise a uniform model
public void initModel(){
        for (int i = 0; i < getN(); i++) {
                model[i] = 0.5;
        }
}

public double[] getModel(){
        return model;
}

public int getLambda(){
        return lambda;
}

public Evaluator getEvaluator(){
        return evaluator;
}

public int getBestFitness(){
        return evaluator.eval(population.get(0));
}

public void samplePop(){
        for (int i = 0; i < getLambda(); i++) {
                Individual ind = new Individual(getN());
                ind.sample(getModel());
                population.add(ind);
        }
}

// print out the population
public void printPop(){
        for (Individual o : population) {
                for (int i : o.getBitstring())
                        System.out.print(i);
                System.out.println();
        }
}

// print model and relevant stats to CSV file
public void printToCSV(String filename){
        try {
                PrintWriter out = new PrintWriter(
                        new BufferedWriter(
                                new FileWriter(filename, true)));
                StringBuilder sb = new StringBuilder();

                // Append strings from array
                for (double marginal : getModel()) {
                        sb.append(String.valueOf(marginal));
                        sb.append(",");
                }
                sb.append(String.valueOf(getBestFitness()));
                sb.append(",");
                sb.append(String.valueOf(getCurrentLevel()));
                sb.append(",");
                sb.append(String.valueOf(getPositionK()));
                sb.append(",");
                sb.append(String.valueOf(getSumK()));

                out.println(sb.toString());
                out.flush();
                out.close();
        } catch (IOException err) {
                err.printStackTrace();
        }
}

public ArrayList<Individual> getPop(){
        return population;
}

public int getCurrentLevel(){
        return currentLevel;
}

public int getPositionK(){
        return k;
}

public void updateModel(){
        currentLevel = -1;
        k = -1;

        int[] numOnes = new int[getN()];
        for (int i = 0; i < getMU(); i++) {
                int[] bitstring = population.get(i).getBitstring();
                for (int j = 0; j < getN(); j++) {
                        numOnes[j] += bitstring[j];
                }
        }

        // get the current level
        for (int i=0; i<getN(); i++) {
                if (numOnes[i] != getMU()) break;
                currentLevel++;
        }

        //get the k value
        k = calculateK(currentLevel+1);

        for (int i = 0; i < getN(); i++) {
                double newMarginal = ((double) numOnes[i]) / getMU();
                model[i] = (newMarginal <= 1.0/n) ? 1.0/n : ((newMarginal >= 1-1.0/n) ? 1-1.0/n : newMarginal);
        }
}

// calculate the value of K
public int calculateK(int startIndex){
        int sum = 0;
        int indx = startIndex;
        while ((indx < getN()) && (sum < getMU())) {
                for (int i=sum; i<getMU(); i++) {
                        if (population.get(i).getBitstring()[indx] != 1) break;
                        sum++;
                }
                indx++;
        }
        return (sum >= getMU()) ? indx-1 : getN();
}


public boolean isStopCondFulfilled(){
        return (getBestFitness() == opt);
}

public int getN(){
        return n;
}

public int getMU(){
        return mu;
}

// return the sum of K-1 marginals
public double getSumK(){
        double sumK = 0;
        for (int i = currentLevel+1; i < k; i++) {
                sumK += model[i];
        }
        return sumK;
}

public void sortPop(Comparator<Individual> cmp){
        Collections.sort(population, cmp);
}

public void printStats(){
        System.out.printf("\t %d  \t %d \t %d \t %f \n",
                          getBestFitness(), getCurrentLevel(),
                          getPositionK(), getSumK());

}
/**
 * @param args the command line arguments
 */
public static void main(String[] args) {


        if (args.length != 7) {
                System.out.println("ARGS: N ID Const Power(-1 for log) SelPressure Func filename");
                System.exit(0);
        }

        // command-line arguments
        int n = Integer.parseInt(args[0]);
        int id = Integer.parseInt(args[1]);
        int c = Integer.parseInt(args[2]);
        double power = Double.parseDouble(args[3]);
        double selPres = Double.parseDouble(args[4]);
        String filename = args[6];

        int mu = 0;

        if (power >= 0) {
                mu = c * ((int) Math.pow(n, power));
        } else {
                mu = c * ((int) Math.log(n));
        }

        int lambda = (int) ((int) mu / selPres);

        // configurations

        int opt = 0;
        Comparator<Individual> cmp = null;
        Evaluator evaluator = null;

        if (args[5].equalsIgnoreCase("onemax")) {
                opt = n;
                cmp = new SortForOneMax();
                evaluator = new EvaluatorForOneMax();
        } else if (args[5].equalsIgnoreCase("leadingones")) {
                opt = n;
                cmp = new SortForLeadingOnes();
                evaluator = new EvaluatorForLeadingOnes();
        } else if (args[5].equalsIgnoreCase("binval")) {
                opt = n;
                cmp = new SortForBinVal();
                evaluator = new EvaluatorForLeadingOnes();  //use leadingones for now
        }

        // run
        UMDA umda = new UMDA(n, mu, lambda, opt, evaluator);
        umda.sortPop(cmp);
        //umda.printPop();
        int iteration = 1;
        while (!umda.isStopCondFulfilled()) {
                System.out.print(iteration);
                umda.printStats();
                umda.printToCSV(filename);
                umda.updateModel();
                umda.samplePop();
                umda.sortPop(cmp);
                iteration++;
        }
        System.out.printf("%d \t %d \t %d\n", n, id, iteration*lambda);
}
}
