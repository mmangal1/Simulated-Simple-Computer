package project;

public class Memory {
	public static final int DATA_SIZE = 2048;
	private int[] data = new int[DATA_SIZE];
	private int changedIndex = -1;
	
	
	
	int[] getArray() {
		return data;
	}
	void setArray(int[] data) {
		this.data = data;
	}
	
	public int getData(int index){
		return data[index];
	}
	
	public void setData(int index, int value){
		data[index] = value;
		changedIndex = index;
	}
	
	public int getChangedIndex() {
		return changedIndex;
	}
	
	void clear(int start, int end){
		for(int x = start; x < end; x++){
			data[x] = 0;
		}
		changedIndex = -1;
	}
	

	
	
}
