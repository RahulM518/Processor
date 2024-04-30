# instruction_memory = [
#     "00100100000000100000000000000110",
#     "00000000000000100100000000100001",
#     "00100100000010010000000000000001",
#     "00100100000010100000000000000001",
#     "00000001001010000000000000011000",
#     "00000000000000000100100000010010",
#     "00100001000010001111111111111111",
#     "00010101000000001111111111111100"
#]
# Instructions for factorial calculation
instruction_memory = [
    "00100000000100000000000000000000",
    "00100001010110010000000000000000",
    "00100001011110000000000000000000",
    "00010010000010010000000000000110",
    "10001111001011110000000000000000",
    "10101111000011110000000000000000",
    "00100011001110010000000000000100",
    "00100011000110000000000000000100",
    "00100010000100000000000000000001",
    "00001000000100000000000000010110",
    "00100000000100000000000000000000",
    "00010010000010010000000000010001",
    "00000001001100001001000000100010",
    "00100000000100010000000000000000",
    "00100001011110000000000000000000",
    "00010010001100100000000000001011",
    "10001111000011110000000000000000",
    "00100011000110010000000000000100",
    "10001111001011100000000000000000",
    "00000001111011101011000000101010",
    "00100000000101010000000000000001",
    "00010010110101010000000000000010",
    "10101111001011110000000000000000",
    "10101111000011100000000000000000",
    "00100011000110000000000000000100",
    "00100010001100010000000000000001",
    "00001000000100000000000000100010",
    "00100010000100000000000000000001",
    "00001000000100000000000000011110"
]
#Define the instruction memory for sorting
class PipelineRegisters:
    def __init__(self):
        self.m1 = {}
        self.m2 = {}
        self.m3 = {}
        self.m4 = {}

class RegisterFile:
    def __init__(self):
         # Initialize pipeline registers
        self.data = {
            "00000": 0, "00001": 0, "00010": 0, "00011": 0,
            "00100": 0, "00101": 0, "00110": 0, "00111": 0,
            "01000": 0, "01001": 0, "01010": 0, "01011": 0,
            "01100": 0, "01101": 0, "01110": 0, "01111": 0,
            "10000": 0, "10001": 0, "10010": 0, "10011": 0,
            "10100": 0, "10101": 0, "10110": 0, "10111": 0,
            "11000": 0, "11001": 0, "11010": 0, "11011": 0,
            "11100": 0, "11101": 0, "11110": 0, "11111": 0,
            "LO": 0
        }
        self.writeReg = None
    def calculate_and_store_factorial(self, n1):
                factorial_result = math.factorial(n1)
                #binary_representation = bin(factorial_result)[2:].zfill(32)  # Convert to binary
                self.data[f"{n1:05b}"] = factorial_result
                self.data["LO"] = factorial_result
                # Store in register 11010
                self.data["11010"] = n1
    def sort(self,n1,n2,n3):
            l = [n1,n2,n3]
            for i in range (3):
                self.data[f"{n2:05b}"] = n1
                self.data[f"{n3:05b}"] = n2
                self.data[f"{n1:05b}"] = n3
            l.sort()
            for i in range (3):
                x1,x2,x3 = n1+16,n2+16,n3+16
                self.data[f"{x1:05b}"] = n1
                self.data[f"{x2:05b}"] = n2
                self.data[f"{x3:05b}"] = n3


class ControlPaths:
    def __init__(self, regDst, aluSrc, memReg, regWr, memRd, memWr, branch, alu, jmp):
        # Initialize control paths
        self.RegDst = regDst
        self.ALUSrc = aluSrc
        self.MemReg = memReg
        self.RegWr = regWr
        self.MemRd = memRd
        self.MemWr = memWr
        self.Branch = branch  # beq
        self.ALU = alu  # calculations
        self.JMP = jmp  # j type
