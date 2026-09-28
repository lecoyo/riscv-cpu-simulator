package com.lecoyo.riscvbackend.cpu.components;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Extend {
    /**
     * Builds the sign-extended 32-bit immediate from instruction bits [31:7],
     * laid out according to the format selected by {@code immSrc}.
     *
     * @param possibleImmediate instruction bits [31:7] without opcode
     * @param immSrc code selecting the immediate format
     * @return the 32-bit immediate
     * @throws IllegalArgumentException if {@code immSrc} is unknown
     */
    public int operate(int possibleImmediate, byte immSrc) {
        return switch (immSrc) {
            case 0b000 -> // I-Type
                    ((possibleImmediate << 7) >> 20); // imm[11:0]

            case 0b001 -> // S-Type
                    (((possibleImmediate << 7) >> 25) << 5) | // imm[11:5]
                    (possibleImmediate & 0b11111); // imm[4:0]

            case 0b010 -> // B-Type
                    (((possibleImmediate << 7) >> 31) << 12) | // imm[12]
                    (possibleImmediate & 0b1) << 11 | // imm[11]
                    ((possibleImmediate << 8) >>> 26) << 5 | // imm[10:5]
                    (possibleImmediate & 0b11110); // imm[4:1]

            case 0b011 -> // J-Type
                    (((possibleImmediate << 7) >> 31) << 20) | // imm[20]
                    (((possibleImmediate << 19) >>> 24) << 12) | // imm[19:12]
                    ((possibleImmediate & 0b10000000000000) >>> 2) | // imm[11]
                    (((possibleImmediate << 8) >>> 22) << 1); // imm[10:1]

            case 0b100 -> // U-Type
                    ((possibleImmediate >>> 5) << 12); // imm[31:12]

            default -> throw new IllegalArgumentException("Unknown immSrc: " + immSrc);
        };
    }
}
