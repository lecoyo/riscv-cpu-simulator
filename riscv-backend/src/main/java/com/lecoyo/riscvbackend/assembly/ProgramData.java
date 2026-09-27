package com.lecoyo.riscvbackend.assembly;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;

@Getter
@AllArgsConstructor
public class ProgramData {
    private List<Instruction> instructions;
    private HashMap<String, Integer> labels;
}
