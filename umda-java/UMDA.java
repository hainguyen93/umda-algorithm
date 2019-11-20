/*
 * Implementation of the Univariate Marginal Distribution Algorithm in Java
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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

    public int getCurrentLevel(){
      int[] numOnes = getOnesAmongMuFittest();
      for (int i = 0; i < getN(); i++){
        if (numOnes[i] != getMU()){
          return i+1;
        }
      }
      return getN();
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

    public void printPop(){
        for (Individual o : population){
            for (int i : o.getBitstring())
                System.out.print(i);
            System.out.println();
        }
    }

    public ArrayList<Individual> getPop(){
      return population;
    }

    public int[] getOnesAmongMuFittest(){
      int[] numOnes = new int[getN()];
      for (int i = 0; i < getMU(); i++){
          int[] bitstring = population.get(i).getBitstring();
          for (int j = 0; j < getN(); j++){
              numOnes[j] += bitstring[j];
          }
      }
    }

    public void updateModel(){
      int[] numOnes = getOnesAmongMuFittest();
      for (int i = 0; i < getN(); i++){
            double newMarginal = ((double) numOnes[i]) / getMU();
            if (newMarginal <= 1.0/getN())
              newMarginal = 1.0/getN();
            else if (newMarginal >= 1 - 1.0/getN())
              newMarginal = 1 - 1.0/getN();
            model[i] = newMarginal;
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

    public void printCurrentLevel(int iteration){
      System.out.printf("%d \t %d \n", iteration, getCurrentLevel());
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {


        if (args.length != 6){
            System.out.println("ARGS: N ID Const Power(-1 for log) SelPressure Func");
            System.exit(0);
        }


        // command-line arguments
        int n = Integer.parseInt(args[0]);
        int id = Integer.parseInt(args[1]);
        int c = Integer.parseInt(args[2]);
        double power = Double.parseDouble(args[3]);
        double selPres = Double.parseDouble(args[4]);

        //System.out.printf("%d \t %d \t %d \t %f \t %f \n", n, id, c, power, selPres);

        int mu = 0;

        if (power >= 0){
          mu = c * ((int) Math.pow(n, power));
        } else {
          mu = c * ((int) Math.log(n));
        }

        int lambda = (int) ((int) mu / selPres);

        // configurations

        int opt = 0;
        Comparator<Individual> cmp = null;

        if (args[5].equalsIgnoreCase("onemax")){
            opt = n;
            cmp = new SortForOneMax();
        } else if (args[5].equalsIgnoreCase("leadingones")){
            opt = n;
            cmp = new SortForLeadingOnes();
        } else if (args[5].equalsIgnoreCase("binval")){
            opt = n;
            cmp = new SortForBinVal();
        }

        // run
        UMDA umda = new UMDA(n, mu, lambda, opt);
        umda.sortPop(cmp);
        //umda.printPop();
        int iteration = 1;
        while (!umda.isStopCondFulfilled()){
            umda.printCurrentLevel(iteration);
            //System.out.printf("%d \t %d \n", iteration, umda.getPop().get(0).eval());
            umda.updateModel();
            umda.samplePop();
            umda.sortPop(cmp);
            iteration++;
        }
        //System.out.printf("%d \t %d \t %d\n", n, id, iteration*lambda);
    }
}
