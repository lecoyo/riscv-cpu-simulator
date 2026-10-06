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
    void sltiu() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, -1
            sltiu x2, x1, 5
            """);
        assertEquals(0, regs[2]);
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
    void srli() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, 16
            srli x2, x1, 4
            """);
        assertEquals(1, regs[2]);
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
    void add() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, 7
            addi x2, x0, 8
            add x3, x1, x2
            """);
        assertEquals(15, regs[3]);
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
    void sltu() {
        int[] regs = registersAtLastStep("""
            addi x1, x0, -1
            addi x2, x0, 1
            sltu x3, x1, x2
            """);
        assertEquals(0, regs[3]);
    }

    @Test
    void lui() {
        int[] regs = registersAtLastStep("lui x1, 74565");
        assertEquals(0x12345000, regs[1]);
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
}