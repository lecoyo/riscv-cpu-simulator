package com.lecoyo.riscvbackend.cpu.controlunit;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ALUControlTest {
    private final ALUControl aluControl = new ALUControl();

    @Test
    void rType_addVsSub() {
        assertEquals(0b0000, aluControl.decode((byte) 0b10, (byte) 0b000, (byte) 0b0000000));
        assertEquals(0b1000, aluControl.decode((byte) 0b10, (byte) 0b000, (byte) 0b0100000));
    }

    @Test
    void rType_srlVsSra() {
        assertEquals(0b0101, aluControl.decode((byte) 0b10, (byte) 0b101, (byte) 0b0000000));
        assertEquals(0b1001, aluControl.decode((byte) 0b10, (byte) 0b101, (byte) 0b0100000));
    }

    @Test
    void iType_addiNeverBecomesSub() {
        assertEquals(0b0000, aluControl.decode((byte) 0b11, (byte) 0b000, (byte) 0b0100000));
    }

    @Test
    void iType_sraiVsSrli() {
        assertEquals(0b0101, aluControl.decode((byte) 0b11, (byte) 0b101, (byte) 0b0000000));
        assertEquals(0b1001, aluControl.decode((byte) 0b11, (byte) 0b101, (byte) 0b0100000));
    }

    @Test
    void loadStore_alwaysAdd() {
        assertEquals(0b0000, aluControl.decode((byte) 0b00, (byte) 0, (byte) 0));
    }

    @Test
    void branch_alwaysSub() {
        assertEquals(0b1000, aluControl.decode((byte) 0b01, (byte) 0, (byte) 0));
    }

    @Test
    void unknownAluOp_throws() {
        assertThrows(IllegalStateException.class,
                () -> aluControl.decode((byte) 0b111, (byte) 0, (byte) 0));
    }
}