package com.lecoyo.riscvbackend.assembly.types;

import com.lecoyo.riscvbackend.assembly.Instruction;
import lombok.Getter;

@Getter
public class RType extends Instruction {
    private final byte imm1;
    private final byte funct3;
    private final byte rs1;
    private final byte rs2;
    private final byte funct7;

    public RType(byte imm1, byte funct3, byte rs1, byte rs2, byte funct7) {
        this.imm1 = imm1;
        this.funct3 = funct3;
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.funct7 = funct7;
    }

    @Override
    protected int assemble(String mnemonic) {
        return 0;
    }
}
