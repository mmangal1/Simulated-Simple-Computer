package project;

import java.util.Map;
import java.util.TreeMap;

public class MachineModel {

	public final Map<Integer, Instruction> INSTRUCTIONS = new TreeMap<>();
	private CPU cpu = new CPU();
	private Memory memory = new Memory();
	private HaltCallback callback;
	Code code = new Code();
	
	Job[] jobs = new Job[2]; 
	private Job currentJob;
	
	public MachineModel() {
		this(() -> System.exit(0));
	}
	
	public MachineModel(HaltCallback x){
		callback = x;
		jobs[0] = new Job();
		jobs[1] = new Job();
		
		currentJob = jobs[0];
		jobs[0].setStartcodeIndex(0);
		jobs[0].setStartmemoryIndex(0);
		jobs[1].setStartcodeIndex(Code.CODE_MAX/4);
		jobs[1].setStartmemoryIndex(Memory.DATA_SIZE/2);
	System.out.println("0 " + jobs[0]);
	System.out.println("1 " + jobs[1]);
		 //INSTRUCTION_MAP entry for "ADDI"
        INSTRUCTIONS.put(0xA, arg -> {
            cpu.setAccumulator(cpu.getAccumulator() + arg);
            cpu.incrementIP();
        });

        //INSTRUCTION_MAP entry for "ADD"
        INSTRUCTIONS.put(0xB, arg -> {
            int arg1 = memory.getData(cpu.getMemoryBase()+arg);
            cpu.setAccumulator(cpu.getAccumulator() + arg1);
            memory.getData(cpu.getMemoryBase() + arg);
            cpu.incrementIP();
        });

        //INSTRUCTION_MAP entry for "ADDN"
        INSTRUCTIONS.put(0xC, arg -> {
            int arg1 = memory.getData(cpu.getMemoryBase()+arg);
            int arg2 = memory.getData(cpu.getMemoryBase()+arg1);
            cpu.setAccumulator(cpu.getAccumulator() + arg2);
            cpu.incrementIP();
        });
        
        //INSTRUCTION_MAP entry for "SUBI"
        INSTRUCTIONS.put(0xD, arg -> {
            cpu.setAccumulator(cpu.getAccumulator() - arg);
            cpu.incrementIP();
        });

        //INSTRUCTION_MAP entry for "SUB"
        INSTRUCTIONS.put(0xE, arg -> {
            int arg1 = memory.getData(cpu.getMemoryBase()+arg);
            cpu.setAccumulator(cpu.getAccumulator() - arg1);
            cpu.incrementIP();
        });

        //INSTRUCTION_MAP entry for "SUBN"
        INSTRUCTIONS.put(0xF, arg -> {
            int arg1 = memory.getData(cpu.getMemoryBase()+arg);
            int arg2 = memory.getData(cpu.getMemoryBase()+arg1);
            cpu.setAccumulator(cpu.getAccumulator() - arg2);
            cpu.incrementIP();
        });
        
        //INSTRUCTION_MAP entry for "MULI"
        INSTRUCTIONS.put(0x10, arg -> {
            cpu.setAccumulator(cpu.getAccumulator() * arg);
            cpu.incrementIP();
        });

        //INSTRUCTION_MAP entry for "MUL"
        INSTRUCTIONS.put(0x11, arg -> {
            int arg1 = memory.getData(cpu.getMemoryBase()+arg);
            cpu.setAccumulator(cpu.getAccumulator() * arg1);
            cpu.incrementIP();
        });

        //INSTRUCTION_MAP entry for "MULN"
        INSTRUCTIONS.put(0x12, arg -> {
            int arg1 = memory.getData(cpu.getMemoryBase()+arg);
            int arg2 = memory.getData(cpu.getMemoryBase()+arg1);
            cpu.setAccumulator(cpu.getAccumulator() * arg2);
            cpu.incrementIP();
        });
        
        //INSTRUCTION_MAP entry for "DIVI"
        INSTRUCTIONS.put(0x13, arg -> {if(arg == 0) throw new DivideByZeroException("Cannot Divide by Zero");
        	else{
            cpu.setAccumulator(cpu.getAccumulator() / arg);
            cpu.incrementIP();
            }
        });

        //INSTRUCTION_MAP entry for "DIV"
        INSTRUCTIONS.put(0x14, arg -> {
            int arg1 = memory.getData(cpu.getMemoryBase()+arg);
            if(arg1 == 0) throw new DivideByZeroException("Cannot Divide by Zero");
            cpu.setAccumulator(cpu.getAccumulator() / arg1);
            cpu.incrementIP();
            
        });

        //INSTRUCTION_MAP entry for "DIVN"
        INSTRUCTIONS.put(0x15, arg -> {
            int arg1 = memory.getData(cpu.getMemoryBase()+arg);
            int arg2 = memory.getData(cpu.getMemoryBase()+arg1);
            if(arg2 == 0) throw new DivideByZeroException("Cannot Divide by Zero");
            cpu.setAccumulator(cpu.getAccumulator() / arg2);
            cpu.incrementIP();
        });
  
        //Instruction 0 is NOP and simply increments the instructionPointer in the CPU
        INSTRUCTIONS.put(0x0, arg -> {cpu.incrementIP();});
        
        //Instruction 1 is LODI, which loads arg into the accumulator of the cpu. Increment instructionPointer.
        INSTRUCTIONS.put(0x1, arg -> {
        	cpu.setAccumulator(arg);
        	cpu.incrementIP();
        });
        
        //Instruction 2 is LOD, which retrieves the value in memory at arg 
        //(as in ADD, arg is an offset from memoryBase) and loads that into the accumulator of the cpu. Increment instructionPointer.
        INSTRUCTIONS.put(0x2, arg -> {
        	int arg1 = memory.getData(cpu.getMemoryBase() + arg);
        	cpu.setAccumulator(arg1);
        	cpu.incrementIP();
        });
        
        //Instruction 3 is LODN, which retrieves the value in memory at arg 
        //(offset from memoryBase) and used that value as the 
        //index to retrieve the value, which is and loaded into the accumulator of the cpu. Increment instructionPointer. 
        INSTRUCTIONS.put(0x3, arg -> {
        	int arg1 = memory.getData(cpu.getMemoryBase() + arg);
        	int arg2 = memory.getData(cpu.getMemoryBase() + arg1);
        	cpu.setAccumulator(arg2);
        	cpu.incrementIP();
        });
        
        //Instruction 4 is STO, 
        //which stores the value in the accumulator into memory at the index arg (offset from memoryBase). Increment instructionPointer. 
        INSTRUCTIONS.put(0x4, arg -> {
        	memory.setData(cpu.getMemoryBase() + arg, cpu.getAccumulator());
        	cpu.incrementIP();
        });
        
        //Instruction 5 is STON, which stores the value in the accumulator into memory at the index (offset from memoryBase) 
        //that is the value stored in memory at the index arg (offset from memoryBase). Increment instructionPointe
        INSTRUCTIONS.put(0x5, arg -> {
        	int arg1 = memory.getData(cpu.getMemoryBase() + arg);
        	memory.setData(cpu.getMemoryBase() + arg1, cpu.getAccumulator());
        	cpu.incrementIP();
        });
        
        //Instruction 6 is JMPI, which does a relative immediate jump. This means that arg is 
        //added to the instructionPointer in the cpu. Note that a negative arg allows for jumping to an earlier point in the program.
        INSTRUCTIONS.put(0x6, arg -> {
        	cpu.setInstructionPointer(cpu.getInstructionPointer() + arg);
        });
        
        //Instruction 7 is JUMP is a similar jump but the value 
        //stored in memory at index arg (offset from memoryBase) is added to the instructionPointer.
        INSTRUCTIONS.put(0x7, arg -> {
        	cpu.setInstructionPointer(memory.getData(cpu.getMemoryBase() + arg) + cpu.getInstructionPointer());
        });
        
        //Instruction 8 is JMZI  does the same as JMPI IF the accumulator is 0. Otherwise, the instructionPointer is incremented.
        INSTRUCTIONS.put(0x8, arg ->{
        	if(cpu.getAccumulator() == 0){
        		cpu.setInstructionPointer(cpu.getInstructionPointer() + arg);
        	}else{
        		cpu.incrementIP();
        	}
        });
        
        //Instruction 9 is JMPZ  does the same as JUMP IF the accumulator is 0. Otherwise, the instructionPointer is incremented
        INSTRUCTIONS.put(0x9, arg -> {
        	if(cpu.getAccumulator() == 0){
        		cpu.setInstructionPointer(memory.getData(cpu.getMemoryBase() + arg) + cpu.getInstructionPointer());
        	}else{
        		cpu.incrementIP();
        	}
        });
        
        //Instruction 0x16 is ANDI: if the accumulator is not zero and arg is not zero, change the accumulator to 1. 
        //In all other situations set the accumulator to 0. Either way, increment instructionPointer. 
        //This relates to the && operation in C: non-zero values can be treated as true and 0 as false. 
        //a && b is only true if both a and b are true.
        INSTRUCTIONS.put(0x16, arg -> {
        	if(cpu.getAccumulator() != 0 && arg != 0){
        		cpu.setAccumulator(1);
        	}else{
        		cpu.setAccumulator(0);
        	}
        	cpu.incrementIP();
        });
        
        //Instruction 0x17 is AND: if the accumulator is not zero and the value in memory at index arg (offset from memoryBase) 
        //is not zero, change the accumulator to 1. In all other situations set the accumulator to 0. Either way, increment instructionPointer.
        INSTRUCTIONS.put(0x17, arg ->{
        	if(cpu.getAccumulator() != 0 && (memory.getData(cpu.getMemoryBase() + arg)) != 0){
        		cpu.setAccumulator(1);
        	}else{
        		cpu.setAccumulator(0);
        	}
        	cpu.incrementIP();
        });
        
        //Instruction 0x18 is NOT: if the accumulator is not zero, set it to 0. If the accumulator is 0 set it to 1. 
        //Again this corresponds to exchanging true and false. Increment instructionPointer.
        INSTRUCTIONS.put(0x18, arg ->{
        	if(cpu.getAccumulator() != 0)
        		cpu.setAccumulator(0);
        	else
        		cpu.setAccumulator(1);
        	cpu.incrementIP();
        });
        
        //Instruction 0x19 is CMPL. If the value in memory at index arg (offset from memoryBase) is strictly negative, 
        //set the accumulator to 1, otherwise set it to 0. 
        //Increment instructionPointer. If the indicated memory value is negative this signals true in the accumulator, otherwise false.
        INSTRUCTIONS.put(0x19, arg ->{
        	if(memory.getData(cpu.getMemoryBase() + arg) < 0)
        		cpu.setAccumulator(1);
        	else
        		cpu.setAccumulator(0);
        	memory.getData(cpu.getMemoryBase() + arg);
        	cpu.incrementIP();
        });
        

	//Instruction 0x1A is CMPZ. If the value in memory at index arg (offset from memoryBase) is zero, 
	//set the accumulator to 1, otherwise set it to 0. 
	//Increment instructionPointer. If the indicated memory value is zero this signals true in the accumulator, otherwise false.
        INSTRUCTIONS.put(0x1A, arg -> {
        	if(memory.getData(cpu.getMemoryBase() + arg) == 0)
        		cpu.setAccumulator(1);
        	else
        		cpu.setAccumulator(0);
        	//memory.getData(cpu.getMemoryBase() + arg);
        	cpu.incrementIP();
        });
	
        INSTRUCTIONS.put(0x1B, arg -> {
        	int target = memory.getData(cpu.getMemoryBase() + arg);
        	cpu.setInstructionPointer(currentJob.getStartcodeIndex()+target);
        });

	//Instruction 0x1F is HALT. The instruction calls halt() and does not increment the instructionPointer
        INSTRUCTIONS.put(0x1F, arg -> {
        	callback.halt();
        });
	
	}
	
