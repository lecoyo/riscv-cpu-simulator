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
     * @param a byte address of the instruction
     * @return the 32-bit instruction word starting at address {@code pc}
     */
    public int read(int a) {
        return ((memory[a] & 0xFF) << 24) |
                ((memory[a + 1] & 0xFF) << 16) |
                ((memory[a + 2] & 0xFF) << 8) |
                ((memory[a + 3] & 0xFF));
    }
}
