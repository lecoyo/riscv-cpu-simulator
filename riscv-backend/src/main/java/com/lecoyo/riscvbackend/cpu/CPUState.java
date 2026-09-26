package com.lecoyo.riscvbackend.cpu;

import lombok.Getter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

@Getter
public class CPUState {
    public int pc = 0;
    public int[] memory;
    public int[] registers = new int[32];
    public HashMap<String, Integer> labels = new HashMap<>();

    void init(int[] data) {
        System.arraycopy(memory, 0, data, 0, data.length);
    }

    public void setRegister(byte regNumber, int value) {
        registers[regNumber] = value;
    }

    public int getLabelValue(String label) {
        return labels.get(label) - pc;
    }
}
