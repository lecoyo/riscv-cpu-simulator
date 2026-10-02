package com.lecoyo.riscvbackend.assembly.load;

import com.lecoyo.riscvbackend.assembly.Instruction;
import com.lecoyo.riscvbackend.assembly.ProgramData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InstructionLoaderTest {

    private static ProgramData load(String... lines) {
        return new InstructionLoader(lines).getObjects();
    }

    private static List<Integer> assemble(String... lines) {
        List<Instruction> insts = load(lines).getInstructions();
        return new InstructionEncoder().encode(insts);
    }

    private static int hex(String s) {
        return Integer.parseUnsignedInt(s, 16);
    }

    @ParameterizedTest(name = "{0} -> 0x{1}")
    @CsvSource(delimiter = '|', value = {
            "add t0, t1, t2        | 007302B3",
            "sub t0, t1, t2        | 407302B3",
            "addi t0, t0, 1        | 00128293",
            "addi t0, t0, -1       | FFF28293",
            "addi t0, t0, 0x7FF    | 7FF28293",
            "srai t0, t0, 2        | 4022D293",
            "lw t0, 4(sp)          | 00412283",
            "sw t0, 4(sp)          | 00512223",
            "lui t0, 1             | 000012B7",
            "beq t0, t1, 8         | 00628463",
            "jal ra, 8             | 008000EF",
            "jal 8                 | 008000EF",
            "jal x0, -4            | FFDFF06F",
    })
    void singleInstruction(String asm, String expectedHex) {
        assertEquals(hex(expectedHex), assemble(asm.trim()).get(0));
    }

    @Test
    void registerAliasesProduceSameEncoding() {
        assertEquals(assemble("add t0, t1, t2"), assemble("add x5, x6, x7"));
        assertEquals(assemble("addi sp, sp, -16"), assemble("addi x2, x2, -16"));
        assertEquals(assemble("addi s0, zero, 1"), assemble("addi fp, x0, 1"));
    }

    @Test
    void forwardBranch() {
        List<Integer> words = assemble(
                "beq t0, t1, end",
                "addi t0, t0, 1",
                "end:",
                "addi t1, t1, 1"
        );
        assertEquals(3, words.size());
        assertEquals(hex("00628463"), words.get(0));
    }

    @Test
    void backwardBranch() {
        List<Integer> words = assemble(
                "loop:",
                "addi t0, t0, 1",
                "bne t0, t1, loop"
        );
        assertEquals(hex("FE629EE3"), words.get(1));
    }

    @Test
    void labelAddressesAreCorrect() {
        ProgramData data = load(
                "start:",
                "addi t0, t0, 1",
                "addi t0, t0, 1",
                "middle:",
                "addi t0, t0, 1",
                "end:"
        );
        assertEquals(0, data.getLabels().get("start"));
        assertEquals(8, data.getLabels().get("middle"));
        assertEquals(12, data.getLabels().get("end"));
        assertEquals(3, data.getInstructions().size());
    }

    @Test
    void consecutiveLabelsShareAddress() {
        ProgramData data = load("a:", "b:", "addi t0, t0, 1");
        assertEquals(0, data.getLabels().get("a"));
        assertEquals(0, data.getLabels().get("b"));
    }

    @Test
    void jalToLabel() {
        List<Integer> words = assemble(
                "jal ra, func",
                "addi t0, t0, 1",
                "func:",
                "addi t1, t1, 1"
        );
        assertEquals(hex("008000EF"), words.get(0));
    }

    @Test
    void emptyLinesAndExtraWhitespaceAreIgnored() {
        List<Integer> words = assemble(
                "",
                "   addi   t0 ,  t0 ,  1   ",
                "   ",
                "add t0, t1, t2"
        );
        assertEquals(List.of(hex("00128293"), hex("007302B3")), words);
    }

    @Test
    void emptyProgram() {
        assertTrue(load().getInstructions().isEmpty());
        assertTrue(load("", "  ").getLabels().isEmpty());
    }

    // Fails unless InstructionLoader.getObjects() calls clean() on each line
    @Test
    void commentsAreStripped() {
        List<Integer> words = assemble(
                "# only a comment",
                "addi t0, t0, 1   # increment counter"
        );
        assertEquals(List.of(hex("00128293")), words);
    }

    @ParameterizedTest(name = "throws for: {0}")
    @ValueSource(strings = {
            "foo t0, t1, t2",
            "add t0, t1, q9",
            "add t0, t1",
            "lui t0",
            "addi t0, t0, 2048",
            "addi t0, t0, -2049",
            "slli t0, t0, 32",
            "slli t0, t0, -1",
            "lui t0, 1048576",
            "sw t0, 2048(sp)",
            "beq t0, t1, nowhere",
            "beq t0, t1, 3",
            "beq t0, t1, 4096",
    })
    void invalidInstructionsThrow(String asm) {
        assertThrows(IllegalArgumentException.class, () -> load(asm));
    }

    @Test
    void duplicateLabelThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> load("loop:", "addi t0, t0, 1", "loop:"));
    }

    @ParameterizedTest(name = "{0} -> 0x{1}")
    @CsvSource(delimiter = '|', value = {
            "addi t0, t0, 2047     | 7FF28293",
            "addi t0, t0, -2048    | 80028293",
            "lui t0, 1048575       | FFFFF2B7",
            "slli t0, t0, 31       | 01F29293",
    })
    void boundaryValues(String asm, String expectedHex) {
        assertEquals(hex(expectedHex), assemble(asm.trim()).get(0));
    }

    @Test
    void programOrderIsPreserved() {
        List<Integer> words = assemble(
                "addi t0, t0, 1",
                "add t0, t1, t2",
                "sw t0, 4(sp)"
        );
        assertEquals(List.of(hex("00128293"), hex("007302B3"), hex("00512223")), words);
    }
}