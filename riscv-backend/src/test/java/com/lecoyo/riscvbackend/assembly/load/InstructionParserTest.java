package com.lecoyo.riscvbackend.assembly.load;

import com.lecoyo.riscvbackend.assembly.Instruction;
import com.lecoyo.riscvbackend.assembly.types.InstType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class InstructionParserTest {

    private static InstructionParser parser(Map<String, Integer> labels) {
        return new InstructionParser(new HashMap<>(labels));
    }

    private static InstructionParser parser() {
        return parser(Map.of());
    }

    private static int hex(String s) {
        return Integer.parseUnsignedInt(s, 16);
    }

    private static int encode(int pc, String line) {
        return parser().parse(pc, line).encode();
    }

    @ParameterizedTest(name = "{0} -> {1}")
    @CsvSource(delimiter = '|', value = {
            "add t0, t1, t2      | RType",
            "mul t0, t1, t2      | RType",
            "addi t0, t0, 1      | IType",
            "lw t0, 4(sp)        | IType",
            "jalr ra, 0(t0)      | IType",
            "sw t0, 4(sp)        | SType",
            "beq t0, t1, 8       | BType",
            "lui t0, 1           | UType",
            "auipc t0, 1         | UType",
            "jal ra, 8           | JType",
    })
    void returnsCorrectClass(String asm, String simpleName) {
        Instruction inst = parser().parse(0, asm.trim());
        assertEquals(simpleName, inst.getClass().getSimpleName());
    }

    @ParameterizedTest(name = "{0} -> 0x{1}")
    @CsvSource(delimiter = '|', value = {
            "add t0, t1, t2      | 007302B3",
            "sub t0, t1, t2      | 407302B3",
            "mul t0, t1, t2      | 027302B3",
            "sra t0, t1, t2      | 407352B3",
            "addi t0, t0, 1      | 00128293",
            "addi t0, t0, -1     | FFF28293",
            "addi t0, t0, 0x10   | 01028293",
            "slli t0, t0, 3      | 00329293",
            "srli t0, t0, 3      | 0032D293",
            "srai t0, t0, 2      | 4022D293",
            "lw t0, 4(sp)        | 00412283",
            "jalr ra, 0(t0)      | 000280E7",
            "sw t0, 4(sp)        | 00512223",
            "sw t0, -4(sp)       | FE512E23",
            "lui t0, 1           | 000012B7",
            "auipc t0, 1         | 00001297",
    })
    void encodingOfNonJumpInstructions(String asm, String expectedHex) {
        assertEquals(hex(expectedHex), encode(0, asm.trim()));
    }

    @Test
    void branchWithNumericOffset() {
        assertEquals(hex("00628463"), encode(0, "beq t0, t1, 8"));
    }

    @Test
    void numericOffsetIsRelativeToPc() {
        assertEquals(encode(0, "beq t0, t1, 8"), encode(100, "beq t0, t1, 8"));
    }

    @Test
    void branchForwardToLabel() {
        InstructionParser p = parser(Map.of("end", 8));
        assertEquals(hex("00628463"), p.parse(0, "beq t0, t1, end").encode());
    }

    @Test
    void branchBackwardToLabel() {
        InstructionParser p = parser(Map.of("loop", 0));
        assertEquals(hex("FE629EE3"), p.parse(4, "bne t0, t1, loop").encode());
    }

    @Test
    void jalWithRdAndLabel() {
        InstructionParser p = parser(Map.of("func", 8));
        assertEquals(hex("008000EF"), p.parse(0, "jal ra, func").encode());
    }

    @Test
    void jalWithoutRdDefaultsToRa() {
        assertEquals(encode(0, "jal ra, 8"), encode(0, "jal 8"));
    }

    @Test
    void jalBackward() {
        assertEquals(hex("FFDFF06F"), encode(0, "jal x0, -4"));
    }

    @ParameterizedTest(name = "{0} -> 0x{1}")
    @CsvSource(delimiter = '|', value = {
            "addi t0, t0, 2047   | 7FF28293",
            "addi t0, t0, -2048  | 80028293",
            "slli t0, t0, 31     | 01F29293",
            "lui t0, 1048575     | FFFFF2B7",
            "lui t0, 0           | 000002B7",
            "sw t0, 2047(sp)     | 7E512FA3",
            "sw t0, -2048(sp)    | 80512023",
            "beq t0, t1, 4094    | 7E628FE3",
            "beq t0, t1, -4096   | 80628063",
            "jal x0, 1048574     | 7FFFF06F",
            "jal x0, -1048576    | 8000006F",
    })
    void boundaryValues(String asm, String expectedHex) {
        assertEquals(hex(expectedHex), encode(0, asm.trim()));
    }

    @Test
    void registerAliasesAreEquivalent() {
        assertEquals(encode(0, "add t0, t1, t2"), encode(0, "add x5, x6, x7"));
        assertEquals(encode(0, "add s0, s1, a0"), encode(0, "add fp, x9, x10"));
        assertEquals(encode(0, "add t3, s2, s11"), encode(0, "add x28, x18, x27"));
        assertEquals(encode(0, "addi sp, zero, 1"), encode(0, "addi x2, x0, 1"));
    }

    @Test
    void immediateFormats() {
        assertEquals(encode(0, "addi t0, t0, 16"), encode(0, "addi t0, t0, 0x10"));
        assertEquals(encode(0, "addi t0, t0, -16"), encode(0, "addi t0, t0, -0x10"));
    }

    @Test
    void extraWhitespaceAndCommasAreTolerated() {
        assertEquals(encode(0, "add t0, t1, t2"), encode(0, "  add   t0 ,t1,   t2  "));
        assertEquals(encode(0, "lw t0, 4(sp)"), encode(0, "lw t0,4( sp )"));
    }

    @ParameterizedTest(name = "throws for: {0}")
    @ValueSource(strings = {
            "foo t0, t1, t2",
            "add t0, t1, q9",
            "add t0, t1",
            "addi t0, t0",
            "lw t0",
            "sw t0, 4",
            "beq t0, t1",
            "lui t0",
            "jal",
            "addi t0, t0, abc",
            "addi t0, t0, 2048",
            "addi t0, t0, -2049",
            "slli t0, t0, 32",
            "slli t0, t0, -1",
            "lw t0, 2048(sp)",
            "lui t0, 1048576",
            "lui t0, -1",
            "sw t0, 2048(sp)",
            "sw t0, -2049(sp)",
            "beq t0, t1, nowhere",
            "beq t0, t1, 3",
            "beq t0, t1, 4096",
            "beq t0, t1, -4098",
            "jal ra, 1048576",
            "jal ra, 5",
    })
    void invalidInputThrows(String asm) {
        assertThrows(IllegalArgumentException.class, () -> parser().parse(0, asm));
    }

    @Test
    void errorMessageContainsMnemonicForMissingOperands() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> parser().parse(0, "add t0, t1"));
        assertTrue(e.getMessage().contains("add"));
    }

    @Test
    void cleanSplitsIntoMnemonicAndOperands() {
        assertArrayEquals(new String[]{"addi", "t0", "t0", "1"}, parser().clean("addi t0, t0, 1"));
        assertArrayEquals(new String[]{"lw", "t0", "4", "sp"}, parser().clean("lw t0, 4(sp)"));
        assertArrayEquals(new String[]{"lw", "t0", "4", "sp"}, parser().clean("  lw   t0 ,  4 ( sp )  "));
    }

    @ParameterizedTest
    @CsvSource({"loop:,true", "end:,true", "addi t0 t0 1,false", "loop,false", "':',true"})
    void isLabel(String line, boolean expected) {
        assertEquals(expected, InstructionParser.isLabel(line));
    }

    @ParameterizedTest(name = "{0} -> {1}")
    @CsvSource({"add,R", "addi,I", "lw,I", "sw,S", "beq,B", "lui,U", "jal,J"})
    void getTypeByMnemonic(String asm, String expectedType) {
        Instruction inst = parser(Map.of()).parse(0, asm.equals("jal") ? "jal 4"
                : asm.equals("lui") ? "lui t0, 1"
                : asm.equals("beq") ? "beq t0, t1, 4"
                : asm.equals("sw") ? "sw t0, 0(sp)"
                : asm.equals("lw") ? "lw t0, 0(sp)"
                : asm.equals("addi") ? "addi t0, t0, 1"
                : "add t0, t1, t2");
        assertEquals(InstType.valueOf(expectedType), InstructionParser.getType(inst));
    }
}
