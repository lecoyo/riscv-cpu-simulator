package com.lecoyo.riscvbackend.cpu.components;

public class InstructionMemory {
    private final byte[] memory;

    public InstructionMemory(byte[] program) {
        this.memory = program;
    }

    public int read(int pc) {
        return ((memory[pc] & 0xFF) << 24) |
                ((memory[pc + 1] & 0xFF) << 16) |
                ((memory[pc + 2] & 0xFF) << 8) |
                ((memory[pc + 3] & 0xFF));
    }
}
