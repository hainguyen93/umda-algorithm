/*
 * Implementation of the Univariate Marginal Distribution Algorithm in Java
 * @author: Phan Trung Hai Nguyen (Uni. of Birmingham)
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.lang.Exception;

// argument parser (similar to Python)
import static net.sourceforge.argparse4j.impl.Arguments.storeTrue;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class EDA {

protected int mu;
protected int lambda;
protected int maxIter;
protected int iteration;
protected double smoothParam;

protected Problem function;
protected Model model;
protected Population population;

// constructor, EDA
public EDA(int mu, int lambda, Problem function, int maxIter){
        this.function = function;
        this.mu = mu;
        this.smoothParam = 1.0; //max value
        this.lambda = lambda;
        this.maxIter = maxIter;
        init();  // initialising
}

// constructor, PBIL
public EDA(int mu, int lambda, double smoothParam, Problem function, int maxIter){
        this.function = function;
        this.mu = mu;
        this.smoothParam = smoothParam;
        this.lambda = lambda;
        this.maxIter = maxIter;
        init();  // initialising
}

// initialising
public void init(){
        iteration = 1;
        model = new Model(getN()); // uniform
        population = new Population(getModel(), getLambda(),
                                    getMU(), getN(), getCMP());
}

public Problem getFunction(){
        return function;
}

//return the smooth param
public double getSmoothParam(){
        return smoothParam;
}

public void changeSmoothParam(double newValue){
        smoothParam = newValue;
}

// increment the iteration counter
public void nextIteration(){
        iteration++;
}

public Model getModel(){
        return model;
}

public int getMaxIter(){
        return maxIter;
}

public int getLambda(){
        return lambda;
}

public int getIteration(){
        return iteration;
}

public int getOptimalValue(){
        return getFunction().getOptimalValue();
}

public Evaluator getEvaluator(){
        return getFunction().getEvaluator();
}

public int getBestFitness(){
        return getEvaluator().eval(getPop().getAnIndividual(0));
}

// get fitness value for the ind-th individual
public int getFitness(int ind, Evaluator e){
        if (ind < 0) ind = 0; // best
        if (ind > getLambda()) ind = getLambda(); // worst
        return e.eval(getPop().getAnIndividual(ind));
}

public int getBestOM(){
        return (new EvaluatorForOneMax()).eval(getPop().getAnIndividual(0));
}

public int getBestLos(){
        return (new EvaluatorForLeadingOnes()).eval(getPop().getAnIndividual(0));
}

public void samplePop(){
        getPop().samplePop(getModel());
}

// print out the population
public void printPop(int size){
        getPop().printPop(size);
}

// print model and relevant stats to CSV file
public void printToCSV(String filename){
        if (filename == null) return;
        try {
                PrintWriter out = new PrintWriter(
                        new BufferedWriter(
                                new FileWriter(filename, true)));
                StringBuilder sb = new StringBuilder();

                // first iteration
                if (getIteration() == 1) {
                        for (int i = 1; i <= getN(); i++) {
                                sb.append("p" + i);
                                sb.append(",");
                        }
                        sb.append("BestFitness, CurLevel, BestOM, BestLOS\n");
                }

                for (double marginal : getModel().getVector()) {
                        sb.append(String.valueOf(marginal));
                        sb.append(",");
                }

                sb.append(String.valueOf(getBestFitness()));
                sb.append(",");
                sb.append(String.valueOf(getCurrentLevel()));
                sb.append(",");
                sb.append(String.valueOf(getBestOM()));
                sb.append(",");
                sb.append(String.valueOf(getBestLos()));

                out.println(sb.toString());
                out.flush();
                out.close();
        } catch (IOException err) {
                err.printStackTrace();
        }
}

public Population getPop(){
        return population;
}

public int getCurrentLevel(){
        return getPop().getCurrentLevel();
}

// update the model
public void updateModel(){
        // getModel().updateModel(getPop());
        getModel().updateModel(getPop(), getSmoothParam());
}

public boolean isStopCondFulfilled(){
        if (getIteration() >= getMaxIter()) {
                System.out.println("EXIT MESSAGE:\n\tMax Iteration Exceeded!");
                return true;
        } else if (getBestFitness() == getOptimalValue()) {
                System.out.println("EXIT MESSAGE:\n\tOptimal Solution Found");
                return true;
        }
        return false;
}

public int getN(){
        return getFunction().getSize();
}

// get the evaluator from Problem object
public Comparator<Individual> getCMP(){
        return getFunction().getCMP();
}

public int getMU(){
        return mu;
}

public double getSelectivePressure(){
        return 1.0 * getMU() / getLambda();
}

public String toString(){
        return "EDA Algorithm Setting:\n\tValue for MU: " + getMU()
               + " (= const * (function^power))"
               + "\n\tValue for Lambda: " + getLambda()
               + " (= mu / selPre)"
               + "\n\tSelective Pressure: " + getSelectivePressure()
               + "\n\tSmooth Param: "+ getSmoothParam();
}

public void sortPop(){
        getPop().sortPop();
}

// print stats for the top k individuals
public void printStats(int topK){
        if (topK <= 0) return;
        if ((this instanceof CGA) && (topK > 2)) {
                topK = 2;
        }
        if (getIteration() <= 1) {
                System.out.println("\nPROGRESSION:\nIndividual\tBest-Fitness\tBest-OneMax\tBest-LOS");
        }
        System.out.printf("\nIteration #%d\n", getIteration());
        for (int i=0; i< topK; i++) {
                System.out.printf("Individual #%d\t%d\t%d\t%d\n",
                                  i,
                                  getFitness(i, getEvaluator()),
                                  getFitness(i, new EvaluatorForOneMax()),
                                  getFitness(i, new EvaluatorForLeadingOnes()));
        }
}

// optimisation
public void optimise(int num, String filename){
        sortPop();
        while (!isStopCondFulfilled()) {
                printStats(num);
                printToCSV(filename);
                updateModel();
                samplePop();
                sortPop();
                // printPop(mu);
                nextIteration();
        }
}

// static method to parse args
public static Map createArgsParser(String[] args){
        ArgumentParser parser = ArgumentParsers.newFor("java EDA")
                                .build()
                                .defaultHelp(true)
                                .description("Optimise a pseudo-Boolean function by a univariate EDA. \n" +
                                             "For UMDA (and PBIL): "+
                                             "\n\tThe population size MU is calculated as [const * (function^power)]. \n"+
                                             "\tThe population size LAMBDA then equals [MU / selPres]. \n" +
                                             "For cGA:" +
                                             "\n\tThe abstract population size K is calculated as [const * (function^power)]."+
                                             "\n\tThe population size LAMBDA is always two."+
                                             "\n\u00a9 2020 by Phan Trung Hai Nguyen.");

        parser.addArgument("--optimiser", "-optim")
        .type(String.class)
        .choices("umda", "cga")
        .setDefault("umda")
        .dest("optim")
        .help("Select an optimiser. \nThe PBIL algorithm can be chosen by "+
              "selecting the UMDA with a chosen smoothing parameter eta.");

        parser.addArgument("--problem", "-prob")
        .choices("onemax", "leadingones", "binval", "jump", "dlb", "scpath")
        .setDefault("onemax")
        .type(String.class)
        .dest("problem")
        .help("Select a pseudo-Boolean function to optimise");

        parser.addArgument("-n")
        .type(int.class)
        .setDefault(100)
        .dest("n")
        .help("Enter the problem instance size");

        parser.addArgument("-id")
        .type(int.class)
        .setDefault(1)
        .dest("id")
        .help("Select the ID of the run (for parallel computing)");

        parser.addArgument("--const", "-c")
        .type(int.class)
        .setDefault(5)
        .dest("const")
        .help("Select the leading constant in the population size MU (or K)");

        parser.addArgument("--function", "-f")
        .type(String.class)
        .choices("log", "n", "nlog", "sqrtlog")
        .setDefault("n")
        .help("Select the size of the population size MU (or K)");

        parser.addArgument("--power", "-p")
        .type(double.class)
        .setDefault(0.5)
        .dest("power")
        .help("Select the power in the population size MU (or K)");

        parser.addArgument("-eta", "--smoothParam")
        .type(double.class)
        .setDefault(1.0)
        .dest("smoothParam")
        .help("Select a smoothing parameter (only for PBIL, it's 1.0 for UMDA)");

        parser.addArgument("--selPres", "-sp")
        .type(double.class)
        .setDefault(0.4)
        .dest("selPre")
        .help("Select the selective pressure");

        parser.addArgument("--filename", "-fname")
        .type(String.class)
        .dest("filename")
        .help("Select the filename to store the probability vectors");

        parser.addArgument("--maxIter", "-mi")
        .type(int.class)
        .setDefault(1000)
        .dest("maxIter")
        .help("Select the maximum number of iterations allowed");

        parser.addArgument("--jumpGap", "-jg")
        .type(int.class)
        .setDefault(10)
        .dest("jumpGap")
        .help("Enter the gap of the Jump function (only used if Jump is chosen)");

        parser.addArgument("--numPrint", "-np")
        .type(int.class)
        .setDefault(2)
        .dest("numPrint")
        .help("Select the number of fittest individuals to print on console");

        return parser.parseArgsOrFail(args).getAttrs();
}

// calculate the population size MU from command-line args
public static int estimateMUFromArgs(int n, int c, double power,
                                     String function){
        double fBase = 0.0;
        switch (function) {
        case "log": fBase = Math.log(n); break;
        case "n": fBase = n; break;
        case "nlog": fBase = n * Math.log(n); break;
        case "sqrtlog": fBase = Math.sqrt(n) * Math.log(n);
        }
        return (int) (c * Math.pow(fBase, power));
}

// define the pseudo-Boolean function
public static Problem parseProblemFromArgs(String prob, int n, int gapK) throws Exception {
        Problem problem = null;
        switch (prob) {
        case "onemax":
                problem = new OneMax(n);
                break;
        case "leadingones":
                problem = new LeadingOnes(n);
                break;
        case "binval":
                problem= new BinVal(n);
                break;
        case "jump":
                problem = new Jump(n, gapK);
                break;
        case "dlb":
                if (n % 2 != 0) {  //n is odd
                        throw new Exception("DLB requires an even n.");
                }
                problem = new DLB(n);
                break;
        case "scpath":
                problem = new SCPath(n);
        }
        return problem;
}

// print final summary at the end of the run
public static void printSummary(int iteration, int lambda, int maxIter){
        if (iteration >= maxIter) {
                System.out.println("SUMMARY:\n\tThe algorithm COULD NOT find " +
                                   "the optimal solution within the allowed "+
                                   "budget and the chosen parameter settings");
        } else {
                System.out.printf("SUMMARY:\n\tThe algorithm did find the " +
                                  "optimal solution for the chosen problem "+
                                  "after %d iterations. \n\tThe empirical runtime "+
                                  "(in terms of the number of function evaluations) "+
                                  "is %d\n", iteration, iteration*lambda);
        }
}


// main method
public static void main(String[] args) {

        // parse command-line arguments
        Map attrs = createArgsParser(args);
        System.out.println("CONFIGURATIONS:\n\t" + attrs.toString());

        String optim = String.valueOf(attrs.get("optim"));
        int n = (int) attrs.get("n");
        int id = (int) attrs.get("id");
        int c = (int) attrs.get("const");
        double power = (double) attrs.get("power");
        double smoothParam = (double) attrs.get("smoothParam");
        double selPres = (double) attrs.get("selPre");
        int maxIter = (int) attrs.get("maxIter");
        String problemName = String.valueOf(attrs.get("problem"));
        int gapK = (int) attrs.get("jumpGap");
        String func = String.valueOf(attrs.get("function"));
        int numPrint = (int) attrs.get("numPrint");

        // optional, output filename
        String filename = (attrs.get("filename") != null)
                          ? String.valueOf(attrs.get("filename"))
                          : null;

        // population sizes
        int mu = estimateMUFromArgs(n, c, power, func);
        int lambda = (int) (mu / selPres);

        // define the pseudo-Boolean function
        Problem problem = null;
        try {
                problem = parseProblemFromArgs(problemName, n, gapK);
        } catch (Exception e) {
                System.out.println("ERROR: "+ e.getMessage());
                System.exit(0);
        }

        EDA eda = null;
        switch (optim) {
        case "umda":
                eda = new EDA(mu, lambda, smoothParam, problem, maxIter);
                break;
        case "cga":
                eda = new CGA(mu, problem, maxIter);
        }

        // print useful info.
        // System.out.println(problem.toString());
        // System.out.println(eda.toString());

        // start optimising
        eda.optimise(numPrint, filename);

        printSummary(eda.getIteration(), eda.getLambda(), eda.getMaxIter());
        //System.out.printf("%d, %d, %d, %d, %d\n", n, id, iteration*lambda, lambda, iteration);
}
}
