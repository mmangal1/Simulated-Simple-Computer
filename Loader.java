package project;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Loader {

	public static String load(MachineModel model, File file, int codeOffset, int memoryOffset){
		int codeSize = 0;
		if(model == null || file == null)
			return null;
		try{ 
			Scanner input = new Scanner(file);
			boolean incode = true;
			while(input.hasNextLine()){
				String str = input.nextLine();
				Scanner parser = new Scanner(str);
				//System.out.println(str);
				int opcode = parser.nextInt(16);
				//System.out.println(opcode);
				if(incode && opcode == -1){
					incode = false;
				}
				else if(incode && opcode != -1){
					int arg = parser.nextInt(16);
					//System.out.println("arg = " + arg);
					model.setCode(codeOffset+codeSize, opcode, arg);
					codeSize++;
					///System.out.println(codeSize + " = CodeSize");
				}else{
					int location = parser.nextInt(16);
					//System.out.println(opcode + memoryOffset);
					model.setData(opcode + memoryOffset, location);
				}
				parser.close();
			}
			return "" + codeSize;
			
			
		}catch(FileNotFoundException e){
			return "File " + file.getName() + " Not Found";
		}catch(ArrayIndexOutOfBoundsException e){
			return "Array Index " + e.getMessage();
		}catch(NoSuchElementException e){
			return "From Scanner: NoSuchElementException";
		}
	}
	
	public static void main(String[] args) {
		MachineModel model = new MachineModel();
		String s = Loader.load(model, new File("factorial.pexe"),100,200);
		//System.out.println(s);
		for(int i = 100; i < 100+Integer.parseInt(s); i++) {
			System.out.println(model.getCode().getText(i));			
		}
		//System.out.println(model.getData(200));
		System.out.println(200 + " " + model.getData(200));
		
	}
	
}
