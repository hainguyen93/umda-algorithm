import java.util.Comparator;
import java.util.Random;

/**
 *
 * @author pthnguyen
 */
public class Individual {

private int[] bitstring;
private int n;

public Individual(int n){
        this.n = n;
        bitstring = new int[n];
}

public void sample(double[] p){
        Random rand = new Random();
        for (int i = 0; i < n; i++) {
                double r = rand.nextDouble();
                bitstring[i] = (r <= p[i]) ? 1 : 0;
        }
}

// binval
public int eval(){
        int sum = 0;
        for (int bit : bitstring) {
                sum += bit;
        }
        return sum;
}

// counts the leading ones
public int getLeadingOnes(){
        int res = 0;
        for (int bit: getBitstring()) {
                if (bit == 1) res++;
                else break;
        }
        return res;
}

public int getN(){
        return n;
}

public int[] getBitstring(){
        return bitstring;
}
}
