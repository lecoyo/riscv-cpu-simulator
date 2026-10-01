package com.lecoyo.riscvbackend.cpu;

import com.lecoyo.riscvbackend.cpu.SingleCycleProcessor.CpuSnapshot;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SingleCycleProcessorTest {
    private static final int MEMORY_SIZE = 256;

    private static int iType(int imm, int rs1, int funct3, int rd, int opcode) {
        return ((imm & 0xFFF) << 20) | (rs1 << 15) | (funct3 << 12) | (rd << 7) | opcode;
    }

    private static int rType(int funct7, int rs2, int rs1, int funct3, int rd, int opcode) {
        return (funct7 << 25) | (rs2 << 20) | (rs1 << 15) | (funct3 << 12) | (rd << 7) | opcode;
    }

    private static int sType(int imm, int rs2, int rs1, int funct3, int opcode) {
        return (((imm >> 5) & 0x7F) << 25) | (rs2 << 20) | (rs1 << 15)
                | (funct3 << 12) | ((imm & 0x1F) << 7) | opcode;
    }

    private static int addi(int rd, int rs1, int imm) { return iType(imm, rs1, 0b000, rd, 0b0010011); }
    private static int add(int rd, int rs1, int rs2)  { return rType(0b0000000, rs2, rs1, 0b000, rd, 0b0110011); }
    private static int sub(int rd, int rs1, int rs2)  { return rType(0b0100000, rs2, rs1, 0b000, rd, 0b0110011); }
    private static int lw(int rd, int rs1, int imm)   { return iType(imm, rs1, 0b010, rd, 0b0000011); }
    private static int sw(int rs2, int rs1, int imm)  { return sType(imm, rs2, rs1, 0b010, 0b0100011); }

    private static byte[] program(int... instructions) {
        byte[] bytes = new byte[instructions.length * 4];
        for (int i = 0; i < instructions.length; i++) {
            bytes[i * 4]     = (byte) (instructions[i]);
            bytes[i * 4 + 1] = (byte) (instructions[i] >>> 8);
            bytes[i * 4 + 2] = (byte) (instructions[i] >>> 16);
            bytes[i * 4 + 3] = (byte) (instructions[i] >>> 24);
        }
        return bytes;
    }

    private static List<CpuSnapshot> run(int... instructions) {
        return new SingleCycleProcessor(program(instructions), MEMORY_SIZE).simulate();
    }

    private static int[] finalRegisters(List<CpuSnapshot> trace) {
        return trace.get(trace.size() - 1).registers();
    }

    @Test
    void encoderProducesKnownInstructions() {
        assertEquals(0x00500093, addi(1, 0, 5));
        assertEquals(0x002081B3, add(3, 1, 2));
        assertEquals(0x402081B3, sub(3, 1, 2));
        assertEquals(0x00012183, lw(3, 2, 0));
        assertEquals(0x00112023, sw(1, 2, 0));
    }

    @Test
    void emptyProgramProducesEmptyTrace() {
        assertTrue(run().isEmpty());
    }

    @Test
    void traceHasOneSnapshotPerInstruction() {
        List<CpuSnapshot> trace = run(addi(1, 0, 1), addi(2, 0, 2), addi(3, 0, 3));
        assertEquals(3, trace.size());
    }

    @Test
    void snapshotsContainPcBeforeExecution() {
        List<CpuSnapshot> trace = run(addi(1, 0, 1), addi(2, 0, 2), addi(3, 0, 3));
        assertEquals(0, trace.get(0).pc());
        assertEquals(4, trace.get(1).pc());
        assertEquals(8, trace.get(2).pc());
    }

    @Test
    void snapshotsContainRawInstruction() {
        int first = addi(1, 0, 5);
        int second = add(2, 1, 1);
        List<CpuSnapshot> trace = run(first, second);
        assertEquals(first, trace.get(0).instruction());
        assertEquals(second, trace.get(1).instruction());
    }

    @Test
    void simulateTwiceSecondRunIsEmpty() {
        SingleCycleProcessor cpu = new SingleCycleProcessor(program(addi(1, 0, 1)), MEMORY_SIZE);
        assertEquals(1, cpu.simulate().size());
        assertTrue(cpu.simulate().isEmpty());
    }

    @Test
    void snapshotsContainControlSignals() {
        List<CpuSnapshot> trace = run(addi(1, 0, 1));
        assertNotNull(trace.get(0).controlSignals());
    }

    @Test
    void snapshotsContainIndependentRegisterStates() {
        List<CpuSnapshot> trace = run(addi(1, 0, 1), addi(1, 0, 2), addi(1, 0, 3));
        assertEquals(1, trace.get(0).registers()[1]);
        assertEquals(2, trace.get(1).registers()[1]);
        assertEquals(3, trace.get(2).registers()[1]);
    }

    @Test
    void snapshotContainsAluResultAndImmediate() {
        CpuSnapshot snapshot = run(addi(1, 0, 5)).get(0);
        assertEquals(5, snapshot.aluResult());
        assertEquals(5, snapshot.immExt());
    }

    @Test
    void snapshotAluResultOfAdd() {
        List<CpuSnapshot> trace = run(addi(1, 0, 5), addi(2, 0, 7), add(3, 1, 2));
        assertEquals(12, trace.get(2).aluResult());
    }

    @Test
    void addiWritesImmediateIntoRegister() {
        int[] regs = finalRegisters(run(addi(1, 0, 5)));
        assertEquals(5, regs[1]);
    }

    @Test
    void addiAddsToExistingRegisterValue() {
        int[] regs = finalRegisters(run(addi(1, 0, 10), addi(2, 1, 7)));
        assertEquals(17, regs[2]);
    }

    @Test
    void addAddsTwoRegisters() {
        int[] regs = finalRegisters(run(
                addi(1, 0, 5),
                addi(2, 0, 7),
                add(3, 1, 2)));
        assertEquals(12, regs[3]);
    }

    @Test
    void subSubtractsTwoRegisters() {
        int[] regs = finalRegisters(run(
                addi(1, 0, 10),
                addi(2, 0, 3),
                sub(3, 1, 2)));
        assertEquals(7, regs[3]);
    }

    @Test
    void subWithNegativeResult() {
        int[] regs = finalRegisters(run(
                addi(1, 0, 3),
                addi(2, 0, 10),
                sub(3, 1, 2)));
        assertEquals(-7, regs[3]);
    }

    @Test
    void addiWithNegativeImmediate() {
        int[] regs = finalRegisters(run(addi(1, 0, -1)));
        assertEquals(-1, regs[1]);
    }

    @Test
    void aluResultLargerThanDataMemory() {
        SingleCycleProcessor cpu = new SingleCycleProcessor(program(addi(1, 0, 2000)), 16);
        List<CpuSnapshot> trace = cpu.simulate();
        assertEquals(2000, trace.get(0).registers()[1]);
    }

    @Test
    void dependentInstructionsSeeEarlierResults() {
        int[] regs = finalRegisters(run(
                addi(1, 0, 1),
                add(1, 1, 1),
                add(1, 1, 1),
                add(1, 1, 1)));
        assertEquals(8, regs[1]);
    }

    @Test
    void writeToX0IsIgnored() {
        int[] regs = finalRegisters(run(addi(0, 0, 5)));
        assertEquals(0, regs[0]);
    }

    @Test
    void otherRegistersStayZero() {
        int[] regs = finalRegisters(run(addi(4, 0, 9)));
        for (int i = 0; i < 32; i++) {
            if (i != 4) {
                assertEquals(0, regs[i], "x" + i + " should stay 0");
            }
        }
    }

    @Test
    void storeThenLoadReturnsSameValue() {
        int[] regs = finalRegisters(run(
                addi(1, 0, 42),
                addi(2, 0, 16),
                sw(1, 2, 0),
                lw(3, 2, 0)));
        assertEquals(42, regs[3]);
    }

    @Test
    void storeAndLoadWithOffset() {
        int[] regs = finalRegisters(run(
                addi(1, 0, 99),
                addi(2, 0, 16),
                sw(1, 2, 8),
                lw(3, 2, 8)));
        assertEquals(99, regs[3]);
    }

    @Test
    void storesToDifferentAddressesAreIndependent() {
        int[] regs = finalRegisters(run(
                addi(1, 0, 11),
                addi(2, 0, 22),
                addi(5, 0, 32),
                sw(1, 5, 0),
                sw(2, 5, 4),
                lw(3, 5, 0),
                lw(4, 5, 4)));
        assertEquals(11, regs[3]);
        assertEquals(22, regs[4]);
    }

    @Test
    void loadFromUntouchedMemoryReturnsZero() {
        int[] regs = finalRegisters(run(
                addi(3, 0, 5),
                addi(2, 0, 64),
                lw(3, 2, 0)));
        assertEquals(0, regs[3]);
    }

    @Test
    void memWriteFlagIsOnlySetForStore() {
        List<CpuSnapshot> trace = run(
                addi(1, 0, 42),
                addi(2, 0, 16),
                sw(1, 2, 0),
                lw(3, 2, 0),
                add(4, 1, 1));

        assertFalse(trace.get(0).memWrite());
        assertFalse(trace.get(1).memWrite());
        assertTrue(trace.get(2).memWrite());
        assertFalse(trace.get(3).memWrite());
        assertFalse(trace.get(4).memWrite());
    }

    @Test
    void storeDoesNotChangeRegisters() {
        int[] regs = finalRegisters(run(
                addi(1, 0, 42),
                addi(2, 0, 16),
                sw(1, 2, 0)));
        assertEquals(42, regs[1]);
        assertEquals(16, regs[2]);
        assertEquals(0, regs[3]);
    }
}