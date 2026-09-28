package com.lecoyo.riscvbackend.cpu.components;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ALU {
    /**
     * Performs the ALU operation selected by {@code aluControl} on the two operands.
     * <p>
     * Supported operations: ADD, SUB, AND, OR, XOR, SLT, SLTU, SLL, SRL, SRA
     *
     * @param srcA the first operand
     * @param srcB the second operand
     * @param aluControl the 4-bit code selecting the operation
     * @return the 32-bit result of the operation
     * @throws IllegalArgumentException if {@code aluControl} is not a known operation code
     */
    public int operate(int srcA, int srcB, byte aluControl) {
        return switch (aluControl) {
            case 0b0000 -> srcA + srcB; // ADD
            case 0b0001 -> srcA << (srcB & 0b11111); // SLL
            case 0b0010 -> srcA < srcB ? 1 : 0; // SLT
            case 0b0011 -> Integer.compareUnsigned(srcA, srcB) < 0 ? 1 : 0; // SLTU
            case 0b0100 -> srcA ^ srcB; // XOR
            case 0b0101 -> srcA >>> (srcB & 0b11111); // SRL
            case 0b0110 -> srcA | srcB; // OR
            case 0b0111 -> srcA & srcB; // AND
            case 0b1000 -> srcA - srcB; // SUB
            case 0b1001 -> srcA >> (srcB & 0b11111); // SRA
            default -> throw new IllegalArgumentException("Unknown ALU control: " + aluControl);
        };
    }
}
