package com.lecoyo.riscvbackend.cpu.controlunit;

public class ALUControl {
    /**
     * Refines a coarse ALU operation selector into the final 4-bit ALU
     * operation code, using {@code funct3}/{@code funct7} for R-Type and
     * I-Type-ALU instructions.
     *
     * @param aluOp  the coarse ALU operation selector
     * @param funct3 the 3-bit funct3 field
     * @param funct7 the 7-bit funct7 field
     * @return the 4-bit ALU control code
     * @throws IllegalArgumentException if {@code aluOp} or {@code funct3} is unknown
     */
    public byte decode(byte aluOp, byte funct3, byte funct7) {
        return switch (aluOp) {
            case 0b00 -> 0b0000; // load/store
            case 0b01 -> 0b1000; // branch
            case 0b10 -> decodeRType(funct3, funct7);      // R-Type
            case 0b11 -> decodeIType(funct3, funct7);      // I-Type-ALU
            default -> throw new IllegalArgumentException("Unknown Opcode: " + aluOp);
        };
    }

    private byte decodeRType(byte funct3, byte funct7) {
        return (byte) switch (funct3) {
            case 0b000 -> funct7 == 0b0100000 ? 0b1000 : 0b0000; // SUB vs ADD
            case 0b001 -> 0b0001; // SLL
            case 0b010 -> 0b0010; // SLT
            case 0b011 -> 0b0011; // SLTU
            case 0b100 -> 0b0100; // XOR
            case 0b101 -> funct7 == 0b0100000 ? 0b1001 : 0b0101; // SRA vs SRL
            case 0b110 -> 0b0110; // OR
            case 0b111 -> 0b0111; // AND
            default -> throw new IllegalArgumentException("Unknown funct3: " + funct3);
        };
    }

    private byte decodeIType(byte funct3, byte funct7) {
        return (byte) switch (funct3) {
            case 0b000 -> 0b0000; // ADDI
            case 0b001 -> 0b0001; // SLLI
            case 0b010 -> 0b0010; // SLTI
            case 0b011 -> 0b0011; // SLTIU
            case 0b100 -> 0b0100; // XORI
            case 0b101 -> funct7 == 0b0100000 ? 0b1001 : 0b0101; // SRAI vs SRLI
            case 0b110 -> 0b0110; // ORI
            case 0b111 -> 0b0111; // ANDI
            default -> throw new IllegalArgumentException("Unknown funct3: " + funct3);
        };
    }
}