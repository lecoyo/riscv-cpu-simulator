package com.lecoyo.riscvbackend.cpu.components;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExtendTest {
    private final Extend extend = new Extend();

    private int possibleImmediate(int instruction) {
        return instruction >>> 7;
    }

    @Test
    void iType_positive() {
        int instr = 0b000000000101_00000_000_00001_0010011;
        assertEquals(5, extend.operate(possibleImmediate(instr), (byte) 0b000));
    }

    @Test
    void iType_negative() {
        int instr = 0b111111111111_00000_000_00001_0010011;
        assertEquals(-1, extend.operate(possibleImmediate(instr), (byte) 0b000));
    }

    @Test
    void sType_negative() {
        int instr = 0b1111111_00001_00010_010_11100_0100011;
        assertEquals(-4, extend.operate(possibleImmediate(instr), (byte) 0b001));
    }

    @Test
    void bType_negative() {
        int instr = 0b1_111111_00000_00000_000_1110_1_1100011;
        assertEquals(-4, extend.operate(possibleImmediate(instr), (byte) 0b010));
    }

    @Test
    void jType_negative() {
        int instr = 0b1_1111111111_1_11111111_00000_1101111;
        assertEquals(-4, extend.operate(possibleImmediate(instr), (byte) 0b011));
    }

    @Test
    void uType() {
        int instr = (0x12345 << 12) | (1 << 7) | 0b0110111;
        assertEquals(0x12345000, extend.operate(possibleImmediate(instr), (byte) 0b100));
    }

    @Test
    void unknownImmSrc_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> extend.operate(0, (byte) 0b111));
    }
}