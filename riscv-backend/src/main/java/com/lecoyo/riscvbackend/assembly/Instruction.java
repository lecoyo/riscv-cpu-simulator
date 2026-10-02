package com.lecoyo.riscvbackend.assembly;

/**
 * Abstract Instruction field for
 */

public abstract class Instruction {
    public Byte opcode;
    public String mnemonic;

    public final int encode() {
        return assemble();
    }

    protected abstract int assemble();
}
