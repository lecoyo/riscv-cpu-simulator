package com.lecoyo.riscvbackend.cpu.components;

public class DataMemory {
    private byte[] memory;

    /**
     * Creates the data memory in the given size.
     *
     * @param size the size in bytes of the memory
     */
    public DataMemory(int size) {
        this.memory = new byte[size];
    }

    /**
     * Operates the data memory.
     * If {@code memWrite} is true, writes the {@code writeData} to the given address at {@code aluResult}.
     * If {@code memWrite} is false, reads the data at {@code aluResult} and returns it.
     *
     * @param a the address for the operation
     * @param wd the data to write
     * @param we switch between read and write mode
     * @return the 32-bit word starting at address {@code aluResult}, or {@code 0} if {@code memWrite} is true.
     */
    public int operate(int a, int wd, boolean we) {
        if (we) {
            write(a, wd);
            return 0;
        }
        else {
            return read(a);
        }
    }

    private int read(int address) {
        return ((memory[address]     & 0xFF) << 24) |
                ((memory[address + 1] & 0xFF) << 16) |
                ((memory[address + 2] & 0xFF) << 8)  |
                ((memory[address + 3] & 0xFF));
    }

    private void write(int address, int value) {
        memory[address] = (byte) ((value >>> 24) & 0xFF);
        memory[address + 1] = (byte) ((value >>> 16) & 0xFF);
        memory[address + 2] = (byte) ((value >>> 8) & 0xFF);
        memory[address + 3] = (byte) (value & 0xFF);
    }
}
