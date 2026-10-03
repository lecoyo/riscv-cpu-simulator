package com.lecoyo.riscvbackend.cpu;

import com.lecoyo.riscvbackend.cpu.components.*;
import com.lecoyo.riscvbackend.cpu.controlunit.ALUControl;
import com.lecoyo.riscvbackend.cpu.controlunit.ControlSignals;
import com.lecoyo.riscvbackend.cpu.controlunit.ControlUnit;
import com.lecoyo.riscvbackend.cpu.controlunit.MainControlUnit;

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

    private static final int OPCODE_JAL = 0b1101111;
    private static final int OPCODE_JALR = 0b1100111;
    private static final int OPCODE_LUI = 0b0110111;
    private static final int OPCODE_AUIPC = 0b0010111;
    private static final int OPCODE_B_TYPE = 0b1100011;

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
            boolean memWrite
    ) {}

    /**
     * Creates a processor with the given program and data memory size.
     *
     * @param instructions the program as bytes
     * @param memorySize   size of the data memory in bytes
     */
    public SingleCycleProcessor(byte[] instructions, int memorySize) {
        this.instructionMemory = new InstructionMemory(instructions);
        this.dataMemory = new DataMemory(memorySize);
        this.controlUnit = new ControlUnit(new MainControlUnit(), new ALUControl());
        this.registerFile = new RegisterFile();
        this.registerFile.write(2, memorySize/2, true);
        this.extend = new Extend();
        this.alu = new ALU();
    }

    /**
     * Executes the whole program and returns a snapshot after every instruction.
     *
     * @return the list of CPU snapshots, one per executed instruction
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
     * Executes one instruction and returns the resulting snapshot.
     * For branch instructions the control signals are resolved twice: first without a branch decision
     * to run the execute stage, then again with the evaluated branch condition so that {@code pcSrc} is set correctly.
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

        if (decodeResult.op() == OPCODE_B_TYPE) {
            controlSignals = controlUnit.operate(
                    decodeResult.op(),
                    decodeResult.funct3(),
                    decodeResult.funct7(),
                    isZeroFlag(executeResult, decodeResult)
            );
        }

        int readData = memory(decodeResult, executeResult, controlSignals);
        writeBack(readData, decodeResult, executeResult, controlSignals);
        pcUpdate(decodeResult, executeResult, controlSignals);

        return new CpuSnapshot(
                pcBefore,
                instruction,
                controlSignals,
                registerFile.getRegisters().clone(),
                executeResult.aluResult(),
                executeResult.immExt(),
                controlSignals.isMemWrite()
        );
    }

    /**
     * Evaluates the branch condition of a B-type instruction by comparing the two source register values.
     *
     * @param executeResult the result of the execute stage, providing the register values
     * @param decodeResult the decoded instruction fields, providing {@code funct3} to select the comparison
     * @return {@code true} if the according branch condition is met
     * @throws IllegalArgumentException if {@code funct3} does not match a known branch instruction
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
     * Splits the instruction word into its bit fields.
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
     * The first ALU operand is the value of {@code rs1}, except for {@code lui} (constant 0)
     * and {@code auipc} (the current pc).
     *
     * @param decodeResult the decoded instruction fields
     * @param controlSignals the control signals of the instruction
     * @return the ALU result, the second register value and the extended immediate
     */
    private ExecuteResult execute(DecodeResult decodeResult, ControlSignals controlSignals) {
        // Register file
        int[] rd = registerFile.read(decodeResult.rs1(), decodeResult.rs2());
        int rd1 = switch (decodeResult.op()) {
            case OPCODE_LUI -> 0;
            case OPCODE_AUIPC -> pc;
            default -> rd[0];
        };
        int rd2 = rd[1];

        // Extend
        int immExt = extend.operate(decodeResult.rawImmediate(), controlSignals.getImmSrc());

        // ALU
        int aluSrcB = Mux2.select(rd2, immExt, controlSignals.isAluSrc());
        int aluResult = alu.operate(rd1, aluSrcB, controlSignals.getAluControl());

        return new ExecuteResult(aluResult, rd1, rd2, immExt);
    }

    /**
     * Accesses the data memory (load or store).
     * The memory is only accessed for loads ({@code resultSrc}) and stores ({@code memWrite}).
     * Stores of a byte or halfword (sb, sh) only replace the lowest bytes of the addressed word.
     * Loads are narrowed according to {@code funct3} and sign- or zero-extended (lb, lh, lw, lbu, lhu).
     *
     * @param decodeResult the decoded instruction fields
     * @param executeResult the result of the execute stage
     * @param controlSignals the control signals of the instruction
     * @return the data read from memory, or {@code 0} if the instruction is a store or does not access memory
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
     * Selects the value to write back and writes it to the destination register.
     * For {@code jal} and {@code jalr} the return address {@code pc + 4} is written,
     * otherwise the value is selected between ALU result and memory data.
     *
     * @param readData the data read from data memory
     * @param decodeResult the decoded instruction fields
     * @param executeResult the result of the execute stage
     * @param controlSignals the control signals of the instruction
     */
    private void writeBack(int readData, DecodeResult decodeResult, ExecuteResult executeResult, ControlSignals controlSignals) {
        int result = Mux2.select(executeResult.aluResult(), readData, controlSignals.isResultSrc());

        if (decodeResult.op() == OPCODE_JAL || decodeResult.op() == OPCODE_JALR) {
            result = Adder.add(pc, 4);
        }

        registerFile.write(decodeResult.rd(), result, controlSignals.isRegWrite());
    }

    /**
     * Sets the pc to either {@code pc + 4} or the branch/jump target.
     * For {@code jalr} the target is the ALU result ({@code rs1 + imm}) with the lowest bit cleared
     * and the jump is always taken.
     * For {@code jal} and branches the target is {@code pc + imm}.
     *
     * @param decodeResult the decoded instruction fields
     * @param executeResult the result of the execute stage
     * @param controlSignals the control signals of the instruction
     */
    private void pcUpdate(DecodeResult decodeResult, ExecuteResult executeResult, ControlSignals controlSignals) {
        boolean isJalr = decodeResult.op() == OPCODE_JALR;

        int pcTarget = isJalr
                ? executeResult.aluResult() & ~1
                : Adder.add(pc, executeResult.immExt());
        int pcPlus4 = Adder.add(pc, 4);

        pc = Mux2.select(pcPlus4, pcTarget, isJalr || controlSignals.isPcSrc());
    }
}
