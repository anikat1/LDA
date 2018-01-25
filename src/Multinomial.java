import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Multinomial {
	double[] prob;
	int prob_sz;
	private Map<Integer, Integer> X;
	public Multinomial(){X = new HashMap<Integer, Integer>();}
	public Multinomial(double[] p){
		prob_sz = p.length;
		prob = p;
		double[] actual_prob=new double[prob_sz];
		for(int i=0;i<prob_sz;i++) actual_prob[i]=p[i];
		X =  new HashMap<Integer, Integer>();
		Arrays.sort(prob);
		for(int i=0;i<prob_sz;i++) {
			for(int j=0;j<prob_sz;j++){
				if(Math.abs(prob[i]-actual_prob[j])<1e-7){
					X.put(i,j+1);
					break;
				}
			}
		}
	}
	
	public int nextSample(){
		double rand = (double) Math.random();
		//System.out.println("rand value "+rand);
		double temp=0;
		/*System.out.println("Multinomial Prob");
	    for(int i=0;i<prob_sz;i++) System.out.printf(" %d  %.6f,", X.get(i),prob[i]);*/
	    //System.out.println();
		for(int i=0;i<prob_sz;i++){
			temp+=prob[i];
			if(rand<=temp){
				int val = X.get(i);
				return val;
			}
		}
		return 0;
	}
}
