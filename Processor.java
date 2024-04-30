// IMT2022002 IMT2022518
import java.util.*;
import java.io.*;

class nPipelineRegisters {
    Map<String, Integer> data = new HashMap<>();
    String writeReg;

    public nPipelineRegisters() {

        data.put("00000", 0);
        data.put("00001", 0);
        data.put("00010", 0);
        data.put("00011", 0);
        data.put("00100", 0);
        data.put("00101", 0);
        data.put("00110", 0);
        data.put("00111", 0);
        data.put("01000", 0);
        data.put("01001", 0);
        data.put("01010", 0);
        data.put("01011", 0);
        data.put("01100", 0);
        data.put("01101", 0);
        data.put("01110", 0);
        data.put("01111", 0);
        data.put("10000", 0);
        data.put("10001", 0);
        data.put("10010", 0);
        data.put("10011", 0);
        data.put("10100", 0);
        data.put("10101", 0);
        data.put("10110", 0);
        data.put("10111", 0);
        data.put("11000", 0);
        data.put("11001", 0);
        data.put("11010", 0);
        data.put("11011", 0);
        data.put("11100", 0);
        data.put("11101", 0);
        data.put("11110", 0);
        data.put("11111", 0);
        data.put("LO", 0);
    }
}

class controlPaths {
    boolean RegDst;
    boolean ALUSrc;
    boolean MemReg;
    boolean RegWr;
    int MemRd;
    int MemWr;
    boolean Branch;
    int ALU;
    boolean JMP;

    public controlPaths(boolean regDst, boolean aluSrc, boolean memReg, boolean regWr, int memRd, int memWr, boolean branch, int alu, boolean jmp) {
        RegDst = regDst;
        ALUSrc = aluSrc;
        MemReg = memReg;
        RegWr = regWr;
        MemRd = memRd;
        MemWr = memWr;
        Branch = branch;
        ALU = alu;
        JMP = jmp;
    }
}


public class Processor {
    static Map<String, String> iOpcode = new HashMap<>();
    static Map<String, String> iFunc = new HashMap<>();

    static {
        iOpcode.put("addi", "001000");
        iOpcode.put("addiu", "001001"); //check
        iOpcode.put("lw", "100011");
        iOpcode.put("sw", "101011");
        iOpcode.put("addu", "000000");
        iOpcode.put("add", "000000");
        iOpcode.put("sub", "000000");
        iOpcode.put("mult", "000000");
        iOpcode.put("mflo", "000000");
        iOpcode.put("beq", "000100");
        iOpcode.put("bne", "000101");
        iOpcode.put("slt", "000000");
        iOpcode.put("j", "000010");

        iFunc.put("addi", null);
        iFunc.put("addiu", null); //check
        iFunc.put("lw", null);
        iFunc.put("sw", null);
        iFunc.put("addu", "100001");
        iFunc.put("sub", "100010");
        iFunc.put("mflo", "010010");
        iFunc.put("mult", "011000");
        iFunc.put("beq", null);
        iFunc.put("bne", null);
        iFunc.put("slt", "101010");
        iFunc.put("add", "100000");
        iFunc.put("and", "100100");
        iFunc.put("or", "100101");
        iFunc.put("j", "001001");
    }

