package com.lecoyo.riscvbackend.assembly.types;

import com.lecoyo.riscvbackend.assembly.Instruction;
import lombok.Getter;

@Getter
public class UType extends Instruction {
    private byte opcode;
    private byte rd;
    private int fullImmediate;

    public UType(String mnemonic, byte opcode, byte rd, int fullImmediate) {
        this.mnemonic = mnemonic;
        this.opcode = opcode;
        this.rd = rd;
        this.fullImmediate = fullImmediate;
    }

    @Override
    protected int assemble(String mnemonic) {
        return 0;
    }
}
