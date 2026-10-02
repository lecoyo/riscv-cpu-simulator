package com.lecoyo.riscvbackend.assembly.load;

import com.lecoyo.riscvbackend.assembly.Instruction;
import com.lecoyo.riscvbackend.assembly.ProgramData;import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@AllArgsConstructor
public class InstructionLoader {
    private String[] mnemonics;

    /**
     * Iterates through loaded string array <code>mnemonics</code> and returns a ProgramData object with all instruction
     * objects and labels.
     * @return <code>ProgramData</code> object with all instructions and labels
     */
    public ProgramData getObjects() {
        HashMap<String, Integer> labels = new HashMap<>();
        List<Instruction> instructions = new ArrayList<>();

        int pc = 0;

        // check for labels
        for (String mnemonic : mnemonics) {
            String line = mnemonic.trim();
            if (line.isEmpty()) continue;

            if (InstructionParser.isLabel(line)) {
                String labelName = line.substring(0, line.indexOf(':')).trim();
                if (labels.put(labelName, pc) != null) {
                    throw new IllegalArgumentException("Duplicate label: " + labelName);
                }
            } else {
                pc += 4;
            }
        }

        InstructionParser parser = new InstructionParser(labels);
        pc = 0;

        // map instructions
        for (String mnemonic : mnemonics) {
            String line = mnemonic.trim();
            line = clean(line);
            if (line.isEmpty() || InstructionParser.isLabel(line)) continue;

            Instruction instruction = parser.parse(pc, line);
            instructions.add(instruction);
            pc += 4;
        }

        return new ProgramData(instructions, labels);
    }

    /**
     * Removes everything after and including a <code>#</code> symbol (representing a comment)
     * that would otherwise interfere with the parsing step.
     *
     * @param line The line to clean
     * @return Line without comments
     */
    private String clean(String line) {
        int comment = line.indexOf('#');
        if(comment >= 0) {
            line = line.substring(0, comment);
        }

        return line.trim();
    }
}
