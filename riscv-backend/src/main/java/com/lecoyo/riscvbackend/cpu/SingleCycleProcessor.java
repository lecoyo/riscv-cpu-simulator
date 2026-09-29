package com.lecoyo.riscvbackend.cpu;

import com.lecoyo.riscvbackend.cpu.components.*;
import com.lecoyo.riscvbackend.cpu.controlunit.ALUControl;
import com.lecoyo.riscvbackend.cpu.controlunit.ControlSignals;
import com.lecoyo.riscvbackend.cpu.controlunit.ControlUnit;
import com.lecoyo.riscvbackend.cpu.controlunit.MainControlUnit;

public class SingleCycleProcessor {
    private final InstructionMemory instructionMemory;
    private final DataMemory dataMemory;
    private final ControlUnit controlUnit;
    private final RegisterFile registerFile;
    private final Extend extend;
    private final ALU alu;

    private int pc = 0;

    private record DecodeResult(byte op, byte rd, byte funct3, byte rs1, byte rs2, byte funct7, int rawImmediate) {}

    private record ExecuteResult(int aluResult, int rd2, int immExt) {}

    public SingleCycleProcessor(byte[] instructions, int memorySize) {
        this.instructionMemory = new InstructionMemory(instructions);
        this.dataMemory = new DataMemory(memorySize);
        this.controlUnit = new ControlUnit(new MainControlUnit(), new ALUControl());
        this.registerFile = new RegisterFile();
        this.extend = new Extend();
        this.alu = new ALU();
    }

    public void simulate() {
        while (pc < instructionMemory.getProgramSize()) {
            step();
        }
    }

    private void step() {
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
    }

    private int fetch() {
        return instructionMemory.read(pc);
    }

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

    private int memory(ExecuteResult executeResult, ControlSignals controlSignals) {
        return dataMemory.operate(executeResult.aluResult(), executeResult.rd2(), controlSignals.isMemWrite());
    }

    private void writeBack(int readData, DecodeResult decodeResult, ExecuteResult executeResult, ControlSignals controlSignals) {
        int result = Mux2.select(executeResult.aluResult(), readData, controlSignals.isResultSrc());
        registerFile.write(decodeResult.rd(), result, controlSignals.isRegWrite());
    }

    private void pcUpdate(ExecuteResult executeResult, ControlSignals controlSignals) {
        int pcTarget = Adder.add(pc, executeResult.immExt());
        int pcPlus4 = Adder.add(pc, 4);

        pc = Mux2.select(pcPlus4, pcTarget, controlSignals.isPcSrc());
    }
}
