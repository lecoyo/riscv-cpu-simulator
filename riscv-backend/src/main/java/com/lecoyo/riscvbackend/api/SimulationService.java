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

        for (SingleCycleProcessor.CpuSnapshot snapshot : snapshotList) {
            packageList.add(new Package(snapshot, instructionStrings[snapshot.pc() / 4]));
        }

        return packageList;
    }
}
