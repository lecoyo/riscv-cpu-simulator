package com.lecoyo.riscvbackend.assembly.types;

import com.lecoyo.riscvbackend.assembly.Instruction;
import lombok.Getter;

@Getter
public class IType extends Instruction {
    private byte rd;
    private byte funct3;
    private byte rs1;
    private short imm;

    public IType(String mnemonic, byte opcode, byte rd, byte funct3, byte rs1, short imm) {
        this.mnemonic = mnemonic;
        this.opcode = opcode;
        this.rd = rd;
        this.funct3 = funct3;
        this.rs1 = rs1;
        this.imm = imm;
    }

    @Override
    protected int assemble() {
        return (opcode & 0x7F)
                | (getRd() & 0x1F) << 7
                | (getFunct3() & 0x7) << 12
                | (getRs1() & 0x1F) << 15
                | (getImm() & 0xFFF) << 20;
    }
}
