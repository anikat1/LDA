import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class LDAModel {

	double alpha = 0.1;
	double beta =  0.01;
	int gibbs_iteration=100;
	int num_docs; //M
	int num_topics; //k user defied parameter
	int vocab_size; //V
	int N; //total no of words in all documents
	
	double[][] theta; //document topic distribution dir(alpha)
	double[][] phi; //topic word distribution dir(beta)
	int[][] docTopicCount; //update count for each doc each topic DXK
	int[][] topicWordCount; //update count of each word appearance for each topic kXV
	int[][] Z;  //topic sampling for each word of each document mult(theta[d])
	
	int[][] docWordID; //from preprocess data identify each word in document 
	                  //as an id which is the position of vocab list
	int[] docLength; //no. of words in each doc; N
	
	public LDAModel(){}
	public LDAModel(int M, int V, int k, int iteration, int[][] wordID, int docLength[]){
		gibbs_iteration=iteration;
		num_docs=M;
		num_topics=k;
		vocab_size=V;
		theta = new double[M][k];
		phi = new double[k][V];
		docTopicCount = new int[M][k];
		topicWordCount = new int[k][V];
		//Arrays.fill(topicWordCount, 0);
		//Arrays.fill(docTopicCount, 0);
		N=0;
		Z = new int[M][];
		for(int i=0;i<M;i++) {
			Z[i] = new int[docLength[i]];
			N+=docLength[i];
		}
		
		this.docLength = docLength;
		docWordID = wordID; 
	}
	public void init(){
		//for each word of each document sample Z, 
		//update doc-topic and topic-word count according to Z value 
		Random r= new Random();
		for(int m=0;m<num_docs;m++){
			for(int n=0;n<docLength[m];n++){
				Z[m][n] = r.nextInt(num_topics)+1;
				// increment topicWord count matrix;
				int topic = Z[m][n];
				int word = docWordID[m][n];
				topicWordCount[topic-1][word-1]++;
				docTopicCount[m][topic-1]++;				
			}
		}
		updateThetaAndPhi();
	}
	
	public void gibbsSampling(){
		for(int i=0;i<gibbs_iteration;i++){
			for(int m=0;m<num_docs;m++){
				for(int n=0;n<docLength[m];n++){
					int old_z = Z[m][n];
					int w = docWordID[m][n];
					//decrement step
					topicWordCount[old_z-1][w-1]--;
					docTopicCount[m][old_z-1]--;
					//assign new z according to gibbs sampling giffiths formula z= P(w|z=j)*P(z=j|d)
					double[] p_z = new double[num_topics];
					//System.out.print("P(z):");
					for(int k=0;k<num_topics;k++){
						double num1 = (double)docTopicCount[m][k]+alpha;  //no times doc m assigned to topic k
						double den1 = (double)find_sum(docTopicCount[m],num_topics) + num_topics*(double)alpha;
						double num2 = (double)topicWordCount[k][w-1]+beta; //no times word n assigned to topic k
						double den2 = (double)find_sum(topicWordCount[k],vocab_size) + vocab_size*(double)beta;
						p_z[k] = (num1/den1)*(num2/den2);
						//System.out.printf("n1 %.2f d1 %.2f n2 %.2f d2 %.2f %.6f\n", num1, den1, num2, den2, p_z[k]);
					}
					//System.out.println();
					//normalize p_z
					double norm = find_norm(p_z,num_topics);
					for(int k=0;k<num_topics;k++) p_z[k]/=norm;
					Multinomial ML = new Multinomial(p_z);
					int new_z = ML.nextSample();
					//System.out.println("sample topic generated "+new_z);
					//update theta, phi value
					Z[m][n]=new_z;
					docTopicCount[m][new_z-1]++;
					topicWordCount[new_z-1][w-1]++;
					
				}
				//System.out.println("iteration:"+i+" doc:"+m+" len:"+docLength[m]);
			}
			System.out.println("iteration "+i);
		}
		updateThetaAndPhi();
	}
	
	public void runLDAModel(List<String>Vocabs){
		init();
		System.out.println("init complete");
		gibbsSampling();
		System.out.println("gibbs Sampling complete");
		//outputDocModel();
		outputWordModel(Vocabs);
	}
	public void updateThetaAndPhi(){
		//initialize normalize theta value
		for(int i=0;i<num_docs;i++){
			int sum = find_sum(docTopicCount[i],num_topics);
			for(int k=0;k<num_topics;k++) theta[i][k]=((double)docTopicCount[i][k]+alpha)/(sum+alpha*num_topics);
		}
	    //initialize normalize phi value
		for(int k=0;k<num_topics;k++){
			int sum = find_sum(topicWordCount[k],vocab_size);
			for(int v=0;v<vocab_size;v++) phi[k][v]=((double)topicWordCount[k][v]+beta)/(sum+beta*vocab_size);
		}
	}
	
	public int find_sum(int[] M, int m){
		int s=0;
		for(int i=0;i<m;i++) s+=M[i];
		return s;
	}
	public double find_norm(double[] M, int m){
		double s=0;
		for(int i=0;i<m;i++) s+=M[i];
		return s;
	}
	
	
	public void outputDocModel(){
		System.out.println("Top 5 topic for each document");
		for(int k=0;k<num_topics;k++){
			System.out.printf("      Topic %d",k+1);
		}
		System.out.println();
		for(int m=0;m<num_docs;m++){
			System.out.printf("doc-%d", m+1);
			for(int k=0;k<num_topics;k++){
				if(k>0) System.out.printf("    ");
				System.out.printf("   %.3f", theta[m][k]);
			}
			System.out.println();
		}
	}
	
	public void outputWordModel(List<String>Vocabs){
		System.out.println("Top 20 words for each topic");
		/*for(int k=0;k<num_topics;k++){
			System.out.printf("       Topic %d",k+1);
		}
		System.out.println();
		for(int v=0;v<vocab_size;v++){
			System.out.printf("%s", Vocabs.get(v));
			for(int k=0;k<num_topics;k++){
				if(k>0) System.out.printf("    ");
				System.out.printf("   %.3f", phi[k][v]);
			}
			System.out.println();
		}*/
		for(int k=0;k<num_topics;k++){
			//Map<Integer, Integer>topWords = new HashMap<Integer, Integer>();
			//Integer[] sortedCount = new Integer[vocab_size];
			ArrayList<TopWordList> tp= new ArrayList<TopWordList>();
			for(int v=0;v<vocab_size;v++){
				tp.add(new TopWordList(Vocabs.get(v), topicWordCount[k][v]));
			}
			Collections.sort(tp);
			//for(int v=0;v<vocab_size;v++) System.out.printf("%.3f,",phi[k][v]);
			System.out.printf("Topic %d:", k+1);
			for(int i=0;i<20;i++) {
			   System.out.printf("%s,",tp.get(i).word);
			}
			System.out.println();
		}
	}
	
	public void printLDAModel(){
		System.out.printf("Topics %d, Words %d, documents %d\n",num_topics,vocab_size,num_docs);
		/*System.out.println("doc length");
		for(int m=0;m<num_docs;m++) System.out.printf("%d ",docLength[m]);
		System.out.println();
		System.out.println("doc-word ID");
		for(int m=0;m<num_docs;m++) {
			for(int n=0;n<docLength[m];n++) System.out.printf("%d,",docWordID[m][n]);
			System.out.println();
		}*/
		System.out.println("doc topic");
		for(int m=0;m<num_docs;m++){
			for(int k=0;k<num_topics;k++) System.out.printf("%d ",docTopicCount[m][k]);
			System.out.println();		
		}
		System.out.println("topic word");
		for(int m=0;m<vocab_size;m++){
			for(int k=0;k<num_topics;k++) System.out.printf("%d ",topicWordCount[k][m]);
			System.out.println();		
		}
	}
}
