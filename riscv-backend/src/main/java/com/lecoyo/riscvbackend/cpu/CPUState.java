package com.lecoyo.riscvbackend.cpu;

import lombok.Getter;

import java.util.HashMap;

@Getter
public class CPUState {
    public int pc = 0;
    public HashMap<String, Integer> labels = new HashMap<>();
    public int[] registers = new int[31];

    public void setRegister(byte regNumber, int value) {
        registers[regNumber] = value;
    }

    public int getLabelValue(String label) {
        return labels.get(label) - pc;
    }
}
