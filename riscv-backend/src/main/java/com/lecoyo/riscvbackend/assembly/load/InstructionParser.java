package com.lecoyo.riscvbackend.assembly.load;

import com.lecoyo.riscvbackend.assembly.Instruction;

public class InstructionParser {
    public Instruction parse(String instruction) {
        return null;
    }

    public boolean isLabel(String line) {
        return line.endsWith(":");
    }
}
