package com.lecoyo.riscvbackend.cpu.components;

public class InstructionMemory {
    private final byte[] memory;

    /**
     * Creates and implements the instruction memory with the instructions in bytes.
     *
     * @param program the program to be loaded into memory
     */
    public InstructionMemory(byte[] program) {
        this.memory = program;
    }

    /**
     * Reads the instruction word at byte address {@code pc}.
     * {@code pc} must be word-aligned (a multiple of 4).
     *
     * @param pc byte address of the instruction
     * @return 32-bit instruction starting at address {@code pc}
     */
    public int read(int pc) {
        return ((memory[pc] & 0xFF) << 24) |
                ((memory[pc + 1] & 0xFF) << 16) |
                ((memory[pc + 2] & 0xFF) << 8) |
                ((memory[pc + 3] & 0xFF));
    }
}
