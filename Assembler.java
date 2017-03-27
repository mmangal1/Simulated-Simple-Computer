package project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Assembler {
	
	public static String assemble(File input, File output){
		ArrayList<String> code = new ArrayList<>();
		ArrayList<String> data = new ArrayList<>();
		String returnValue = "Success";
		try{
			Scanner scan = new Scanner(input);
			ArrayList<String> inText = new ArrayList<>();
			
			while(scan.hasNext())
				inText.add(scan.nextLine());
			
			scan.close();
			
			int lineNum = inText.size();
			boolean firstError = false;
			boolean firstError1 = false;
			//Check for Error 1-------------------------------------
			
			for(int x = 0; x < lineNum; x++){
				if(inText.get(x).trim().length() == 0)
					if(inText.get(x+1).trim().length() > 0){
						lineNum = x;
						firstError1 = true;
						returnValue = "Error: line " + (lineNum+1) + " is a blank line";
					}
			}
			
			//System.out.println(returnValue);
			
			//Check for Error 2-------------------------------------		

				for(int x = 0; x < lineNum; x++){
					if(inText.get(x).charAt(0) == (' ') || inText.get(x).charAt(0) == ('\t')){
						lineNum = x;
						firstError1 = true;
						returnValue = "Error: line " + (lineNum+1) + " starts with white space";
					}
				}

			
			//check for Error 3-----------------------------------

				for(int x = 0; x < lineNum; x++){
					if(inText.get(x).trim().toUpperCase().equals("DATA")){
						if(!inText.get(x).equals("DATA")){
							lineNum = x;
							firstError1 = true;
							returnValue = "Error: line " + (lineNum+1) + " does not have DATA in upper case";
						}
					}
				}

			//adding to code arraylist------------------------------------------------------------
			int index = 0;
			while(index < lineNum && !inText.get(index).trim().equals("DATA")){
				code.add(inText.get(index).trim());
				index++;
			}
		System.out.println(code);
			
			//adding to data arraylist---------------------------------------------------------------
			if(!returnValue.equals("Error: line " + lineNum + " does not have DATA in upper case")){
				index++; 
				while(index < lineNum){
					data.add(inText.get(index).trim());
					index++;
				}
			}
	
		System.out.println(data);
			
			
			ArrayList<String> outText = new ArrayList<>();
			for(int x = 0; x < code.size() && firstError == false; x++){
				String[] parts = code.get(x).trim().split("\\s+");
				System.out.println(parts[0]);
				
				
				//Error testing---------------------------------------
				if(InstructionMap.sourceCodes.contains(parts[0].toUpperCase()) && !InstructionMap.sourceCodes.contains(parts[0])){
							firstError = true;
							returnValue = "Error: line " + (x+1) 
								+ " does not have the instruction mnemonic in upper case";
							System.out.println(returnValue);
				}
				else if(InstructionMap.noArgument.contains(parts[0])){
					if(!(parts.length == 1)){
						firstError = true;
						returnValue = "Error: line " + (x+1) + " has an illegal argument";		
						System.out.println(returnValue);
					}else{
						int opcode = InstructionMap.opcode.get(parts[0]);
						outText.add(Integer.toHexString(opcode).toUpperCase() + " 0");
					}
					//System.out.println(returnValue);
				}else if(parts.length == 1){
					firstError = true;
					returnValue = "Error: line " + (x+1) + " is missing an argument";	
					System.out.println(returnValue);
				}else if(parts.length > 2){
					firstError = true;
					returnValue = "Error: line " + (x+1) + " has more than one argument";
					//System.out.println(returnValue);
				}
				//System.out.println(returnValue);
				//System.out.println((parts.length == 1));
				//End of error Testing----------------------------------
				//System.out.println(returnValue);
				else if(parts.length == 2){
					if(parts[0].equals("JUMP") && parts[1].startsWith("#")){
						parts[0] = "JMPI";
						parts[1] = parts[1].substring(1);
						int arg = 0;
						try {
							arg = Integer.parseInt(parts[1],16); //<<<<< CORRECTION
						} catch (NumberFormatException e) {
							firstError = true;
							returnValue = "Error: line " + (x+1)
									+ " does not have a numberic argument";
							//System.out.println(returnValue);
						}
					}
					else if(parts[0].equals("JMPZ") && parts[1].startsWith("#")){
						parts[0] = "JMZI";
						parts[1] = parts[1].substring(1);
						//System.out.println(Arrays.toString(parts));
						int arg = 0;
						try {
							arg = Integer.parseInt(parts[1],16); //<<<<< CORRECTION
						} catch (NumberFormatException e) {
							firstError = true;
							returnValue = "Error: line " + (x+1)
									+ " does not have a numberic argument";
							//System.out.println(returnValue);
						}
					}
					else if(parts[0].equals("JUMP") && parts[1].startsWith("&")){
						parts[0] = "JMPN";
						parts[1] = parts[1].substring(1);
						//System.out.println(Arrays.toString(parts));
						int arg = 0;
						try {
							arg = Integer.parseInt(parts[1],16); //<<<<< CORRECTION
						} catch (NumberFormatException e) {
							firstError = true;
							returnValue = "Error: line " + (x+1)
									+ " does not have a numberic argument";
							//System.out.println(returnValue);
						}
					}
					else if(parts[1].startsWith("#")){
						if(!InstructionMap.immediateOK.contains(parts[0])){
							firstError = true;
							returnValue = "Error: line " + (x+1) + " is an illegal instruction";
							
						}
						parts[1] = parts[1].substring(1);
						parts[0] = parts[0].concat("I");
						int arg = 0; 
						try {
							arg = Integer.parseInt(parts[1],16); //<<<<< CORRECTION
						} catch (NumberFormatException e) {
							firstError = true;
							returnValue = "Error: line " + (x+1)
										+ " does not have a numberic argument";
							//System.out.println(returnValue);
						}
					}
					else if(parts[1].startsWith("&")){
						System.out.println(!InstructionMap.indirectOK.contains(parts[0]));
						if(!InstructionMap.indirectOK.contains(parts[0])){
							firstError = true;
							returnValue = "Error: line " + (x+1) + " is an illegal instruction";
							System.out.println(returnValue);
						}
						parts[1] = parts[1].substring(1);
						parts[0] = parts[0].concat("N");
						int arg = 0;
						try {
							arg = Integer.parseInt(parts[1],16); //<<<<< CORRECTION
						} catch (NumberFormatException e) {
							firstError = true;
							returnValue = "Error: line " + (x+1)
								+ " does not have a numberic argument";
							//System.out.println(returnValue);
						}
					}else if(!InstructionMap.indirectOK.contains(parts[0]) && !InstructionMap.sourceCodes.contains(parts[0]) && !InstructionMap.immediateOK.contains(parts[0]) && !InstructionMap.noArgument.contains(parts[0])){
						firstError = true;
						returnValue = "Error: line " + (x+1) 
							+ " contains unknown mnemonic";
						System.out.println(returnValue);
					}
					//System.out.println(!InstructionMap.indirectOK.contains(parts[0]) && !InstructionMap.sourceCodes.contains(parts[0]) && !InstructionMap.immediateOK.contains(parts[0]) && !InstructionMap.noArgument.contains(parts[0]));
					if(!firstError){
						int opcode = InstructionMap.opcode.get(parts[0]);
						outText.add(Integer.toHexString(opcode).toUpperCase() + " " + parts[1]);
					}
				}
					//else{
//				//System.out.println(parts[0]);
//					int opcode = InstructionMap.opcode.get(parts[0]);
//					if(parts.length == 1){
//						//System.out.println("adding");
//						outText.add(Integer.toHexString(opcode).toUpperCase() + " 0");
//					}
//					if(parts.length == 2){
//						outText.add(Integer.toHexString(opcode).toUpperCase() + " " + parts[1]);
//					}
				}
			//System.out.println(outText);
			outText.add("-1");
			
			for(int x = 0; x < data.size() && firstError == false; x++){
				String[] parts = data.get(x).trim().split("\\s+");
				if(parts.length != 2){
					firstError = true;
					returnValue = "Error: line " + (x+code.size()+2) + " has an illegal number of argument";
					
				}
				int arg = 0; 
				try {
					arg = Integer.parseInt(parts[0],16); //<<<<< CORRECTION
				} catch (NumberFormatException e) {
					firstError = true;
						returnValue = "Error: line " + (x+code.size()+2) 
							+ " does not have a numberic argument";
					
				}
				int arg1 = 0; 
				try {
					arg1 = Integer.parseInt(parts[1],16); //<<<<< CORRECTION
				} catch (NumberFormatException e) {
					firstError = true;
						returnValue = "Error: line " + (x+code.size()+2) 
								+ " does not have a numberic argument";
					
				}
				outText.add(data.get(x));
			}
			//outText.addAll(data);
			System.out.println(outText);
			System.out.println(!firstError && !firstError1);
			if(!firstError && !firstError1){
				PrintWriter out = new PrintWriter(output);
				for(int x = 0; x < outText.size(); x++){
					out.println(outText.get(x));
				}
				out.close();			
			}
			
			
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}
		return returnValue;
	}
	
	public static void main(String[] args){
		File f1 = new File("factorial.pasm");
		File f2 = new File("factorial.pexe");
		Assembler a1 = new Assembler();
		String str = assemble(f1, f2);
		System.out.println(str);
	}
}
