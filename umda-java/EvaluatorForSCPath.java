class EvaluatorForSCPath implements Evaluator {

public EvaluatorForSCPath(){
}

@Override
public int eval(Individual o){
        int om = (new EvaluatorForOneMax()).eval(o);
        int zm = o.getN() - om;
        int los = (new EvaluatorForLeadingOnes()).eval(o);
        if (om == o.getN()) {
                return 2*o.getN();
        } else if (los == om) {
                return o.getN();
        }else {
                return zm;
        }

        // int phi = getLeadingBlocks(o.getBitstring());
        // int start = 2 * phi;
        // int end = 2 * phi + 1;
        // if (phi==o.getN()/2) {  // optimal
        //         return o.getN();
        // } else if (o.getSumOfBits(start, end)==0) {
        //         return 2 * phi + 1;
        // } else if (o.getSumOfBits(start, end)==1) {
        //         return 2 * phi;
        // }
        // return start;
}

// // count the number of leading 11-blocks
// int getLeadingBlocks(int[] bs){
//         int numBlocks = 0;
//         for (int i=0; i<bs.length-1; i+=2) {
//                 if ((bs[i]!=1)||(bs[i+1]!=1)) break;
//                 numBlocks++;
//         }
//         return numBlocks;
// }
}
