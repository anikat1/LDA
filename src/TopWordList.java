
public class TopWordList implements Comparable{
	String word;
	int count;
	public TopWordList(){}
	public TopWordList(String s, int c){
		this.word=s;
		this.count =c;
	}
	public void setWord(String s, int c){
		this.word=s;
		this.count =c;
	}
	
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return ((TopWordList)o).count-this.count;
	}
}
