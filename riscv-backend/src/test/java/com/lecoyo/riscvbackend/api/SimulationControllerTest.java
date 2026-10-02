package com.lecoyo.riscvbackend.api;

import com.lecoyo.riscvbackend.cpu.SingleCycleProcessor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimulationServiceTest {
    private final SimulationService service = new SimulationService();

    private int[] run(String code) {
        List<SingleCycleProcessor.CpuSnapshot> trace = service.simulate(code);
        return trace.get(trace.size() - 1).registers();
    }

    @Test
    void addi_positive() {
        int[] regs = run("addi x1, x0, 5");
        assertEquals(5, regs[1]);
    }

    @Test
    void addi_negative() {
        int[] regs = run("addi x1, x0, -5");
        assertEquals(-5, regs[1]);
    }

    @Test
    void andi() {
        int[] regs = run("""
            addi x1, x0, 12
            andi x2, x1, 10
            """);
        assertEquals(8, regs[2]);
    }

    @Test
    void ori() {
        int[] regs = run("""
            addi x1, x0, 12
            ori x2, x1, 3
            """);
        assertEquals(15, regs[2]);
    }

    @Test
    void xori() {
        int[] regs = run("""
            addi x1, x0, 12
            xori x2, x1, 10
            """);
        assertEquals(6, regs[2]);
    }

    @Test
    void slti_true() {
        int[] regs = run("""
            addi x1, x0, 3
            slti x2, x1, 5
            """);
        assertEquals(1, regs[2]);
    }

    @Test
    void slti_false() {
        int[] regs = run("""
            addi x1, x0, 10
            slti x2, x1, 5
            """);
        assertEquals(0, regs[2]);
    }

    @Test
    void sltiu() {
        int[] regs = run("""
            addi x1, x0, -1
            sltiu x2, x1, 5
            """);
        assertEquals(0, regs[2]);
    }

    @Test
    void slli() {
        int[] regs = run("""
            addi x1, x0, 1
            slli x2, x1, 4
            """);
        assertEquals(16, regs[2]);
    }

    @Test
    void srli() {
        int[] regs = run("""
            addi x1, x0, 16
            srli x2, x1, 4
            """);
        assertEquals(1, regs[2]);
    }

    @Test
    void srai() {
        int[] regs = run("""
            addi x1, x0, -16
            srai x2, x1, 2
            """);
        assertEquals(-4, regs[2]);
    }

    @Test
    void add() {
        int[] regs = run("""
            addi x1, x0, 7
            addi x2, x0, 8
            add x3, x1, x2
            """);
        assertEquals(15, regs[3]);
    }

    @Test
    void sub() {
        int[] regs = run("""
            addi x1, x0, 10
            addi x2, x0, 3
            sub x3, x1, x2
            """);
        assertEquals(7, regs[3]);
    }

    @Test
    void and() {
        int[] regs = run("""
            addi x1, x0, 12
            addi x2, x0, 10
            and x3, x1, x2
            """);
        assertEquals(8, regs[3]);
    }

    @Test
    void or() {
        int[] regs = run("""
            addi x1, x0, 12
            addi x2, x0, 3
            or x3, x1, x2
            """);
        assertEquals(15, regs[3]);
    }

    @Test
    void xor() {
        int[] regs = run("""
            addi x1, x0, 12
            addi x2, x0, 10
            xor x3, x1, x2
            """);
        assertEquals(6, regs[3]);
    }

    @Test
    void sll() {
        int[] regs = run("""
            addi x1, x0, 1
            addi x2, x0, 4
            sll x3, x1, x2
            """);
        assertEquals(16, regs[3]);
    }

    @Test
    void srl() {
        int[] regs = run("""
            addi x1, x0, -1
            addi x2, x0, 28
            srl x3, x1, x2
            """);
        assertEquals(15, regs[3]);
    }

    @Test
    void sra() {
        int[] regs = run("""
            addi x1, x0, -16
            addi x2, x0, 2
            sra x3, x1, x2
            """);
        assertEquals(-4, regs[3]);
    }

    @Test
    void slt_true() {
        int[] regs = run("""
            addi x1, x0, -1
            addi x2, x0, 1
            slt x3, x1, x2
            """);
        assertEquals(1, regs[3]);
    }

    @Test
    void slt_false() {
        int[] regs = run("""
            addi x1, x0, 5
            addi x2, x0, 1
            slt x3, x1, x2
            """);
        assertEquals(0, regs[3]);
    }

    @Test
    void sltu() {
        int[] regs = run("""
            addi x1, x0, -1
            addi x2, x0, 1
            sltu x3, x1, x2
            """);
        assertEquals(0, regs[3]);
    }

    @Test
    void lui() {
        int[] regs = run("lui x1, 74565");
        assertEquals(0x12345000, regs[1]);
    }

    @Test
    void auipc() {
        int[] regs = run("auipc x1, 1");
        assertEquals(0x1000, regs[1]);
    }

    @Test
    void sw_then_lw() {
        int[] regs = run("""
            addi x1, x0, 42
            addi x2, x0, 100
            sw x1, 0(x2)
            lw x3, 0(x2)
            """);
        assertEquals(42, regs[3]);
    }

    @Test
    void sw_withOffset() {
        int[] regs = run("""
            addi x1, x0, 7
            addi x2, x0, 100
            sw x1, 4(x2)
            lw x3, 4(x2)
            """);
        assertEquals(7, regs[3]);
    }

    @Test
    void sw_negativeValue() {
        int[] regs = run("""
            addi x1, x0, -1
            addi x2, x0, 100
            sw x1, 0(x2)
            lw x3, 0(x2)
            """);
        assertEquals(-1, regs[3]);
    }

    @Test
    void lb_signExtends() {
        int[] regs = run("""
            addi x1, x0, -1
            addi x2, x0, 100
            sw x1, 0(x2)
            lb x3, 0(x2)
            """);
        assertEquals(-1, regs[3]);
    }

    @Test
    void lbu_zeroExtends() {
        int[] regs = run("""
            addi x1, x0, -1
            addi x2, x0, 100
            sw x1, 0(x2)
            lbu x3, 0(x2)
            """);
        assertEquals(255, regs[3]);
    }

    @Test
    void lh_signExtends() {
        int[] regs = run("""
            addi x1, x0, -1
            addi x2, x0, 100
            sw x1, 0(x2)
            lh x3, 0(x2)
            """);
        assertEquals(-1, regs[3]);
    }

    @Test
    void lhu_zeroExtends() {
        int[] regs = run("""
            addi x1, x0, -1
            addi x2, x0, 100
            sw x1, 0(x2)
            lhu x3, 0(x2)
            """);
        assertEquals(65535, regs[3]);
    }

    @Test
    void sb_storesOnlyLowestByte() {
        int[] regs = run("""
            addi x1, x0, 258
            addi x2, x0, 100
            sb x1, 0(x2)
            lw x3, 0(x2)
            """);
        assertEquals(2, regs[3]);
    }

    @Test
    void beq_taken() {
        int[] regs = run("""
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
        int[] regs = run("""
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
        int[] regs = run("""
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
    void blt_taken() {
        int[] regs = run("""
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
    void bge_taken() {
        int[] regs = run("""
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
    void bltu_taken() {
        int[] regs = run("""
            addi x1, x0, 3
            addi x2, x0, 5
            bltu x1, x2, 8
            addi x3, x0, 999
            addi x4, x0, 1
            """);
        assertEquals(0, regs[3]);
        assertEquals(1, regs[4]);
    }

    @Test
    void bgeu_taken() {
        int[] regs = run("""
            addi x1, x0, -1
            addi x2, x0, 5
            bgeu x1, x2, 8
            addi x3, x0, 999
            addi x4, x0, 1
            """);
        assertEquals(0, regs[3]);
        assertEquals(1, regs[4]);
    }

    @Test
    void jal() {
        int[] regs = run("""
        jal x1, 8
        addi x5, x0, 999
        addi x3, x0, 1
        """);
        assertEquals(0, regs[5]);
        assertEquals(1, regs[3]);
        assertEquals(4, regs[1]);
    }

    @Test
    void jalr() {
        int[] regs = run("""
        addi x5, x0, 12
        jalr x1, x5, 0
        addi x6, x0, 999
        addi x3, x0, 1
        """);
        assertEquals(0, regs[6]);
        assertEquals(1, regs[3]);
        assertEquals(8, regs[1]);
    }
}