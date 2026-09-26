package com.lecoyo.riscvbackend.assembly;

import com.lecoyo.riscvbackend.cpu.CPUState;

/**
 * Abtract Instruction field for
 */
public abstract class Instruction {
    public CPUState cpu;
    public Byte opcode;
    public String text;
    public Integer binary;

    public Instruction(CPUState cpu, String text) {
        binary = assemble(cpu, text);
    }

    protected abstract int assemble(CPUState cpu, String text);
}
