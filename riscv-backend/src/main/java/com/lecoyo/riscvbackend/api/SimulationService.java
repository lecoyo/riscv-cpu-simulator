package com.lecoyo.riscvbackend.api;

import com.lecoyo.riscvbackend.assembly.load.InstructionLoader;
import com.lecoyo.riscvbackend.cpu.SingleCycleProcessor;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class SimulationService {
    public List<SingleCycleProcessor.CpuSnapshot> simulate(String code) {
        InstructionLoader loader = new InstructionLoader();
        byte[] instructions = loader.getBinary(code);

        SingleCycleProcessor processor = new SingleCycleProcessor(instructions, 2048);
        return processor.simulate();
    }
}
