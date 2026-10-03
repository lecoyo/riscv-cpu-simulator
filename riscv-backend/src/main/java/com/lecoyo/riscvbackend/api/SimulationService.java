package com.lecoyo.riscvbackend.api;

import com.lecoyo.riscvbackend.assembly.load.InstructionLoader;
import com.lecoyo.riscvbackend.cpu.SingleCycleProcessor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@NoArgsConstructor
@Service
public class SimulationService {
    public List<SingleCycleProcessor.CpuSnapshot> simulate(String code) {
        InstructionLoader loader = new InstructionLoader();
        byte[] instructions = loader.getBinary(code);

        SingleCycleProcessor processor = new SingleCycleProcessor(instructions, 2048);
        return processor.simulate();
    }
}
