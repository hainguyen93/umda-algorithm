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

// argument parser (similar to Python)
import static net.sourceforge.argparse4j.impl.Arguments.storeTrue;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class UMDA {

private int mu;
private int lambda;
private int maxIter;
private int iteration;

private Problem function;
private Model model;
private Population population;

// constructor
public UMDA(int mu, int lambda, Problem function, int maxIter){
        this.function = function;
        this.mu = mu;
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
        getModel().updateModel(getPop());
}

public boolean isStopCondFulfilled(){
        if (getIteration() >= getMaxIter()) {
                System.out.println("Max Iteration Exceeded!");
                return true;
        } else if (getBestFitness() == getOptimalValue()) {
                System.out.println("Optimal Solution Found");
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
        return "UMDA Algorithm Setting:\n\tValue for MU: " + getMU() + " (= const * (function^power))"
               + "\n\tValue for Lambda: " + getLambda() + " (= mu / selPre)"
               + "\n\tSelective Pressure: " + getSelectivePressure();
}

public void sortPop(){
        getPop().sortPop();
}

// print stats for the top k individuals
public void printStats(int topK){
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
        ArgumentParser parser = ArgumentParsers.newFor("java UMDA")
                                .build()
                                .defaultHelp(true)
                                .description("Optimise a pseudo-Boolean function by the UMDA Algorithm. \n" +
                                             "The population size MU is calculated as [const * (function^power)]. \n"+
                                             "The population size LAMBDA then equals [MU / selPres].");

        parser.addArgument("-prob")
        .choices("onemax", "leadingones", "binval", "jump")
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

        parser.addArgument("-c")
        .type(int.class)
        .setDefault(5)
        .dest("const")
        .help("Select the leading constant in the population size MU");

        parser.addArgument("-function")
        .type(String.class)
        .choices("log", "n", "nlog", "sqrtlog")
        .setDefault("n")
        .help("Select the size of the population size MU");

        parser.addArgument("-p")
        .type(double.class)
        .setDefault(0.5)
        .dest("power")
        .help("Select the power in the population size MU");

        parser.addArgument("-selPres")
        .type(double.class)
        .setDefault(0.4)
        .dest("selPre")
        .help("Select the selective pressure");

        parser.addArgument("-filename")
        .type(String.class)
        .dest("filename")
        .help("Select the filename to store the probability vectors");

        parser.addArgument("-maxIter")
        .type(int.class)
        .setDefault(1000)
        .dest("maxIter")
        .help("Select the maximum number of iterations allowed");

        parser.addArgument("-jumpGap")
        .type(int.class)
        .setDefault(10)
        .dest("jumpGap")
        .help("Enter the gap of the Jump function (only if Jump chosen)");

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
public static Problem parseProblemFromArgs(String prob){
        Problem problem = null;
        switch (prob) {
        case "onemax": problem = new OneMax(n); break;
        case "leadingones": problem = new LeadingOnes(n); break;
        case "binval": problem= new BinVal(n); break;
        case "jump": problem = new Jump(n, gapK);
        }
        return problem;
}


// main method
public static void main(String[] args) {

        // parse command-line arguments
        Map attrs = createArgsParser(args);
        System.out.println(attrs.toString());

        int n = (int) attrs.get("n");
        int id = (int) attrs.get("id");
        int c = (int) attrs.get("const");
        double power = (double) attrs.get("power");
        double selPres = (double) attrs.get("selPre");
        int maxIter = (int) attrs.get("maxIter");
        String problemName = String.valueOf(attrs.get("problem"));
        int gapK = (int) attrs.get("jumpGap");
        String func = String.valueOf(attrs.get("function"));

        // optional, output filename
        String filename = (attrs.get("filename") != null)
                          ? String.valueOf(attrs.get("filename"))
                          : null;

        // population sizes
        int mu = estimateMUFromArgs(n, c, power, func);
        int lambda = (int) (mu / selPres);

        // define the pseudo-Boolean function
        Problem problem = parseProblemFromArgs(problemName);

        // instantiating UMDA object
        UMDA umda = new UMDA(mu, lambda, problem, maxIter);

        // print useful info.
        System.out.println(problem.toString());
        System.out.println(umda.toString());

        // start optimising
        umda.optimise(10, filename);

        System.out.printf("%d, %d, %d, %d, %d\n", n, id, iteration*lambda, lambda, iteration);
}
}
