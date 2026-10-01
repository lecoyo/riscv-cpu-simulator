package com.lecoyo.riscvbackend.cpu.components;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisterFileTest {
    private RegisterFile rf;

    @BeforeEach
    void setUp() {
        rf = new RegisterFile();
    }

    @Test
    void hasThirtyTwoRegisters() {
        assertEquals(32, rf.getRegisters().length);
    }

    @Test
    void allRegistersInitializedToZero() {
        for (int i = 0; i < 32; i++) {
            assertEquals(0, rf.getRegisters()[i], "x" + i + " should be 0");
        }
    }

    @Test
    void writeThenReadReturnsValue() {
        rf.write(5, 42, true);
        assertEquals(42, rf.read(5, 0)[0]);
    }

    @Test
    void readReturnsBothRegistersInOrder() {
        rf.write(1, 111, true);
        rf.write(2, 222, true);

        int[] result = rf.read(1, 2);
        assertEquals(2, result.length);
        assertEquals(111, result[0]);
        assertEquals(222, result[1]);

        int[] swapped = rf.read(2, 1);
        assertEquals(222, swapped[0]);
        assertEquals(111, swapped[1]);
    }

    @Test
    void readSameRegisterTwiceReturnsSameValue() {
        rf.write(7, 0xCAFEBABE, true);
        int[] result = rf.read(7, 7);
        assertEquals(0xCAFEBABE, result[0]);
        assertEquals(0xCAFEBABE, result[1]);
    }

    @Test
    void writeDisabledDoesNotChangeRegister() {
        rf.write(3, 99, false);
        assertEquals(0, rf.read(3, 3)[0]);

        rf.write(3, 10, true);
        rf.write(3, 99, false);
        assertEquals(10, rf.read(3, 3)[0]);
    }

    @Test
    void writeToX0IsIgnored() {
        rf.write(0, 1234, true);
        assertEquals(0, rf.read(0, 0)[0]);
        assertEquals(0, rf.getRegisters()[0]);
    }

    @Test
    void x0StaysZeroAfterManyWrites() {
        for (int i = 1; i <= 10; i++) {
            rf.write(0, i, true);
        }
        assertEquals(0, rf.read(0, 0)[1]);
    }

    @Test
    void overwriteReplacesValue() {
        rf.write(10, 1, true);
        rf.write(10, 2, true);
        assertEquals(2, rf.read(10, 10)[0]);
    }

    @Test
    void writeDoesNotAffectOtherRegisters() {
        rf.write(4, 0xFF, true);
        for (int i = 0; i < 32; i++) {
            if (i != 4) {
                assertEquals(0, rf.getRegisters()[i], "x" + i + " was changed");
            }
        }
    }

    @Test
    void canWriteAndReadAllRegistersExceptX0() {
        for (int i = 1; i < 32; i++) {
            rf.write(i, i * 100, true);
        }
        for (int i = 1; i < 32; i++) {
            assertEquals(i * 100, rf.read(i, i)[0]);
        }
        assertEquals(0, rf.read(0, 0)[0]);
    }

    @Test
    void handlesNegativeAndExtremeValues() {
        rf.write(1, -1, true);
        rf.write(2, Integer.MIN_VALUE, true);
        rf.write(3, Integer.MAX_VALUE, true);

        assertEquals(-1, rf.read(1, 1)[0]);
        assertEquals(Integer.MIN_VALUE, rf.read(2, 2)[0]);
        assertEquals(Integer.MAX_VALUE, rf.read(3, 3)[0]);
    }

    @Test
    void readReturnsNewArrayEachTime() {
        rf.write(1, 5, true);
        int[] first = rf.read(1, 1);
        first[0] = 999;
        assertEquals(5, rf.read(1, 1)[0]);
        assertNotSame(first, rf.read(1, 1));
    }

    @Test
    void getRegistersReflectsWrites() {
        rf.write(9, 77, true);
        assertEquals(77, rf.getRegisters()[9]);
    }

    @Test
    void readInvalidAddressThrows() {
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> rf.read(32, 0));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> rf.read(0, 32));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> rf.read(-1, 0));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> rf.read(0, -1));
    }

    @Test
    void writeInvalidAddressThrowsWhenEnabled() {
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> rf.write(32, 1, true));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> rf.write(-1, 1, true));
    }

    @Test
    void writeInvalidAddressIsIgnoredWhenDisabled() {
        assertDoesNotThrow(() -> rf.write(32, 1, false));
        assertDoesNotThrow(() -> rf.write(-1, 1, false));
    }
}