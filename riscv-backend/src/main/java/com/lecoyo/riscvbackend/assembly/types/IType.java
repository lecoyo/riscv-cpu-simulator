package com.lecoyo.riscvbackend.assembly.types;

import com.lecoyo.riscvbackend.assembly.Instruction;
import lombok.Getter;

@Getter
public class IType extends Instruction {
    private byte rd;
    private byte funct3;
    private byte rs1;
    private byte imm;

    public IType(CPUState cpu, String text) {
        super(cpu, text);
    }

    @Override
    protected int assemble(CPUState cpu, String text) {
        return 0;
    }
}
