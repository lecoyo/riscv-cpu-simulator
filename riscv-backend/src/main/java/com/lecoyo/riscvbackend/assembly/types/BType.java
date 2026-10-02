package com.lecoyo.riscvbackend.assembly.types;

import com.lecoyo.riscvbackend.assembly.Instruction;
import lombok.Getter;

@Getter
public class BType extends Instruction {
    private byte immHigh;
    private byte funct3;
    private byte rs1;
    private byte rs2;
    private byte immLow;

    private int branchOffset;

    public BType(String mnemonic, byte opcode, int labelValue, int pc, byte imm1, byte funct3, byte rs1, byte rs2, byte imm2) {
        // TODO
        this.mnemonic = mnemonic;
        this.opcode = opcode;
        branchOffset = labelValue - pc;
        this.immHigh = imm1;
        this.funct3 = funct3;
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.immLow = imm2;
    }

    @Override
    protected int assemble() {
        return (opcode & 0x7F)
                | (getImmLow() & 0x1F) << 7
                | (getFunct3() & 0x7) << 12
                | (getRs1() & 0x1F) << 15
                | (getRs2() & 0x1F) << 20
                | (getImmHigh() & 0x7F) << 25;
    }
}
