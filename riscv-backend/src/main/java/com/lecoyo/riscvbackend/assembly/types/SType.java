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

    public SType(byte imm1, byte funct3, byte rs1, byte rs2, byte imm2)
    {
        this.imm1 = imm1;
        this.funct3 = funct3;
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.imm2 = imm2;
    }

    @Override
    protected int assemble(String mnemonic) {
        return 0;
    }
}
