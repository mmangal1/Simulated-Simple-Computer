package project;

public class Code {
	public static final int CODE_MAX= 2048;
	int[] code = new int[CODE_MAX];
	
	public int getOp(int i){
		return code[2*i];
	}
	
	public int getArg(int i){
		return code[2*i+1];
	}
	
	public void clear(int start, int end){
		for(int x = 2*start; x <= 2*end-1; x++){
			code[x] = 0;
		}
	}
	
	public String getText(int i){
		String s1 = Integer.toHexString(code[2*i]).toUpperCase();
		String s2 = Integer.toHexString(code[2*i+1]).toUpperCase();
		//System.out.println(code[2*i+1]);
		if((code[2*i+1]) < 0){
			//s1 = "-" + Integer.toHexString(-code[2*i]).toUpperCase();
			s2 = "-" + Integer.toHexString(-code[2*i+1]).toUpperCase();
			//System.out.println("hi");
		}
		return s1 + " " + s2;
	}	
	
	public void setCode(int i, int op, int arg){
		code[2*i] = op;
		code[2*i+1] = arg;
	}
	
	public String getHex(int i){
		return Integer.toHexString(code[2*i]).toUpperCase() + " " + Integer.toHexString(code[2*i+1]).toUpperCase();
	}
	
	public String getDecimal(int i){
		return InstructionMap.mnemonics.get(code[2*i]) + " " + code[2*i+1];
	}
}