    static controlPaths fetchCP(String instruct) {
        controlPaths cp = new controlPaths(false, false, false, false, 0, 0, false, 0, false);
        if (instruct.substring(0, 6).equals("000000")) {
            cp.RegDst = true;
            cp.RegWr = true;
            cp.ALUSrc = false;
            cp.MemReg = false;
            cp.MemRd = 0;
            cp.MemWr = 0;
            cp.Branch = false;
            if (instruct.substring(26, 32).equals(iFunc.get("slt"))) {
                cp.ALU = 4;
            }else if (instruct.substring(26, 32).equals(iFunc.get("mult"))) {
                cp.ALU = 5;
            }else if (instruct.substring(26, 32).equals(iFunc.get("mflo"))) {
                cp.ALU = 6;
            } else if (instruct.substring(26, 32).equals(iFunc.get("sub"))) {
                cp.ALU = 3;
            } else if (instruct.substring(26, 32).equals(iFunc.get("addu"))) {
                cp.ALU = 2;
            } else if (instruct.substring(26, 32).equals(iFunc.get("add"))) {
                cp.ALU = 2;
            } else if (instruct.substring(26, 32).equals(iFunc.get("or"))) {
                cp.ALU = 1;
            } else if (instruct.substring(26, 32).equals(iFunc.get("and"))) {
                cp.ALU = 0;
            }
            cp.JMP = false;
            return cp;
        } else if (instruct.substring(0, 6).equals(iOpcode.get("addi"))) {
            cp.RegDst = false;
            cp.RegWr = true;
            cp.ALUSrc = true;
            cp.MemReg = false;
            cp.MemRd = 0;
            cp.MemWr = 0;
            cp.Branch = false;
            cp.ALU = 2;
            cp.JMP = false;
            return cp;
        } else if (instruct.substring(0, 6).equals(iOpcode.get("addiu"))) {
            cp.RegDst = false;
            cp.RegWr = true;
            cp.ALUSrc = true;
            cp.MemReg = false;
            cp.MemRd = 0;
            cp.MemWr = 0;
            cp.Branch = false;
            cp.ALU = 2;
            cp.JMP = false;
            return cp;
        } else if (instruct.substring(0, 6).equals(iOpcode.get("lw"))) {
            cp.RegDst = false;
            cp.RegWr = true;
            cp.ALUSrc = true;
            cp.MemReg = true;
            cp.MemRd = 1;
            cp.MemWr = 0;
            cp.Branch = false;
            cp.ALU = 2;
            cp.JMP = false;
            return cp;

        } else if (instruct.substring(0, 6).equals(iOpcode.get("sw"))) {
            cp.RegDst = false;
            cp.RegWr = false;
            cp.ALUSrc = true;
            cp.MemReg = true;
            cp.MemRd = 0;
            cp.MemWr = 1;
            cp.Branch = false;
            cp.ALU = 2;
            cp.JMP = false;
            return cp;
        } else if (instruct.substring(0, 6).equals(iOpcode.get("beq"))) {
            cp.RegDst = false;
            cp.RegWr = false;
            cp.ALUSrc = false;
            cp.MemReg = true;
            cp.MemRd = 0;
            cp.MemWr = 0;
            cp.Branch = true;
            cp.ALU = 3;
            cp.JMP = false;
            return cp;
        } else if (instruct.substring(0, 6).equals(iOpcode.get("bne"))) {
            cp.RegDst = false;
            cp.RegWr = false;
            cp.ALUSrc = false;
            cp.MemReg = true;
            cp.MemRd = 0;
            cp.MemWr = 0;
            cp.Branch = true;
            cp.ALU = -3;
            cp.JMP = false;
            return cp;
        } else if (instruct.substring(0, 6).equals(iOpcode.get("j"))) {
            cp.RegDst = false;
            cp.RegWr = false;
            cp.ALUSrc = false;
            cp.MemReg = false;
            cp.MemRd = 0;
            cp.MemWr = 0;
            cp.Branch = false;
            cp.ALU = 0;
            cp.JMP = true;
            return cp;
        }
        return cp;
    }


    static class DataPath {
        int PC = 4194380;
        nPipelineRegisters registerFile = new nPipelineRegisters();

