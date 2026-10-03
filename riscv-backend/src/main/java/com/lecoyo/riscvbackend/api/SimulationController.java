package com.lecoyo.riscvbackend.api;

import com.lecoyo.riscvbackend.cpu.SingleCycleProcessor;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class SimulationController {
    private final SimulationService simulationService;

    @PostMapping("/simulate")
    public List<SingleCycleProcessor.CpuSnapshot> simulate(@RequestBody CodeRequest request) {
        return simulationService.simulate(request.code());
    }
}
