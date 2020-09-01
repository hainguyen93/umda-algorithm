class Model {

private double[] vector;
private int size;
// private double smoothParam;

// constructor, PBIL
// public Model(int n, double smoothParam){
//         size = n;
//         this.smoothParam = smoothParam;
//         vector = new double[n];
//         initModel();
// }

// constructor, UMDA
public Model(int n){
        size = n;
        // this.smoothParam = 1.0;
        vector = new double[n];
        initModel();
}


public int getSize(){
        return size;
}

// public double getSmoothParam(){
//         return smoothParam;
// }

public double[] getVector(){
        return vector;
}

public double getModelComponent(int indx){
        return getVector()[indx];
}

public void setModelComponent(int indx, double value){
        getVector()[indx] = value;
}

public void initModel(){
        for (int i = 0; i < getSize(); i++) {
                setModelComponent(i, 0.5);
        }
}

// capping the indx-th marginal
public void capModel(){
        double lower = 1.0 / getSize();  //lower border
        double upper = 1.0 - 1.0 / getSize();  //upper border
        for (int i=0; i<getSize(); i++) {
                if (getModelComponent(i) < lower) {
                        setModelComponent(i, lower);
                } else if (getModelComponent(i) > upper) {
                        setModelComponent(i, upper);
                }
        }
}

// updates the model, PBIL
public void updateModel(Population population, double smoothParam){
        int[] numOnes = population.getNumberOfOnes();
        for (int i = 0; i < getSize(); i++) {
                double newP =  (1.0-smoothParam) * getModelComponent(i)
                              + smoothParam * numOnes[i] / population.getMU();
                setModelComponent(i, newP);
        }
        capModel();
}

// UMDA updates
public void updateModel(Population population){
        int[] numOnes = population.getNumberOfOnes();
        for (int i = 0; i < getSize(); i++) {
                double newP =  1.0 * numOnes[i] / population.getMU();
                setModelComponent(i, newP);
        }
        capModel();
}

// update model, cGA
public void updateModelForCGA(Population population, int k){
        int[] bs1 = population.getAnIndividual(0).getBitstring();
        int[] bs2 = population.getAnIndividual(1).getBitstring();
        for (int i = 0; i< getSize(); i++) {
                if (bs1[i] > bs2[i]) {
                        setModelComponent(i, getModelComponent(i) + 1.0/k);
                } else if (bs1[i] < bs2[i]) {
                        setModelComponent(i, getModelComponent(i) - 1.0/k);
                }
        }
        capModel();
}
}