        String IF(String defaultFile) {
            String[] lines = null;
            try {
                Scanner scanner = new Scanner(new File(defaultFile));
                List<String> listLines = new ArrayList<>();
                while (scanner.hasNextLine()) {
                    listLines.add(scanner.nextLine());
                }
                lines = listLines.toArray(new String[0]);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (lines.length <= (PC - 4194380) / 4) {
                return null;
            }
            return lines[(PC - 4194380) / 4];
        }

        int[] ID(boolean jmp, String instruct, boolean regDst) {
            if (jmp) {
                String jumpAddress = "0000" + instruct.substring(6, 32) + "00";
                int jumpAddressInt = Integer.parseInt(jumpAddress, 2);
                PC = jumpAddressInt - 4;
                return new int[]{0, 0, 0};
            }
            String r1 = instruct.substring(6, 11);
            String r2 = instruct.substring(11, 16);
            String r3;
            String number = instruct.substring(17, 32);
            int imm = Integer.parseInt(number, 2);

            if (instruct.charAt(16) == '1') {
                imm = ~(int)(Math.pow(2, 15) - 1) + imm;
            }
            if (regDst) {
                r3 = instruct.substring(16, 21);
            } else {
                r3 = instruct.substring(11, 16);
            }
            registerFile.writeReg = r3;
            int res1 = registerFile.data.get(r1);
            int res2 = registerFile.data.get(r2);
            return new int[]{res1, res2, imm};
        }

        int EX(int res1, int res2, int imm, int aluOp, boolean aluSrc, boolean branch) {
            int x = res1;
            int y = res2;
            boolean zero = false;
            if (aluSrc) {
                y = imm;
            }
            if (aluOp == 0) {
                return x & y;
            } else if (aluOp == 1) {
                return x | y;
            } else if (aluOp == 2) {
                return x + y;
            } else if (aluOp == 3) {
                if (x == y) {
                    zero = true;
                }

                if (zero && branch) {
                    PC = PC + 4 * imm;
                }
                return x - y;
            } else if (aluOp == -3) {

                if (x != y) {
                    zero = true;
                }

                if (zero && branch) {
                    PC = PC + 4 * imm;
                }
                return x - y;
            } else if (aluOp == 4) {
                return x < y ? 1 : 0;
            } else if (aluOp == 5) {
                registerFile.data.put("LO" , (x* y) );
            } else if(aluOp==6) 
            {
                return registerFile.data.get("LO");
            }
            return 0;
        }

        int MEM(int address, int writeData, int memRd, int memWr) {
            if (memRd == 1) {
                return Memory.get(address);
            }


            if (memWr == 1) {
                Memory.put(address, writeData);
            }

            return 0;
        }

        void WB(int readData, int aluResult, boolean memtoReg, boolean regWr) {
            PC = PC + 4;
            if (!regWr) {
                return;
            }
            if (memtoReg) {
                registerFile.data.put(registerFile.writeReg, readData);
            } else {
                registerFile.data.put(registerFile.writeReg, aluResult);
            }
        }
    }

    static int codeAdd = 4194380; //got from mars
    static int initialAdd = 4;
    static int finalAdd = 112;
    static Map<Integer, Integer> Memory = new HashMap<>();

    static {
        for (int i = initialAdd; i <= finalAdd; i += 4) {
            Memory.put(i, 0);
        }
    }

    public static void main(String[] args) {
        DataPath dp = new DataPath();

        //For sorting code uncomment this: For factorial comment this part
        dp.registerFile.data.put("01001", 3);
        dp.registerFile.data.put("01010", 4);
        dp.registerFile.data.put("01011", 40);
        dp.registerFile.data.put("01011", dp.registerFile.data.get("01011") - 4);
        for (int i = 0; i < dp.registerFile.data.get("01001"); i++) {
            System.out.print("Enter the integer: ");
            Memory.put(dp.registerFile.data.get("01010") + 4 * i, new Scanner(System.in).nextInt());
        }


        //Reading instructions
        boolean instruct = true;

        int cycle = 0;
        while (instruct) {
            // System.out.println(dp.PC); //PC values
            cycle++;
            // machinecode1  = factorial ,  machinecode2 = sorting
            String machineCode = dp.IF("machinecode2.txt");
            if (machineCode == null) {
                break;
            }
            controlPaths cp = fetchCP(machineCode);
            int[] output = dp.ID(cp.JMP, machineCode, cp.RegDst);
            int result = dp.EX(output[0], output[1], output[2], cp.ALU, cp.ALUSrc, cp.Branch);
            int readData = dp.MEM(result, output[1], cp.MemRd, cp.MemWr);
            dp.WB(readData, result, cp.MemReg, cp.RegWr);
        }
        System.out.println("Cycle is: " + cycle);
        System.out.println("Final PC value: " + dp.PC);
        // System.out.println(dp.registerFile.data); //Print this for factorial to see register values
        System.out.println(Memory); //Print this for sorting
    }    
}

// IMT2022002 IMT2022518
