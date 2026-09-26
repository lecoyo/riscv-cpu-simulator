package com.lecoyo.riscvbackend.assembly.types;

import com.lecoyo.riscvbackend.assembly.Instruction;
import com.lecoyo.riscvbackend.cpu.CPUState;
import lombok.Getter;

@Getter
public class JType extends Instruction {
    private byte opcode;
    private byte rd;
    private int imm;

    private int fullImmediate;

    public JType(CPUState cpu, String text) {
        super(cpu, text);
    }

    @Override
    protected int assemble(CPUState cpu, String text) {
        return 0;
    }
}
