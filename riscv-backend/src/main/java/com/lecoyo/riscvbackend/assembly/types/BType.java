package com.lecoyo.riscvbackend.assembly.types;

import com.lecoyo.riscvbackend.assembly.Instruction;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
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

    public static BType fromInt(int instr) {
        BType b = new BType();

        b.immHigh = (byte) ((instr >>> 25) & 0x7F);
        b.rs2     = (byte) ((instr >>> 20) & 0x1F);
        b.rs1     = (byte) ((instr >>> 15) & 0x1F);
        b.funct3  = (byte) ((instr >>> 12) & 0x07);
        b.immLow  = (byte) ((instr >>> 7) & 0x1F);

        int imm12   = (instr >>> 31) & 0x1;
        int imm10_5 = (instr >>> 25) & 0x3F;
        int imm4_1  = (instr >>> 8)  & 0xF;
        int imm11   = (instr >>> 7)  & 0x1;

        int offset = (imm12 << 12) | (imm11 << 11) | (imm10_5 << 5) | (imm4_1 << 1);

        b.branchOffset = (offset << 19) >> 19;

        return b;
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
