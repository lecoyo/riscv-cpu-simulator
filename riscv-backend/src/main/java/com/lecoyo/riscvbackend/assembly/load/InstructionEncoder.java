package com.lecoyo.riscvbackend.assembly.load;

import com.lecoyo.riscvbackend.assembly.Instruction;
import com.lecoyo.riscvbackend.assembly.types.InstType;
import com.lecoyo.riscvbackend.assembly.types.RType;

import java.util.ArrayList;
import java.util.List;

public class InstructionEncoder {
    // TODO
    public List<Integer> encode(List<Instruction> instructions) {
        List<Integer> result = new ArrayList<>();
        for(Instruction inst : instructions) {
            int word = 0;
            switch(InstructionParser.getType(inst)) {
                case InstType.R: {
                    RType r_inst = (RType) inst;
                    result.add((inst.opcode & 0x3F)
                            | ((r_inst.getRd() & 0x1F) << 7)
                            | ((r_inst.getFunct3() & 0x7) << 12)
                            | ((r_inst.getRs1() & 0x1F) << 15)
                            | ((r_inst.getRs2() & 0x1F) << 20)
                            | ((r_inst.getFunct7() & 0x7F) << 25));
                }
            }
        }
        return result;
    }
}
