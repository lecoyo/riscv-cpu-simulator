package com.lecoyo.riscvbackend.cpu.components;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ALUTest {
    private final ALU alu = new ALU();

    @Test
    void add() {
        assertEquals(15, alu.operate(10, 5, (byte) 0b0000));
    }

    @Test
    void sub() {
        assertEquals(5, alu.operate(10, 5, (byte) 0b1000));
    }

    @Test
    void sll() {
        assertEquals(20, alu.operate(5, 2, (byte) 0b0001));
    }

    @Test
    void slt_true() {
        assertEquals(1, alu.operate(-1, 1, (byte) 0b0010));
    }

    @Test
    void slt_false() {
        assertEquals(0, alu.operate(5, 1, (byte) 0b0010));
    }

    @Test
    void sltu_treatsNegativeAsLarge() {
        assertEquals(0, alu.operate(-1, 1, (byte) 0b0011));
    }

    @Test
    void xor() {
        assertEquals(0b0110, alu.operate(0b0101, 0b0011, (byte) 0b0100));
    }

    @Test
    void srl_fillsWithZeros() {
        assertEquals(0x7FFFFFFF, alu.operate(-1, 1, (byte) 0b0101));
    }

    @Test
    void sra_preservesSign() {
        assertEquals(-1, alu.operate(-1, 1, (byte) 0b1001));
    }

    @Test
    void or() {
        assertEquals(0b0111, alu.operate(0b0101, 0b0011, (byte) 0b0110));
    }

    @Test
    void and() {
        assertEquals(0b0001, alu.operate(0b0101, 0b0011, (byte) 0b0111));
    }

    @Test
    void shiftAmount_onlyUsesLower5Bits() {
        assertEquals(10, alu.operate(5, 33, (byte) 0b0001));
    }

    @Test
    void unknownAluControl_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> alu.operate(1, 1, (byte) 0b1111));
    }
}