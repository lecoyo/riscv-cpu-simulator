package com.lecoyo.riscvbackend.assembly.types;

import com.lecoyo.riscvbackend.assembly.Instruction;
import lombok.Getter;

@Getter
public class JType extends Instruction {
    private byte opcode;
    private byte rd;
    private int imm;

    private int fullImmediate;

    public JType(String mnemonic, byte opcode, byte rd, int imm) {
        this.mnemonic = mnemonic;
        this.opcode = opcode;
        this.rd = rd;
        this.imm = imm;
    }

    @Override
    protected int assemble(String mnemonic) {
        return 0;
    }
}
