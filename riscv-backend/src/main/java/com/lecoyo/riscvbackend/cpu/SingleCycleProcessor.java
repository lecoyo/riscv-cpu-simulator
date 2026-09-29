package com.lecoyo.riscvbackend.cpu;

import com.lecoyo.riscvbackend.cpu.components.*;
import com.lecoyo.riscvbackend.cpu.controlunit.ControlSignals;
import com.lecoyo.riscvbackend.cpu.controlunit.ControlUnit;

public class SingleCycleProcessor {
    InstructionMemory instructionMemory;
    DataMemory dataMemory;
    ControlUnit controlUnit;
    RegisterFile registerFile;
    Extend extend;
    ALU alu;

    public SingleCycleProcessor(byte[] instructions, int memorySize) {
        this.instructionMemory = new InstructionMemory(instructions);
        this.dataMemory = new DataMemory(memorySize);
        this.registerFile = new RegisterFile();
        this.extend = new Extend();
        this.alu = new ALU();
    }


    public void simulate() {
        int pc = 0;

        // Goes through all the instruction stored in 4 byte each
        while (pc < instructionMemory.getProgramSize() / 4) {
            // fetch instruction
            int instruction = instructionMemory.read(pc);

            // split up the instruction
            byte op = (byte) (instruction & 0b1111111); // 6:0
            byte a3 = (byte) ((instruction >>> 7) & 0b11111); // 11:7
            byte funct3 = (byte) ((instruction >>> 12) & 0b111); // 14:12
            byte a1 = (byte) ((instruction >>> 15) & 0b11111); // 19:15
            byte a2 = (byte) ((instruction >>> 20) & 0b11111); // 24:20
            byte funct7 = (byte) (instruction >>> 25); // 31:25

            int possibleImmediate = (instruction >>> 7); // 31:7

            // decode control signals from control unit
            ControlSignals controlSignals = controlUnit.operate(op, funct3, funct7, false);

            // read register file
            int[] rd = registerFile.read(a1, a2);
            int rd1 = rd[0];
            int rd2 = rd[1];

            // extend unit
            int immExt = extend.operate(possibleImmediate, controlSignals.getImmSrc());

            // ALU
            int aluSrcB = Mux2.select(rd2, immExt, controlSignals.isAluSrc());
            int aluResult = alu.operate(rd1, aluSrcB, controlSignals.aluControl);

            // data memory
            int readData = dataMemory.operate(aluResult, rd2, controlSignals.isMemWrite());

            // write back
            int result = Mux2.select(aluResult, readData, controlSignals.isResultSrc());
            registerFile.write(a3, result, controlSignals.isRegWrite());

            // PC counter
            int pcTarget = Adder.add(pc, immExt);
            int pcPlus4 = Adder.add(pc, 4);

            pc =  Mux2.select(pcPlus4, pcTarget, controlSignals.isPcSrc());
        }
    }
}
