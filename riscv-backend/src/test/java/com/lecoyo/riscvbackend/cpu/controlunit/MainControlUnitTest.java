package com.lecoyo.riscvbackend.cpu.controlunit;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MainControlUnitTest {
    private final com.lecoyo.riscvbackend.cpu.controlunit.MainControlUnit mainControlUnit = new MainControlUnit();

    @Test
    void rType_add() {
        ControlSignals cs = mainControlUnit.decode((byte) 0b0110011);
        assertTrue(cs.isRegWrite());
        assertFalse(cs.isAluSrc());
        assertFalse(cs.isMemWrite());
        assertEquals(0b10, cs.getAluControl());
    }

    @Test
    void iTypeLoad() {
        ControlSignals cs = mainControlUnit.decode((byte) 0b0000011);
        assertTrue(cs.isRegWrite());
        assertTrue(cs.isAluSrc());
        assertTrue(cs.isResultSrc());
        assertFalse(cs.isMemWrite());
    }

    @Test
    void sType() {
        ControlSignals cs = mainControlUnit.decode((byte) 0b0100011);
        assertFalse(cs.isRegWrite());
        assertTrue(cs.isMemWrite());
        assertTrue(cs.isAluSrc());
    }

    @Test
    void unknownOpcode_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> mainControlUnit.decode((byte) 0b1111111));
    }
}