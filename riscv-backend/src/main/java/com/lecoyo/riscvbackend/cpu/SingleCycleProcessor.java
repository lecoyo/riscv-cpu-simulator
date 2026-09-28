package com.lecoyo.riscvbackend.cpu;

import com.lecoyo.riscvbackend.cpu.components.DataMemory;
import com.lecoyo.riscvbackend.cpu.components.Extend;
import com.lecoyo.riscvbackend.cpu.components.InstructionMemory;
import com.lecoyo.riscvbackend.cpu.components.RegisterFile;
import com.lecoyo.riscvbackend.cpu.controlunit.ControlSignals;
import com.lecoyo.riscvbackend.cpu.controlunit.ControlUnit;

public class SingleCycleProcessor {
    InstructionMemory instructionMemory;
    DataMemory dataMemory;
    ControlUnit controlUnit;
    RegisterFile registerFile;
    Extend extend;
    int pc = 0;

    public SingleCycleProcessor(byte[] instructions, int memorySize) {
        this.instructionMemory = new InstructionMemory(instructions);
        this.dataMemory = new DataMemory(memorySize);
        this.registerFile = new RegisterFile();
    }


    public void simulate() {
        // Goes through all the instruction stored in 4 byte each
        for (int i = 0; i < instructionMemory.getProgramSize() / 4; i++) {
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

            // TODO ALU
        }
    }
}
