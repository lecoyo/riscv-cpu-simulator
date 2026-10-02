package com.lecoyo.riscvbackend.api;

import com.lecoyo.riscvbackend.assembly.load.InstructionLoader;
import com.lecoyo.riscvbackend.cpu.SingleCycleProcessor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SimulationController {
    @PostMapping("/simulate")
    public List<SingleCycleProcessor.CpuSnapshot> simulate(@RequestBody CodeRequest request) {
        InstructionLoader loader = new InstructionLoader();
        byte[] instructions = loader.getBinary(request.code());

        SingleCycleProcessor processor = new SingleCycleProcessor(instructions, 2056);
        return processor.simulate();
    }
}
