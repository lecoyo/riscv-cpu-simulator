package com.lecoyo.riscvbackend.assembly.load;

import com.lecoyo.riscvbackend.assembly.Instruction;
import com.lecoyo.riscvbackend.assembly.ProgramData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InstructionLoader {
    private String[] mnemonics;

    public ProgramData getObjects() {
        InstructionParser parser = new InstructionParser();
        List<Instruction> instructions = new ArrayList<>();
        HashMap<String, Integer> labels = new HashMap<>();

        for(int i = 0; i < mnemonics.length; i++) {
            String s = mnemonics[i];
            s = clean(s);

            if(!parser.isLabel(s))
                instructions.add(parser.parse(s));

            String labelName = s.substring(0, s.indexOf(":"));
            labels.put(labelName, (i + 1) * 4);
        }

        return new ProgramData(instructions, labels);
    }

    private String clean(String line) {
        int comment = line.indexOf('#');
        if(comment >= 0) {
            line = line.substring(0, comment);
        }

        return line.trim();
    }
}
