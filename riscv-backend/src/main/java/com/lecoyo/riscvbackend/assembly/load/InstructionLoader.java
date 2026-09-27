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
        InstructionParser parser = new InstructionParser();
        List<Instruction> instructions = new ArrayList<>();
        HashMap<String, Integer> labels = new HashMap<>();

        int pc = 0;

        for (String mnemonic : mnemonics) {
            String s = mnemonic;
            s = clean(s);

            if (parser.isLabel(s)) {
                String labelName = s.substring(0, s.indexOf(":"));
                labels.put(labelName, pc);
            } else {
                Instruction instruction = parser.parse(s);
                instructions.add(instruction);
                pc += 4;
            }
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
