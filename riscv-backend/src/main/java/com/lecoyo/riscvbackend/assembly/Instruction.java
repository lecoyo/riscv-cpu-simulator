package com.lecoyo.riscvbackend.assembly;

/**
 * Abstract Instruction field for
 */

public abstract class Instruction {
    public Byte opcode;
    public String mnemonic;
    public Integer binary;

    public final void encode() {
        this.binary = assemble(mnemonic);
    }

    protected abstract int assemble(String mnemonic);
}
