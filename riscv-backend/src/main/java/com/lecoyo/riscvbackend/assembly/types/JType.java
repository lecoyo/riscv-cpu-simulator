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
    protected int assemble() {
        return (opcode & 0x7F)
                | ((rd & 0x1F) << 7)
                | (((imm >> 12) & 0xFF)  << 12)
                | (((imm >> 11) & 0x1)   << 20)
                | (((imm >> 1)  & 0x3FF) << 21)
                | (((imm >> 20) & 0x1)   << 31);
    }
}
