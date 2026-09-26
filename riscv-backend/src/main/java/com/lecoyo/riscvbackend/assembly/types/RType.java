package com.lecoyo.riscvbackend.assembly.types;

import com.lecoyo.riscvbackend.assembly.Instruction;
import com.lecoyo.riscvbackend.cpu.CPUState;

public class RType extends Instruction {
    private byte imm1;
    private byte funct3;
    private byte rs1;
    private byte rs2;
    private byte funct7;

    public RType(CPUState cpu, String text) {
        super(cpu, text);
    }

    @Override
    protected int assemble(CPUState cpu, String text) {
        return 0;
    }
}
