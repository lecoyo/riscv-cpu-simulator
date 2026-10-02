package com.lecoyo.riscvbackend.assembly.load;

import com.lecoyo.riscvbackend.assembly.Instruction;

import java.util.List;

public class InstructionEncoder {
    public static List<Integer> encode(List<Instruction> instructions) {
        return instructions.stream()
                .map(Instruction::encode)
                .toList();
    }
}
