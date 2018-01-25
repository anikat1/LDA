import java.util.ArrayList;
import java.util.Arrays;

public class LDADemo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/**
		rawdocs <- c('eat turkey on turkey day holiday',
		          'i like to eat cake on holiday',
		          'turkey trot race on thanksgiving holiday',
		          'snail race the turtle',
		          'time travel space race',
		          'movie on thanksgiving',
		          'movie at air and space museum is cool movie',
		          'aspiring movie star')
		*/
		/*String[] tokens ={"eat", "turkey", "day", "holiday",
				          "like", "cake", 
				          "trot", "race", "thanksgiving",
				          "snail", "turtle", "time", "travel", "space",
				          "movie", "air", "museum", "cool", "aspiring", "star", "on"};
		
		ArrayList<String>word = new ArrayList<String>();
		word.addAll(Arrays.asList(tokens));
		int V = word.size();
		int docLength[] ={6,5,6,3,4,3,6,3};
		int wordID[][] ={{1,2,21,2,3,4},{5,1,6,21,4},{2,7,8,21,9,4},{10,8,11},{12,13,14,8},{15,21,9},
				{15,16,14,17,18,15},{19,15,20} //in each document add wordID
		};*/
		int K=15, N=300;
		DataPreprocess dp =new DataPreprocess();
		dp.runPreProcess();
		System.out.println("words:"+dp.vocabSize+" docLen:"+dp.M);
		/*LDAModel L = new LDAModel(dp.M,dp.vocabSize,K,N,dp.docToWordId,dp.docLen);
		L.runLDAModel(dp.word);*/
		//L.printLDAModel();
	}

}
