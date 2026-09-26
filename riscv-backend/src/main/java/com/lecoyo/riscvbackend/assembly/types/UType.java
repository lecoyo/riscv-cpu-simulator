package com.lecoyo.riscvbackend.assembly.types;

import com.lecoyo.riscvbackend.assembly.Instruction;
import com.lecoyo.riscvbackend.cpu.CPUState;

public class UType extends Instruction {
    private byte opcode;
    private byte rd;
    private int fullImmediate;

    public UType(CPUState cpu, String text) {
        super(cpu, text);
    }

    @Override
    protected int assemble(CPUState cpu, String text) {
        return 0;
    }
}
