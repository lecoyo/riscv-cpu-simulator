package com.lecoyo.riscvbackend.assembly;

/**
 * Abstract Instruction field for
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
