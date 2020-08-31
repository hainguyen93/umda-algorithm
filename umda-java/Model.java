class Model {

private double[] vector;
private int size;

public Model(int n){
        size = n;
        vector = new double[n];
        initModel();
}

public int getSize(){
        return size;
}

public double[] getVector(){
        return vector;
}

public void setModelComponent(int indx, double value){
        getVector()[indx] = value;
}

public void initModel(){
        for (int i = 0; i < getSize(); i++) {
                setModelComponent(i, 0.5);
        }
}

// updates the model
public void updateModel(Population population){
        int[] numOnes = population.getNumberOfOnes();
        double lower = 1.0 / getSize();
        double upper = 1.0 - (1.0 / getSize());
        for (int i = 0; i < getSize(); i++) {
                double newP =  1.0 * numOnes[i] / population.getMU();
                newP = (newP <= lower)
                    ? lower
                    : ((newP >= upper) ? upper : newP);
                setModelComponent(i, newP);
        }
}

}
