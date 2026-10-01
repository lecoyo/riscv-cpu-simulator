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

    // program counter as byte address
    private int pc = 0;

    private record DecodeResult(byte op, byte rd, byte funct3, byte rs1, byte rs2, byte funct7, int rawImmediate) {}
    private record ExecuteResult(int aluResult, int rd2, int immExt) {}

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

        while (pc < instructionMemory.getProgramSize()) {
            trace.add(step());
        }

        return trace;
    }

    /**
     * Executes one instruction and returns the resulting snapshot.
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
                false //TODO zero flag
        );

        ExecuteResult executeResult = execute(decodeResult, controlSignals);
        int readData = memory(executeResult, controlSignals);
        writeBack(readData, decodeResult, executeResult, controlSignals);
        pcUpdate(executeResult, controlSignals);

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
     *
     * @param decodeResult the decoded instruction fields
     * @param controlSignals the control signals of the instruction
     * @return the ALU result, the second register value and the extended Immediate
     */
    private ExecuteResult execute(DecodeResult decodeResult, ControlSignals controlSignals) {
        // Register file
        int[] rd = registerFile.read(decodeResult.rs1(), decodeResult.rs2());
        int rd1 = rd[0];
        int rd2 = rd[1];

        // Extend
        int immExt = extend.operate(decodeResult.rawImmediate(), controlSignals.getImmSrc());

        // ALU
        int aluSrcB = Mux2.select(rd2, immExt, controlSignals.isAluSrc());
        int aluResult = alu.operate(rd1, aluSrcB, controlSignals.getAluControl());

        return new ExecuteResult(aluResult, rd2, immExt);
    }

    /**
     * Accesses the data memory.
     * The memory is only accessed if the instruction is a load ({@code resultSrc}) or a store ({@code memWrite}).
     * For all other instructions, the ALU result is not a memory address, so no access takes place.
     *
     * @param executeResult the result of the execute stage
     * @param controlSignals the control signals of the instruction
     * @return the data read from memory, or {@code 0} if the instruction is a store or does not access memory
     */
    private int memory(ExecuteResult executeResult, ControlSignals controlSignals) {
        if (!controlSignals.isMemWrite() && !controlSignals.isResultSrc()) return 0;

        return dataMemory.operate(executeResult.aluResult(), executeResult.rd2(), controlSignals.isMemWrite());
    }

    /**
     * Selects between ALU result and memory data and writes it to the destination register.
     *
     * @param readData the data read from data memory
     * @param decodeResult the decoded instruction fields
     * @param executeResult the result of the execute stage
     * @param controlSignals the control signals of the instruction
     */
    private void writeBack(int readData, DecodeResult decodeResult, ExecuteResult executeResult, ControlSignals controlSignals) {
        int result = Mux2.select(executeResult.aluResult(), readData, controlSignals.isResultSrc());
        registerFile.write(decodeResult.rd(), result, controlSignals.isRegWrite());
    }

    /**
     * Sets the pc to either {@code pc + 4} or the branch/jump target.
     *
     * @param executeResult the result of the execute stage
     * @param controlSignals the control signals of the instruction
     */
    private void pcUpdate(ExecuteResult executeResult, ControlSignals controlSignals) {
        int pcTarget = Adder.add(pc, executeResult.immExt());
        int pcPlus4 = Adder.add(pc, 4);

        pc = Mux2.select(pcPlus4, pcTarget, controlSignals.isPcSrc());
    }
}
