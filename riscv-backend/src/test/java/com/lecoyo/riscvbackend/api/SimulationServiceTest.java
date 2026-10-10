package com.lecoyo.riscvbackend.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class SimulationServiceTest {

    @Autowired
    private SimulationService service;

    private int[] registersAtLastStep(String code) {
        List<Package> result = service.simulate(code);
        return result.get(result.size() - 1).cpuSnapshot().registers();
    }

    private List<String> instructionTypes(String code) {
        return service.simulate(code).stream()
                .map(p -> p.cpuSnapshot().instructionType())
                .toList();
    }

    @Test
    void addi_positive() {
        int[] regs = registersAtLastStep("addi x1, x0, 5");
        assertEquals(5, regs[1]);
    }

    @Test
    void addi_negative() {
        int[] regs = registersAtLastStep("addi x1, x0, -5");
        assertEquals(-5, regs[1]);
    }

    @Test
    void addi_zero() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, 7
            addi x2, x1, 0
            """);
        assertEquals(7, regs[2]);
    }

    @Test
    void andi() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, 12
            andi x2, x1, 10
            """);
        assertEquals(8, regs[2]);
    }

    @Test
    void ori() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, 12
            ori x2, x1, 3
            """);
        assertEquals(15, regs[2]);
    }

    @Test
    void xori() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, 12
            xori x2, x1, 10
            """);
        assertEquals(6, regs[2]);
    }

    @Test
    void xori_minusOneInvertsBits() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, 5
            xori x2, x1, -1
            """);
        assertEquals(-6, regs[2]);
    }

    @Test
    void slti_true() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, 3
            slti x2, x1, 5
            """);
        assertEquals(1, regs[2]);
    }

    @Test
    void slti_false() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, 10
            slti x2, x1, 5
            """);
        assertEquals(0, regs[2]);
    }

    @Test
    void slti_negativeIsLess() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, -1
            slti x2, x1, 1
            """);
        assertEquals(1, regs[2]);
    }

    @Test
    void sltiu_false() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, -1
            sltiu x2, x1, 5
            """);
        assertEquals(0, regs[2]);
    }

    @Test
    void sltiu_true() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, 3
            sltiu x2, x1, 5
            """);
        assertEquals(1, regs[2]);
    }

    @Test
    void slli() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, 1
            slli x2, x1, 4
            """);
        assertEquals(16, regs[2]);
    }

    @Test
    void slli_maxShift() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, 1
            slli x2, x1, 31
            """);
        assertEquals(Integer.MIN_VALUE, regs[2]);
    }

    @Test
    void srli() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, 16
            srli x2, x1, 4
            """);
        assertEquals(1, regs[2]);
    }

    @Test
    void srli_negativeIsLogical() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, -1
            srli x2, x1, 28
            """);
        assertEquals(15, regs[2]);
    }

    @Test
    void srai() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, -16
            srai x2, x1, 2
            """);
        assertEquals(-4, regs[2]);
    }

    @Test
    void srai_positive() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, 16
            srai x2, x1, 2
            """);
        assertEquals(4, regs[2]);
    }

    @Test
    void add() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, 7
            addi x2, x0, 8
            add x3, x1, x2
            """);
        assertEquals(15, regs[3]);
    }

    @Test
    void add_overflowWraps() {
        int[] regs = registersAtLastStep("""
            lui x1, 524288
            addi x1, x1, -1
            addi x2, x1, 1
            """);
        assertEquals(Integer.MIN_VALUE, regs[2]);
    }

    @Test
    void sub() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, 10
            addi x2, x0, 3
            sub x3, x1, x2
            """);
        assertEquals(7, regs[3]);
    }

    @Test
    void sub_negativeResult() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, 3
            addi x2, x0, 10
            sub x3, x1, x2
            """);
        assertEquals(-7, regs[3]);
    }

    @Test
    void and() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, 12
            addi x2, x0, 10
            and x3, x1, x2
            """);
        assertEquals(8, regs[3]);
    }

    @Test
    void or() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, 12
            addi x2, x0, 3
            or x3, x1, x2
            """);
        assertEquals(15, regs[3]);
    }

    @Test
    void xor() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, 12
            addi x2, x0, 10
            xor x3, x1, x2
            """);
        assertEquals(6, regs[3]);
    }

    @Test
    void sll() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, 1
            addi x2, x0, 4
            sll x3, x1, x2
            """);
        assertEquals(16, regs[3]);
    }

    @Test
    void srl() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, -1
            addi x2, x0, 28
            srl x3, x1, x2
            """);
        assertEquals(15, regs[3]);
    }

    @Test
    void sra() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, -16
            addi x2, x0, 2
            sra x3, x1, x2
            """);
        assertEquals(-4, regs[3]);
    }

    @Test
    void slt_true() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, -1
            addi x2, x0, 1
            slt x3, x1, x2
            """);
        assertEquals(1, regs[3]);
    }

    @Test
    void slt_false() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, 5
            addi x2, x0, 1
            slt x3, x1, x2
            """);
        assertEquals(0, regs[3]);
    }

    @Test
    void slt_equal() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, 5
            addi x2, x0, 5
            slt x3, x1, x2
            """);
        assertEquals(0, regs[3]);
    }

    @Test
    void sltu_false() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, -1
            addi x2, x0, 1
            sltu x3, x1, x2
            """);
        assertEquals(0, regs[3]);
    }

    @Test
    void sltu_true() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, 1
            addi x2, x0, -1
            sltu x3, x1, x2
            """);
        assertEquals(1, regs[3]);
    }

    @Test
    void lui() {
        int[] regs = registersAtLastStep("lui x1, 74565");
        assertEquals(0x12345000, regs[1]);
    }

    @Test
    void lui_negative() {
        int[] regs = registersAtLastStep("lui x1, 1048575");
        assertEquals(0xFFFFF000, regs[1]);
    }

    @Test
    void lui_thenAddi() {
        int[] regs = registersAtLastStep("""
            lui x1, 74565
            addi x1, x1, 1656
            """);
        assertEquals(0x12345678, regs[1]);
    }

    @Test
    void auipc_atZero() {
        int[] regs = registersAtLastStep("auipc x1, 1");
        assertEquals(4096, regs[1]);
    }

    @Test
    void auipc_atNonZeroPc() {
        int[] regs = registersAtLastStep("""
            addi x5, x0, 0
            auipc x1, 2
            """);
        assertEquals(4 + 8192, regs[1]);
    }

    @Test
    void auipc_zeroImmediateGivesPc() {
        int[] regs = registersAtLastStep("""
            addi x5, x0, 0
            addi x5, x0, 0
            auipc x1, 0
            """);
        assertEquals(8, regs[1]);
    }

    @Test
    void sw_then_lw() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, 42
            addi x2, x0, 100
            sw x1, 0(x2)
            lw x3, 0(x2)
            """);
        assertEquals(42, regs[3]);
    }

    @Test
    void sw_withOffset() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, 7
            addi x2, x0, 100
            sw x1, 4(x2)
            lw x3, 4(x2)
            """);
        assertEquals(7, regs[3]);
    }

    @Test
    void sw_withNegativeOffset() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, 42
            addi x2, x0, 104
            sw x1, -4(x2)
            lw x3, -4(x2)
            """);
        assertEquals(42, regs[3]);
    }

    @Test
    void sw_differentAddressesDoNotOverlap() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, 11
            addi x2, x0, 22
            addi x5, x0, 100
            sw x1, 0(x5)
            sw x2, 4(x5)
            lw x3, 0(x5)
            lw x4, 4(x5)
            """);
        assertEquals(11, regs[3]);
        assertEquals(22, regs[4]);
    }

    @Test
    void sb_then_lw() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, -1
            addi x2, x0, 100
            sb x1, 0(x2)
            lw x3, 0(x2)
            """);
        assertEquals(255, regs[3]);
    }

    @Test
    void sb_preservesUpperBytes() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, -1
            addi x2, x0, 100
            sw x1, 0(x2)
            addi x4, x0, 0
            sb x4, 0(x2)
            lw x3, 0(x2)
            """);
        assertEquals(0xFFFFFF00, regs[3]);
    }

    @Test
    void sh_then_lw() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, -1
            addi x2, x0, 100
            sh x1, 0(x2)
            lw x3, 0(x2)
            """);
        assertEquals(65535, regs[3]);
    }

    @Test
    void sh_preservesUpperHalf() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, -1
            addi x2, x0, 100
            sw x1, 0(x2)
            addi x4, x0, 0
            sh x4, 0(x2)
            lw x3, 0(x2)
            """);
        assertEquals(0xFFFF0000, regs[3]);
    }

    @Test
    void lb_signExtendsNegative() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, -1
            addi x2, x0, 100
            sw x1, 0(x2)
            lb x3, 0(x2)
            """);
        assertEquals(-1, regs[3]);
    }

    @Test
    void lb_positive() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, 127
            addi x2, x0, 100
            sw x1, 0(x2)
            lb x3, 0(x2)
            """);
        assertEquals(127, regs[3]);
    }

    @Test
    void lbu_zeroExtends() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, -1
            addi x2, x0, 100
            sw x1, 0(x2)
            lbu x3, 0(x2)
            """);
        assertEquals(255, regs[3]);
    }

    @Test
    void lh_signExtendsNegative() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, -1
            addi x2, x0, 100
            sw x1, 0(x2)
            lh x3, 0(x2)
            """);
        assertEquals(-1, regs[3]);
    }

    @Test
    void lh_positive() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, 1000
            addi x2, x0, 100
            sw x1, 0(x2)
            lh x3, 0(x2)
            """);
        assertEquals(1000, regs[3]);
    }

    @Test
    void lhu_zeroExtends() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, -1
            addi x2, x0, 100
            sw x1, 0(x2)
            lhu x3, 0(x2)
            """);
        assertEquals(65535, regs[3]);
    }

    @Test
    void lw_fromUntouchedMemoryIsZero() {
        int[] regs = registersAtLastStep("""
            addi x2, x0, 100
            lw x3, 0(x2)
            """);
        assertEquals(0, regs[3]);
    }

    @Test
    void beq_taken() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, 5
            addi x2, x0, 5
            beq x1, x2, 8
            addi x3, x0, 999
            addi x4, x0, 1
            """);
        assertEquals(0, regs[3]);
        assertEquals(1, regs[4]);
    }

    @Test
    void beq_notTaken() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, 5
            addi x2, x0, 3
            beq x1, x2, 8
            addi x3, x0, 42
            addi x4, x0, 1
            """);
        assertEquals(42, regs[3]);
        assertEquals(1, regs[4]);
    }

    @Test
    void bne_taken() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, 5
            addi x2, x0, 3
            bne x1, x2, 8
            addi x3, x0, 999
            addi x4, x0, 1
            """);
        assertEquals(0, regs[3]);
        assertEquals(1, regs[4]);
    }

    @Test
    void bne_notTaken() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, 5
            addi x2, x0, 5
            bne x1, x2, 8
            addi x3, x0, 42
            addi x4, x0, 1
            """);
        assertEquals(42, regs[3]);
        assertEquals(1, regs[4]);
    }

    @Test
    void blt_taken() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, 3
            addi x2, x0, 5
            blt x1, x2, 8
            addi x3, x0, 999
            addi x4, x0, 1
            """);
        assertEquals(0, regs[3]);
        assertEquals(1, regs[4]);
    }

    @Test
    void blt_notTaken() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, 5
            addi x2, x0, 3
            blt x1, x2, 8
            addi x3, x0, 42
            addi x4, x0, 1
            """);
        assertEquals(42, regs[3]);
        assertEquals(1, regs[4]);
    }

    @Test
    void blt_negativeIsLess() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, -1
            addi x2, x0, 1
            blt x1, x2, 8
            addi x3, x0, 999
            addi x4, x0, 1
            """);
        assertEquals(0, regs[3]);
        assertEquals(1, regs[4]);
    }

    @Test
    void bge_taken_greater() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, 5
            addi x2, x0, 3
            bge x1, x2, 8
            addi x3, x0, 999
            addi x4, x0, 1
            """);
        assertEquals(0, regs[3]);
        assertEquals(1, regs[4]);
    }

    @Test
    void bge_taken_equal() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, 5
            addi x2, x0, 5
            bge x1, x2, 8
            addi x3, x0, 999
            addi x4, x0, 1
            """);
        assertEquals(0, regs[3]);
        assertEquals(1, regs[4]);
    }

    @Test
    void bge_notTaken() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, 3
            addi x2, x0, 5
            bge x1, x2, 8
            addi x3, x0, 42
            addi x4, x0, 1
            """);
        assertEquals(42, regs[3]);
        assertEquals(1, regs[4]);
    }

    @Test
    void bge_negativeIsNotGreater() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, -1
            addi x2, x0, 1
            bge x1, x2, 8
            addi x3, x0, 42
            addi x4, x0, 1
            """);
        assertEquals(42, regs[3]);
        assertEquals(1, regs[4]);
    }

    @Test
    void bltu_taken() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, 1
            addi x2, x0, -1
            bltu x1, x2, 8
            addi x3, x0, 999
            addi x4, x0, 1
            """);
        assertEquals(0, regs[3]);
        assertEquals(1, regs[4]);
    }

    @Test
    void bltu_notTaken() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, -1
            addi x2, x0, 1
            bltu x1, x2, 8
            addi x3, x0, 42
            addi x4, x0, 1
            """);
        assertEquals(42, regs[3]);
        assertEquals(1, regs[4]);
    }

    @Test
    void bgeu_taken() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, -1
            addi x2, x0, 1
            bgeu x1, x2, 8
            addi x3, x0, 999
            addi x4, x0, 1
            """);
        assertEquals(0, regs[3]);
        assertEquals(1, regs[4]);
    }

    @Test
    void bgeu_taken_equal() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, 5
            addi x2, x0, 5
            bgeu x1, x2, 8
            addi x3, x0, 999
            addi x4, x0, 1
            """);
        assertEquals(0, regs[3]);
        assertEquals(1, regs[4]);
    }

    @Test
    void bgeu_notTaken() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, 1
            addi x2, x0, -1
            bgeu x1, x2, 8
            addi x3, x0, 42
            addi x4, x0, 1
            """);
        assertEquals(42, regs[3]);
        assertEquals(1, regs[4]);
    }

    @Test
    void branch_backwardLoop() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, 3
            addi x2, x0, 0
            addi x2, x2, 1
            bne x2, x1, -4
            addi x3, x0, 1
            """);
        assertEquals(3, regs[2]);
        assertEquals(1, regs[3]);
    }

    @Test
    void jal_skipsAndLinks() {
        int[] regs = registersAtLastStep("""
            jal x1, 8
            addi x3, x0, 999
            addi x4, x0, 1
            """);
        assertEquals(4, regs[1]);
        assertEquals(0, regs[3]);
        assertEquals(1, regs[4]);
    }

    @Test
    void jal_linkIsNextInstruction() {
        int[] regs = registersAtLastStep("""
            addi x5, x0, 0
            addi x5, x0, 0
            jal x1, 8
            addi x3, x0, 999
            addi x4, x0, 1
            """);
        assertEquals(12, regs[1]);
        assertEquals(0, regs[3]);
        assertEquals(1, regs[4]);
    }

    @Test
    void jal_toX0DoesNotLink() {
        int[] regs = registersAtLastStep("""
            jal x0, 8
            addi x3, x0, 999
            addi x4, x0, 1
            """);
        assertEquals(0, regs[0]);
        assertEquals(0, regs[3]);
        assertEquals(1, regs[4]);
    }

    @Test
    void jalr_jumpsAndLinks() {
        int[] regs = registersAtLastStep("""
            addi x5, x0, 12
            jalr x1, 0(x5)
            addi x3, x0, 999
            addi x4, x0, 1
            """);
        assertEquals(8, regs[1]);
        assertEquals(0, regs[3]);
        assertEquals(1, regs[4]);
    }

    @Test
    void jalr_withOffset() {
        int[] regs = registersAtLastStep("""
            addi x5, x0, 4
            jalr x1, 8(x5)
            addi x3, x0, 999
            addi x4, x0, 1
            """);
        assertEquals(8, regs[1]);
        assertEquals(0, regs[3]);
        assertEquals(1, regs[4]);
    }

    @Test
    void jalr_clearsLowestBit() {
        int[] regs = registersAtLastStep("""
            addi x5, x0, 13
            jalr x1, 0(x5)
            addi x3, x0, 999
            addi x4, x0, 1
            """);
        assertEquals(8, regs[1]);
        assertEquals(0, regs[3]);
        assertEquals(1, regs[4]);
    }

    @Test
    void jalr_toX0DoesNotLink() {
        int[] regs = registersAtLastStep("""
            addi x5, x0, 12
            jalr x0, 0(x5)
            addi x3, x0, 999
            addi x4, x0, 1
            """);
        assertEquals(0, regs[0]);
        assertEquals(0, regs[3]);
        assertEquals(1, regs[4]);
    }

    @Test
    void jalr_rdEqualsRs1UsesOldValueAsTarget() {
        int[] regs = registersAtLastStep("""
            addi x5, x0, 12
            jalr x5, 0(x5)
            addi x3, x0, 999
            addi x4, x0, 1
            """);
        assertEquals(8, regs[5]);
        assertEquals(0, regs[3]);
        assertEquals(1, regs[4]);
    }

    @Test
    void x0_ignoresWrites() {
        int[] regs = registersAtLastStep("addi x0, x0, 5");
        assertEquals(0, regs[0]);
    }

    @Test
    void otherRegistersStayUntouched() {
        int[] regs = registersAtLastStep("addi x4, x0, 9");
        assertEquals(9, regs[4]);
        for (int i = 0; i < regs.length; i++) {
            if (i == 4 || i == 2) continue;
            assertEquals(0, regs[i], "x" + i + " should stay 0");
        }
    }

    @Test
    void instructionTypes_everyFormat() {
        List<String> types = instructionTypes("""
            addi x5, x0, 10
            add x6, x5, x5
            sw x6, 0(x2)
            lw x7, 0(x2)
            beq x6, x7, 8
            addi x8, x0, 99
            jal x1, 8
            addi x9, x0, 99
            auipc x10, 1
            lui x11, 1
            auipc x13, 0
            jalr x14, 12(x13)
            addi x15, x0, 99
            addi x16, x0, 1
            """);
        assertEquals(List.of(
                "I_TYPE_ALU",
                "R_TYPE",
                "S_TYPE",
                "I_TYPE_LOAD",
                "B_TYPE",
                "J_TYPE",
                "U_TYPE_AUIPC",
                "U_TYPE_LUI",
                "U_TYPE_AUIPC",
                "I_TYPE_JALR",
                "I_TYPE_ALU"
        ), types);
    }

    @Test
    void everyFormatProgram_finalRegisters() {
        int[] regs = registersAtLastStep("""
            addi x5, x0, 10
            add x6, x5, x5
            sw x6, 0(x2)
            lw x7, 0(x2)
            beq x6, x7, 8
            addi x8, x0, 99
            jal x1, 8
            addi x9, x0, 99
            auipc x10, 1
            lui x11, 1
            auipc x13, 0
            jalr x14, 12(x13)
            addi x15, x0, 99
            addi x16, x0, 1
            """);
        assertEquals(28, regs[1]);
        assertEquals(10, regs[5]);
        assertEquals(20, regs[6]);
        assertEquals(20, regs[7]);
        assertEquals(0, regs[8]);
        assertEquals(0, regs[9]);
        assertEquals(4128, regs[10]);
        assertEquals(4096, regs[11]);
        assertEquals(40, regs[13]);
        assertEquals(48, regs[14]);
        assertEquals(0, regs[15]);
        assertEquals(1, regs[16]);
    }
}