package com.lecoyo.riscvbackend.assembly.types;

import com.lecoyo.riscvbackend.assembly.Instruction;
import lombok.Getter;

@Getter
public class IType extends Instruction {
    private byte rd;
    private byte funct3;
    private byte rs1;
    private byte imm;

    public IType(byte opcode, byte rd, byte funct3, byte rs1, byte imm) {
        this.opcode = opcode;
        this.rd = rd;
        this.funct3 = funct3;
        this.rs1 = rs1;
        this.imm = imm;
    }

    @Override
    protected int assemble(String mnemonic) {
        return 0;
    }
}