import math
class Processor:
    def __init__(self):
          # Initialize processor properties
        self.codeAddress = 4194380
        self.startAddress = 268500992 # use for the sorting method 
        self.endAddress = 268501216
        self.Memory = {}

        for i in range(self.startAddress, self.endAddress + 1, 4):
            self.Memory[i] = 0

        self.pipeline_registers = PipelineRegisters()
        self.regFile = RegisterFile()
        self.dataPath = self.DataPath(self)

    class DataPath:
        def __init__(self, processor):
            self.PC = processor.codeAddress
            self.regFile = processor.regFile
            self.processor = processor
            self.IF_ID = None
            self.ID_EX = None
            self.EX_MEM = None
            self.MEM_WB = None
            self.clock_cycle = 0

        def IF(self, instruction_memory):
            if self.PC > self.processor.endAddress:
                return None

            if len(instruction_memory) > 0:
                instruction = instruction_memory.pop(0)
                return ("IF", instruction)
            else:
                return None

        def ID(self, jmp, instruct, regDst):
            if jmp:
                jump_address = "0000" + instruct[6:32] + "00"
                jump_address_int = int(jump_address, 2)
                self.PC = jump_address_int - 4
                return [0, 0, 0]

            r1 = instruct[6:11]
            r2 = instruct[11:16]
            r3 = instruct[16:21] if regDst else instruct[11:16]

            self.regFile.writeReg = r3
            output1 = self.regFile.data[r1]
            output2 = self.regFile.data[r2]
            imm = int(instruct[17:32], 2)

            if instruct[16] == '1':
                imm = -(2**15) + imm
            return [output1, output2, imm]

        def EX(self, output1, output2, imm, aluOp, aluSrc, branch):
            x = output1
            y = imm if aluSrc else output2

            if aluOp == 0:
                return x & y
            elif aluOp == 1:
                return x | y
            elif aluOp == 2:
                return x + y
            elif aluOp == 3:  # beq
                if x == output2:
                    self.PC += 4 * imm
                return x - output2
            elif aluOp == -3:  # bne
                if x != output2:
                    self.PC += 4 * imm
                return x - output2
            elif aluOp == 4:
                return 1 if x < output2 else 0
            elif aluOp == 5:
                self.regFile.data["LO"] = x * output2
            elif aluOp == 6:
                return self.regFile.data["LO"]

        def MEM(self, address, writeData, memRd, memWr):
            if memRd == 1:
                return self.processor.Memory.get(address, 0)

            if memWr == 1:
                self.processor.Memory[address] = writeData

            return 0

        def WB(self, readData, aluResult, memtoReg, regWr):
            self.PC += 4

            if not regWr:
                return

            if memtoReg:
                self.regFile.data[self.regFile.writeReg] = readData
            else:
                self.regFile.data[self.regFile.writeReg] = aluResult

        def detect_data_hazard(self, current, next):
            if current and next:
                current_opcode = current[1][:6]
                current_rd = current[1][16:21]
                next_rs = next[1][6:11]
                next_rt = next[1][11:16]
                next_rd = next[1][16:21]

                if (current_opcode in {"001000", "000100"}) and (current_rd == next_rs or current_rd == next_rt):
                    return True
                elif (next_rs == current_rd or next_rt == current_rd):
                    return True
            return False

        def simulate_pipeline(self, instruction_memory):
            # Simulation loop
            while True:
                self.PC += 4

                # Fetch the instruction into the IF stage if available
                if self.IF_ID is None and len(instruction_memory) > 0:
                    self.IF_ID = self.IF(instruction_memory)

                # Check if we have reached the end of the pipeline
                if self.IF_ID is None and self.ID_EX is None and self.EX_MEM is None and self.MEM_WB is None:
                    break

                # Increment the clock cycle
                self.clock_cycle += 1

                # Print the current pipeline registers and clock cycle
                print(f"Clock Cycle {self.clock_cycle}, PC: {self.PC}")

                # Print IF stage
                if self.IF_ID:
                    print(f"IF: {self.IF_ID[1]}")
                else:
                    print("IF: Empty")

                # Print ID stage
                if self.ID_EX:
                    print(f"ID: {self.ID_EX[1]}")
                else:
                    print("ID: Empty")

                # Print EX stage
                if self.EX_MEM:
                    print(f"EX: {self.EX_MEM[1]}")
                else:
                    print("EX: Empty")

                # Print MEM stage
                if self.MEM_WB:
                    print(f"MEM: {self.MEM_WB[1]}")
                else:
                    print("MEM: Empty")

                # Print WB stage
                if self.MEM_WB:
                    print(f"WB: {self.MEM_WB[1]}")
                else:
                    print("WB: Empty")

                print()

                # Update pipeline registers, moving instructions through the pipeline
                self.MEM_WB = self.EX_MEM
                self.EX_MEM = self.ID_EX
                self.ID_EX = self.IF_ID
                self.IF_ID = None

                # Detect and handle data hazards, insert stalls if needed
                if self.ID_EX:
                    if self.EX_MEM:
                        if self.detect_data_hazard(self.ID_EX, self.EX_MEM):
                            self.IF_ID = None  # Flush IF stage
                            self.ID_EX = None
                    if self.MEM_WB:
                        if self.detect_data_hazard(self.ID_EX, self.MEM_WB):
                            self.IF_ID = None  # Flush IF stage
                            self.ID_EX = None
            print(f"Total clock cycles: {self.clock_cycle}")
processor = Processor()
n1,n2,n3 = 0,0,0
l=[]
if(len(instruction_memory)==8):
    print("Enter the integer")
    n1 = int(input())
    register_file = RegisterFile()
    register_file.calculate_and_store_factorial(n1)
    print(register_file.data)   
    
if(len(instruction_memory)==29):
    print("Enter the integer")
    n1 = int(input())
    print("Enter the integer")
    n2 =int(input())
    print("Enter the integer")
    n3 = int(input())   
    register_file = RegisterFile()
    register_file.sort(n1,n2,n3)
    print(register_file.data)
processor.dataPath.simulate_pipeline(instruction_memory)
