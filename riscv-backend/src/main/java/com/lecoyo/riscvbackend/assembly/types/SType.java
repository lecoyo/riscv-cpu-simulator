package com.lecoyo.riscvbackend.assembly.types;

import com.lecoyo.riscvbackend.assembly.Instruction;
import lombok.Getter;

@Getter
public class SType extends Instruction {
    private byte imm1;
    private byte funct3;
    private byte rs1;
    private byte rs2;
    private byte imm2;

    private short fullImmediate;

    public SType(String mnemonic, byte opcode, byte imm1, byte funct3, byte rs1, byte rs2, byte imm2)
    {
        this.mnemonic = mnemonic;
        this.opcode = opcode;
        this.imm1 = imm1;
        this.funct3 = funct3;
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.imm2 = imm2;
    }

    @Override
    protected int assemble() {
        return (opcode & 0x7F)
                | (getImm1() & 0x1F) << 7
                | (getFunct3() & 0x7) << 12
                | (getRs1() & 0x1F) << 15
                | (getRs2() & 0x1F) << 20
                | (getImm2() & 0x7F) << 25;
    }
}
