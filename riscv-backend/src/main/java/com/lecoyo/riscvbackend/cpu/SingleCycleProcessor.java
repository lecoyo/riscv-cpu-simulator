package com.lecoyo.riscvbackend.cpu;

import com.lecoyo.riscvbackend.cpu.components.*;
import com.lecoyo.riscvbackend.cpu.controlunit.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Simulates a single-cycle RISC-V processor.
 */
public class SingleCycleProcessor {
    private final InstructionMemory instructionMemory;
    private final DataMemory dataMemory;
    private final ControlUnit controlUnit;
    private final RegisterFile registerFile;
    private final Extend extend;
    private final ALU alu;

    private static final int MAX_CYCLES = 10000;

    // program counter as byte address
    private int pc = 0;

    private record DecodeResult(byte op, byte rd, byte funct3, byte rs1, byte rs2, byte funct7, int rawImmediate) {}
    private record ExecuteResult(int aluResult, int rd1, int rd2, int immExt) {}

    public record CpuSnapshot(
            int pc,
            int instruction,
            ControlSignals controlSignals,
            int[] registers,
            int aluResult,
            int immExt,
            boolean memWrite,
            String instructionType
    ) {}

    /**
     * Creates a processor for the given program.
     *
     * @param instructions the program as bytes
     * @param memorySize   size of the data memory in bytes
     */
    public SingleCycleProcessor(byte[] instructions, int memorySize) {
        this.instructionMemory = new InstructionMemory(instructions);
        this.dataMemory = new DataMemory(memorySize);
        this.controlUnit = new ControlUnit(new MainControlUnit(), new ALUControl());
        this.registerFile = new RegisterFile();
        // stack pointer (x2) starts in the middle of the data memory
        this.registerFile.write(2, memorySize/2, true);
        this.extend = new Extend();
        this.alu = new ALU();
    }

    /**
     * Runs the whole program.
     *
     * @return one snapshot per executed instruction
     * @throws IllegalArgumentException if the cycle limit is exceeded
     */
    public List<CpuSnapshot> simulate() {
        List<CpuSnapshot> trace = new ArrayList<>();
        int cycles = 0;

        while (pc < instructionMemory.getProgramSize()) {
            if(cycles > MAX_CYCLES)
                throw new IllegalArgumentException("Too many cycles, simulation stopped.");
            trace.add(step());
            cycles += 1;
        }

        return trace;
    }

    /**
     * Executes one instruction.
     * For branches the control signals are resolved twice: first without a branch decision
     * (needed to run execute), then with the evaluated condition so that {@code pcSrc} is correct.
     *
     * @return the snapshot after this instruction
     */
    private CpuSnapshot step() {
        int pcBefore = pc;
        int instruction = fetch();
        DecodeResult decodeResult = decode(instruction);

        ControlSignals controlSignals = controlUnit.operate(
                decodeResult.op(),
                decodeResult.funct3(),
                decodeResult.funct7(),
                false
        );

        ExecuteResult executeResult = execute(decodeResult, controlSignals);

        if (controlSignals.getInstructionType() == InstructionFormat.B_TYPE) {
            controlSignals = controlUnit.operate(
                    decodeResult.op(),
                    decodeResult.funct3(),
                    decodeResult.funct7(),
                    isZeroFlag(executeResult, decodeResult)
            );
        }

        int readData = memory(decodeResult, executeResult, controlSignals);
        writeBack(readData, decodeResult, executeResult, controlSignals);
        pcUpdate(executeResult, controlSignals);

        return new CpuSnapshot(
                pcBefore,
                instruction,
                controlSignals,
                registerFile.getRegisters().clone(),
                executeResult.aluResult(),
                executeResult.immExt(),
                controlSignals.isMemWrite(),
                getInstructionType(controlSignals)
        );
    }

    /**
     * Maps the decoded instruction type to its name.
     *
     * @param controlSignals the control signals holding the instruction type
     * @return the type name, e.g. "ILoad", "IAlu", "ULui", "S", "R", "B" or "J"
     */
    private String getInstructionType(ControlSignals controlSignals) {
        return switch (controlSignals.getInstructionType()) {
            case I_TYPE_LOAD -> "ILoad";
            case I_TYPE_ALU -> "IAlu";
            case I_TYPE_JALR -> "IJalr";
            case U_TYPE_LUI -> "ULui";
            case U_TYPE_AUIPC -> "UAuipc";
            case S_TYPE -> "S";
            case R_TYPE -> "R";
            case B_TYPE -> "B";
            case J_TYPE -> "J";
        };
    }

    /**
     * Evaluates the branch condition by comparing the two register values.
     *
     * @param executeResult provides the register values
     * @param decodeResult provides {@code funct3} to select the comparison
     * @return {@code true} if the branch condition is met
     * @throws IllegalArgumentException if {@code funct3} is not a known branch
     */
    private boolean isZeroFlag(ExecuteResult executeResult, DecodeResult decodeResult) {
        int rd1 = executeResult.rd1();
        int rd2 = executeResult.rd2();

        return switch (decodeResult.funct3()) {
            case 0b000 -> rd1 == rd2; // BEQ
            case 0b001 -> rd1 != rd2; // BNE
            case 0b100 -> rd1 < rd2; // BLT
            case 0b101 -> rd1 >= rd2; // BGE
            case 0b110 -> Integer.compareUnsigned(rd1, rd2) < 0; // BLTU
            case 0b111 -> Integer.compareUnsigned(rd1, rd2) >= 0; // BGEU
            default -> throw new IllegalArgumentException("Unknown funct3 for B-Type: " + decodeResult.funct3());
        };
    }

