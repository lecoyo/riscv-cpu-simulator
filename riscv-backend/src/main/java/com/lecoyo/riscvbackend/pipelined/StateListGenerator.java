package com.lecoyo.riscvbackend.pipelined;

import com.lecoyo.riscvbackend.api.Package;
import com.lecoyo.riscvbackend.assembly.ProgramData;
import com.lecoyo.riscvbackend.assembly.load.InstructionParser;

import java.util.List;

public class StateListGenerator {
    private List<List<InstructionStates>> stateList;
    private ProgramData programData;

    public void generateList(List<Package> pkg) {
        // TODO
        for(int i = 0; i < pkg.size(); i++) {
            String mnemonic = pkg.get(i).instructionString().split(" ")[0];
            switch(InstructionParser.getOpcode(mnemonic)) {
                case 0b0000011:
                    return;
            }
        }

        return;
    }
}
