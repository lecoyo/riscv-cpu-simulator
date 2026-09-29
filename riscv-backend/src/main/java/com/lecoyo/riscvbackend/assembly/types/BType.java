package com.lecoyo.riscvbackend.assembly.types;

import com.lecoyo.riscvbackend.assembly.Instruction;
import lombok.Getter;

@Getter
public class BType extends Instruction {
    private byte imm1;
    private byte funct3;
    private byte rs1;
    private byte rs2;
    private byte imm2;

    private int branchOffset;
    private int fullImmediate;

    public BType(int labelValue, int pc, String mnemonic) {
        // TODO
        branchOffset = labelValue - pc;
        super(mnemonic);
    }

    @Override
    protected int assemble(String mnemonic) {
        return 0;
    }
}
