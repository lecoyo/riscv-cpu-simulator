package com.lecoyo.riscvbackend.cpu.components;

public class Mux2 {
    /**
     * Selects {@code in0} if {@code sel} is false and {@code in1} if {@code sel} is true.
     *
     * @param in0 first input
     * @param in1 second input
     * @param sel selection input
     * @return the output of the mux
     */
    public static int select(int in0, int in1, boolean sel) {
        return sel ? in1 : in0;
    }

}