    /**
     * Reads the instruction at the current pc.
     *
     * @return the 32-bit instruction
     */
    private int fetch() {
        return instructionMemory.read(pc);
    }

    /**
     * Splits the instruction into its bit fields.
     *
     * @param instruction the 32-bit instruction
     * @return the extracted fields
     */
    private DecodeResult decode(int instruction) {
        return new DecodeResult(
                (byte) (instruction & 0b1111111), // 6:0
                (byte) ((instruction >>> 7) & 0b11111), // 11:7
                (byte) ((instruction >>> 12) & 0b111), // 14:12
                (byte) ((instruction >>> 15) & 0b11111), // 19:15
                (byte) ((instruction >>> 20) & 0b11111), // 24:20
                (byte) (instruction >>> 25), // 31:25
                (instruction >>> 7) // 31:7
        );
    }

    /**
     * Reads the registers, extends the immediate and computes the ALU result.
     * The first ALU operand is {@code rs1}, except for {@code lui} (0) and {@code auipc} (pc).
     *
     * @param decodeResult the decoded instruction fields
     * @param controlSignals the control signals of the instruction
     * @return the ALU result, both ALU-side register values and the extended immediate
     */
    private ExecuteResult execute(DecodeResult decodeResult, ControlSignals controlSignals) {
        // Register file
        int[] rd = registerFile.read(decodeResult.rs1(), decodeResult.rs2());

        InstructionFormat instructionType = controlSignals.getInstructionType();

        int rd1 = rd[0];
        int rd2 = rd[1];

        if (instructionType == InstructionFormat.U_TYPE_AUIPC) {
            rd1 = pc;
        }
        else if (instructionType == InstructionFormat.U_TYPE_LUI) {
            rd1 = 0;
        }

        // Extend
        int immExt = extend.operate(decodeResult.rawImmediate(), controlSignals.getImmSrc());

        // ALU
        int aluSrcB = Mux2.select(rd2, immExt, controlSignals.isAluSrc());
        int aluResult = alu.operate(rd1, aluSrcB, controlSignals.getAluControl());

        return new ExecuteResult(aluResult, rd1, rd2, immExt);
    }

    /**
     * Accesses the data memory, only for loads and stores.
     * Stores narrower than a word (sb, sh) replace only the lowest bytes of the addressed word.
     * Loads are narrowed by {@code funct3} and sign- or zero-extended (lb, lh, lw, lbu, lhu).
     *
     * @param decodeResult the decoded instruction fields
     * @param executeResult the result of the execute stage
     * @param controlSignals the control signals of the instruction
     * @return the loaded value, or {@code 0} for stores and non-memory instructions
     */
    private int memory(DecodeResult decodeResult, ExecuteResult executeResult, ControlSignals controlSignals) {
        if (!controlSignals.isMemWrite() && !controlSignals.isResultSrc()) return 0;

        int address = executeResult.aluResult();
        int funct3 = decodeResult.funct3();
        int word = dataMemory.operate(address, 0, false);

        if (controlSignals.isMemWrite()) {
            int value = executeResult.rd2();
            int merged = switch (funct3) {
                case 0b000 -> (word & 0xFFFFFF00) | (value & 0xFF);
                case 0b001 -> (word & 0xFFFF0000) | (value & 0xFFFF);
                default -> value;
            };
            dataMemory.operate(address, merged, true);
            return 0;
        }

        return switch (funct3) {
            case 0b000 -> (byte) word;
            case 0b001 -> (short) word;
            case 0b100 -> word & 0xFF;
            case 0b101 -> word & 0xFFFF;
            default -> word;
        };
    }

    /**
     * Writes the result to the destination register.
     * The result is the ALU result or the memory data; for {@code jal} and {@code jalr}
     * it is the return address {@code pc + 4}.
     *
     * @param readData the data read from data memory
     * @param decodeResult the decoded instruction fields
     * @param executeResult the result of the execute stage
     * @param controlSignals the control signals of the instruction
     */
    private void writeBack(int readData, DecodeResult decodeResult, ExecuteResult executeResult, ControlSignals controlSignals) {
        int result = Mux2.select(executeResult.aluResult(), readData, controlSignals.isResultSrc());

        InstructionFormat instructionType = controlSignals.getInstructionType();
        if (instructionType == InstructionFormat.J_TYPE || instructionType == InstructionFormat.I_TYPE_JALR) {
            result = Adder.add(pc, 4);
        }

        registerFile.write(decodeResult.rd(), result, controlSignals.isRegWrite());
    }

    /**
     * Sets the pc to {@code pc + 4} or to the jump/branch target.
     * {@code jalr} always jumps to {@code rs1 + imm} (ALU result) with the lowest bit cleared.
     * {@code jal} and branches jump to {@code pc + imm}.
     *
     * @param executeResult the result of the execute stage
     * @param controlSignals the control signals of the instruction
     */
    private void pcUpdate(ExecuteResult executeResult, ControlSignals controlSignals) {
        int pcTarget = controlSignals.getInstructionType() == InstructionFormat.I_TYPE_JALR
                ? executeResult.aluResult() & ~1
                : Adder.add(pc, executeResult.immExt());
        int pcPlus4 = Adder.add(pc, 4);

        pc = Mux2.select(pcPlus4, pcTarget, controlSignals.isPcSrc());
    }
}