package com.lecoyo.riscvbackend.cpu.components;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InstructionMemoryTest {
    @Test
    void readsWordInLittleEndianOrder() {
        InstructionMemory im = new InstructionMemory(new byte[] {
                0x78, 0x56, 0x34, 0x12
        });
        assertEquals(0x12345678, im.read(0));
    }

    @Test
    void readsRealInstruction() {
        InstructionMemory im = new InstructionMemory(new byte[] {
                (byte) 0x93, 0x00, 0x50, 0x00
        });
        assertEquals(0x00500093, im.read(0));
    }

    @Test
    void readsSecondInstructionAtAddress4() {
        InstructionMemory im = new InstructionMemory(new byte[] {
                (byte) 0x93, 0x00, 0x50, 0x00,
                0x13, 0x01, 0x30, 0x00
        });
        assertEquals(0x00500093, im.read(0));
        assertEquals(0x00300113, im.read(4));
    }

    @Test
    void bytesAreTreatedAsUnsigned() {
        InstructionMemory im = new InstructionMemory(new byte[] {
                (byte) 0x80, (byte) 0xFF, (byte) 0x80, (byte) 0xFF
        });
        assertEquals(0xFF80FF80, im.read(0));
    }

    @Test
    void allBytesFFReturnsMinusOne() {
        InstructionMemory im = new InstructionMemory(new byte[] {
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF
        });
        assertEquals(-1, im.read(0));
    }

    @Test
    void allBytesZeroReturnsZero() {
        InstructionMemory im = new InstructionMemory(new byte[4]);
        assertEquals(0, im.read(0));
    }

    @Test
    void highBitSetResultsInNegativeInt() {
        InstructionMemory im = new InstructionMemory(new byte[] {
                0x00, 0x00, 0x00, (byte) 0x80
        });
        assertEquals(Integer.MIN_VALUE, im.read(0));
    }

    @Test
    void readDoesNotModifyMemory() {
        InstructionMemory im = new InstructionMemory(new byte[] {
                0x01, 0x02, 0x03, 0x04
        });
        int first = im.read(0);
        int second = im.read(0);
        assertEquals(first, second);
    }

    @Test
    void readAtLastValidAddressWorks() {
        InstructionMemory im = new InstructionMemory(new byte[] {
                0, 0, 0, 0,
                0x04, 0x03, 0x02, 0x01
        });
        assertEquals(0x01020304, im.read(4));
    }

    @Test
    void unalignedReadStillWorks() {
        InstructionMemory im = new InstructionMemory(new byte[] {
                0x00, 0x11, 0x22, 0x33, 0x44
        });
        assertEquals(0x44332211, im.read(1));
    }

    @Test
    void readOutOfBoundsThrows() {
        InstructionMemory im = new InstructionMemory(new byte[8]);
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> im.read(5)); // 5..8
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> im.read(8));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> im.read(100));
    }

    @Test
    void negativeAddressThrows() {
        InstructionMemory im = new InstructionMemory(new byte[8]);
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> im.read(-4));
    }

    @Test
    void emptyProgramAlwaysThrowsOnRead() {
        InstructionMemory im = new InstructionMemory(new byte[0]);
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> im.read(0));
    }

    @Test
    void getProgramSizeReturnsLengthInBytes() {
        assertEquals(0, new InstructionMemory(new byte[0]).getProgramSize());
        assertEquals(4, new InstructionMemory(new byte[4]).getProgramSize());
        assertEquals(128, new InstructionMemory(new byte[128]).getProgramSize());
    }

    @Test
    void programArrayIsNotCopied() {
        byte[] program = {0x01, 0x00, 0x00, 0x00};
        InstructionMemory im = new InstructionMemory(program);
        assertEquals(1, im.read(0));

        program[0] = 0x02;
        assertEquals(2, im.read(0));
    }

    @Test
    void nullProgramThrowsOnUse() {
        InstructionMemory im = new InstructionMemory(null);
        assertThrows(NullPointerException.class, im::getProgramSize);
        assertThrows(NullPointerException.class, () -> im.read(0));
    }
}