	public int getAccumulator() {
		return cpu.getAccumulator();
	}

	public void setAccumulator(int accumulator) {
		cpu.setAccumulator(accumulator);
	}

	public int getInstructionPointer() {
		return cpu.getInstructionPointer();
	}

	public void setInstructionPointer(int instructionPointer) {
		cpu.setInstructionPointer(instructionPointer);
	}

	public int getMemoryBase() {
		return cpu.getMemoryBase();
	}

	public void setMemoryBase(int memoryBase) {
		cpu.setMemoryBase(memoryBase);
	}
//
//	public int[] getArray() {
//		return memory.getArray();
//	}
	void setArray(int[] data) {
		memory.setArray(data);
	}
	
	public int[] getData(){
		return memory.getArray();
	}
	
	public int getData(int index){
		return memory.getData(index);
	}
	
	public void setData(int index, int value){
		memory.setData(index, value);
	}
	
	public Instruction get(int x){
		return INSTRUCTIONS.get(x);
	};
	
	public void setCode(int i, int op, int arg){
		code.setCode(i, op, arg);
	}
	
	public Code getCode(){
		return code;
	}

	public Job getCurrentJob() {
		return currentJob;
	}

	public void setJob(int i) {
		if(i != 0 && i != 1){
			throw new IllegalArgumentException("you entered an illegal argument");
		}
		currentJob.setCurrentAcc(cpu.getAccumulator());
		currentJob.setCurrentIP(cpu.getInstructionPointer());
		
		currentJob = jobs[i];
		cpu.setAccumulator(currentJob.getCurrentAcc());
		cpu.setInstructionPointer(currentJob.getCurrentIP());
		cpu.setMemoryBase(currentJob.getStartmemoryIndex());
		
	}
	
	public int getChangedIndex(){
		return memory.getChangedIndex();
	}
	
	public States getCurrentState(){
		return currentJob.getCurrentState();
	}
	
	public void setCurrentState(States currentState){
		currentJob.setCurrentState(currentState);
	}
	
	public void step(){
		try{
			int ip = cpu.getInstructionPointer();
			if(ip < currentJob.getStartcodeIndex() || ip >= currentJob.getStartcodeIndex()+currentJob.getCodeSize()){
				throw new CodeAccessException("IP is an illegal argument");
			}
			int opcode = code.getOp(ip);
			int arg = code.getArg(ip);
			get(opcode).execute(arg);
			
		}catch(Exception e){
			callback.halt();
			throw e;
		};
	}
	
	public void clearJob(){
		memory.clear(currentJob.getStartmemoryIndex(), currentJob.getStartmemoryIndex()+Memory.DATA_SIZE/2);
		code.clear(currentJob.getStartcodeIndex(), currentJob.getStartcodeIndex()+currentJob.getCodeSize());
		cpu.setAccumulator(0);
		cpu.setInstructionPointer(currentJob.getStartcodeIndex());
		currentJob.reset();
		
	}
	
	
}
