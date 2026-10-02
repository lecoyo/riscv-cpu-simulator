package com.lecoyo.riscvbackend.api;

import com.lecoyo.riscvbackend.cpu.SingleCycleProcessor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SimulationController {

    @PostMapping("/simulate")
    public List<SingleCycleProcessor.CpuSnapshot> simulate(@RequestBody CodeRequest request) {
        byte[] instructions = new byte[8];
        instructions[0] = (byte) 0b10010011;
        instructions[1] = (byte) 0b00000000;
        instructions[2] = (byte) 0b01010000;
        instructions[3] = (byte) 0b00000000;

        instructions[4] = (byte) 0b10010011;
        instructions[5] = (byte) 0b00110000;
        instructions[6] = (byte) 0b11111100;
        instructions[7] = (byte) 0b01111111;

        SingleCycleProcessor processor = new SingleCycleProcessor(instructions, 1024);
        return processor.simulate();
    }
}
