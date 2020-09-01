public class CGA extends EDA {

private int k;  // abstract population size

//constructor
public CGA(int k, Problem function, int maxIter){
        super(2, 2, function, maxIter);  //mu=0, lambda=2
        this.k = k;
}

public int getK(){
        return k;
}

public void updateModel(){
        getModel().updateModelForCGA(getPop(), getK());
}

// public static void main(String[] args)
}
