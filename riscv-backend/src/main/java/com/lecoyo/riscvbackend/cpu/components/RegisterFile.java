package com.lecoyo.riscvbackend.cpu.components;

import lombok.Getter;

@Getter
public class RegisterFile {
    private int[] registers;

    /**
     * Creates the register file with 32 registers, all initialized to 0.
     * Register x0 is hardwired to 0 and can never be written.
     */
    public RegisterFile() {
        registers = new int[32];
    }

    /**
     * Reads the values of two registers at the given addresses.
     *
     * @param a1 address of the first register to read
     * @param a2 address of the second register to read
     * @return an array containing {@code RD1, RD2}, the values read from registers {@code a1} and {@code a2}
     */
    public int[] read(int a1, int a2) {
        return new int[] {registers[a1], registers[a2]};
    }

    /**
     * Writes {@code wd3} to the register at address {@code a3}, if {@code regWrite} is true.
     * Writes to register x0 are always ignored.
     *
     * @param a3 address of the register to write to
     * @param wd3 the data to write
     * @param we3 switch to enable writing
     */
    public void write(int a3, int wd3, boolean we3) {
        if (we3 && a3 != 0) {
            registers[a3] = wd3;
        }
    }
}
