package com.lecoyo.riscvbackend.cpu.components;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataMemoryTest {
    private static final int SIZE = 64;
    private DataMemory memory;

    @BeforeEach
    void setUp() {
        memory = new DataMemory(SIZE);
    }

    @Test
    void newMemoryIsInitializedWithZeros() {
        for (int addr = 0; addr <= SIZE - 4; addr += 4) {
            assertEquals(0, memory.operate(addr, 0, false));
        }
    }

    @Test
    void writeReturnsZero() {
        assertEquals(0, memory.operate(0, 0xDEADBEEF, true));
    }

    @Test
    void writeThenReadReturnsSameValue() {
        memory.operate(8, 0x12345678, true);
        assertEquals(0x12345678, memory.operate(8, 0, false));
    }

    @Test
    void writeNegativeValue() {
        memory.operate(0, -1, true);
        assertEquals(-1, memory.operate(0, 0, false));
    }

    @Test
    void writeIntMinAndMaxValue() {
        memory.operate(0, Integer.MIN_VALUE, true);
        memory.operate(4, Integer.MAX_VALUE, true);
        assertEquals(Integer.MIN_VALUE, memory.operate(0, 0, false));
        assertEquals(Integer.MAX_VALUE, memory.operate(4, 0, false));
    }

    @Test
    void readDoesNotModifyMemory() {
        memory.operate(0, 0xCAFEBABE, true);
        memory.operate(0, 0x11111111, false);
        assertEquals(0xCAFEBABE, memory.operate(0, 0, false));
    }

    @Test
    void overwriteReplacesPreviousValue() {
        memory.operate(12, 0xAAAAAAAA, true);
        memory.operate(12, 0x55555555, true);
        assertEquals(0x55555555, memory.operate(12, 0, false));
    }

    @Test
    void differentAddressesAreIndependent() {
        memory.operate(0, 0x11111111, true);
        memory.operate(4, 0x22222222, true);
        memory.operate(8, 0x33333333, true);

        assertEquals(0x11111111, memory.operate(0, 0, false));
        assertEquals(0x22222222, memory.operate(4, 0, false));
        assertEquals(0x33333333, memory.operate(8, 0, false));
    }

    @Test
    void writeUsesBigEndianByteOrder() {
        memory.operate(0, 0x01020304, true);

        assertEquals(0x01000000, memory.operate(0, 0, false) & 0xFF000000);
        assertEquals(0x02030400, memory.operate(1, 0, false));
        assertEquals(0x04000000, memory.operate(3, 0, false));
    }

    @Test
    void unalignedWriteAndReadWork() {
        memory.operate(1, 0xAABBCCDD, true);
        assertEquals(0xAABBCCDD, memory.operate(1, 0, false));
    }

    @Test
    void unalignedWriteOverlapsNeighbourWords() {
        memory.operate(0, 0xFFFFFFFF, true);
        memory.operate(2, 0x00000000, true);

        assertEquals(0xFFFF0000, memory.operate(0, 0, false));
    }

    @Test
    void accessAtLastValidAddressWorks() {
        int lastAddr = SIZE - 4;
        memory.operate(lastAddr, 0x0BADF00D, true);
        assertEquals(0x0BADF00D, memory.operate(lastAddr, 0, false));
    }

    @Test
    void readOutOfBoundsThrows() {
        assertThrows(ArrayIndexOutOfBoundsException.class,
                () -> memory.operate(SIZE - 3, 0, false));
        assertThrows(ArrayIndexOutOfBoundsException.class,
                () -> memory.operate(SIZE, 0, false));
    }

    @Test
    void writeOutOfBoundsThrows() {
        assertThrows(ArrayIndexOutOfBoundsException.class,
                () -> memory.operate(SIZE - 3, 1, true));
        assertThrows(ArrayIndexOutOfBoundsException.class,
                () -> memory.operate(SIZE, 1, true));
    }

    @Test
    void negativeAddressThrows() {
        assertThrows(ArrayIndexOutOfBoundsException.class,
                () -> memory.operate(-1, 0, false));
        assertThrows(ArrayIndexOutOfBoundsException.class,
                () -> memory.operate(-4, 1, true));
    }

    @Test
    void zeroSizeMemoryAlwaysThrows() {
        DataMemory empty = new DataMemory(0);
        assertThrows(ArrayIndexOutOfBoundsException.class,
                () -> empty.operate(0, 0, false));
    }

    @Test
    void negativeSizeThrows() {
        assertThrows(NegativeArraySizeException.class, () -> new DataMemory(-1));
    }
}
