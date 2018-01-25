import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class DataPreprocess {
	
	Map<String, Integer> wordSet;
	List<String>word;
	List<String> stopWordList;
	int vocabSize;
	int[][] docToWordId;
	int[] docLen;
	int M; //# documents
	public DataPreprocess(){
		stopWordList= new ArrayList<String>();
		word = new ArrayList<String>();
		wordSet = new HashMap<String, Integer>();
		vocabSize=0;
		initStopWords();
	}
	
	public void initStopWords(){
		String fileName = "Data/stopwordlist.txt";
		String line= null;
		try {
			FileReader fileReader = new FileReader(fileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			try{
				//System.out.println("Stop Words:");
				while((line = bufferedReader.readLine()) != null){
					//System.out.println(line);
					stopWordList.add(line);
				}
			}catch (IOException e) {
				System.out.println( "Error reading file '" + fileName + "'");  
			}   
		}catch (FileNotFoundException e) {
			System.out.println("Unable to open file '" + fileName + "'");
		}
	}
	
	private boolean isStopWord(String stop){
		for(String s:stopWordList){
			if(s.equals(stop)) return true;
		}
		return false;
	}
	public boolean isNumeric(String str)
	{
	  return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}
	
	public List<Integer> tokenizeDocuments(String fileName){
		String line= null;
		List<Integer>docToWord = new ArrayList<Integer>();
		try {
			FileReader fileReader = new FileReader("Data/business/"+fileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			try{
				//System.out.println("Stop Words:");
				//System.out.println("Document tokenized:");
				while((line = bufferedReader.readLine()) != null){
					//System.out.println(line);
					StringTokenizer st = new StringTokenizer(line," \t\n\r\f,.:;?![]'-%$£\"()");
					while(st.hasMoreTokens()){
						String token= st.nextToken().toLowerCase();
						if(!isStopWord(token) && !isNumeric(token) ){
							if(!wordSet.containsKey(token)){
								vocabSize++;
								word.add(token);
								wordSet.put(token, vocabSize);
							}
							docToWord.add(wordSet.get(token));
							//System.out.printf("%s,",token);
							//add vocab to map and list, also convert document word to wordID
						}
					}
				}
				//System.out.println();
			}catch (IOException e) {
				System.out.println( "Error reading file '" + fileName + "'");  
			}   
		}catch (FileNotFoundException e) {
			System.out.println("Unable to open file '" + fileName + "'");
		}
		return docToWord;
		
	}
	
	public void runPreProcess(){
		File folder = new File("Data/business");
		File[] listOfFiles = folder.listFiles();
		M= listOfFiles.length;
		//M=100;
		System.out.println("stop word list: "+stopWordList.size());
		System.out.println("no of files in folder: "+M);
		docLen = new int[M];
		docToWordId = new int[M][]; 
		try {
			PrintWriter doc = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream("corpus"+".txt")));
			for (int i = 0; i < M; i++) { //listOfFiles.length
			      if (listOfFiles[i].isFile()) {
			        //System.out.println("File " + listOfFiles[i].getName());
			        List<Integer> l = tokenizeDocuments(listOfFiles[i].getName());
			        docLen[i]=l.size();
			        docToWordId[i] = new int[l.size()];
			        //System.out.println("doc "+i+":"+l.size());
			        for(int j=0;j<l.size();j++) {
			        	docToWordId[i][j]= l.get(j);
			        	if(j==0) System.out.printf("doc %d, ",i);
			        	doc.write(docToWordId[i][j]+" ");
			        }
			        
			        doc.write("\n");
			       // System.out.println();
			      }
			}
			doc.close();
			System.out.println();
			printVocabs();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("docToWord file can not create");
		}catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("doc to word ID can not be converted");
		}
		
		
		
	}
	
	public void printVocabs(){
		try {
			PrintWriter output = new PrintWriter(new OutputStreamWriter(
				    new FileOutputStream("bbc_vocabs"+".txt")));
			for(String s:word){
				//System.out.printf("%s\n", s);
				output.write(s);
				output.write("\n");
				
			}
			output.close();
			System.out.println("Vocabularies created");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("output file not created");
			//e.printStackTrace();
		}
	}
}
