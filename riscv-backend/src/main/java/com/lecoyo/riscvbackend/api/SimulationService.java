package com.lecoyo.riscvbackend.api;

import com.lecoyo.riscvbackend.assembly.load.InstructionLoader;
import com.lecoyo.riscvbackend.cpu.SingleCycleProcessor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Service
public class SimulationService {
    public List<Package> simulate(String code) {
        InstructionLoader loader = new InstructionLoader();
        byte[] instructions = loader.getBinary(code);

        SingleCycleProcessor processor = new SingleCycleProcessor(instructions, 2048);

        List<Package> packageList = new ArrayList<>();
        List<SingleCycleProcessor.CpuSnapshot> snapshotList = processor.simulate();
        String[] instructionStrings = loader.getInstructionString(code);

        for (int i = 0; i < snapshotList.size(); i++) {
            packageList.add(new Package(snapshotList.get(i), instructionStrings[i]));
        }

        return packageList;
    }
}
