package com.lecoyo.riscvbackend.api;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class SimulationController {

    @PostMapping("/simulate")
    public String simulate(@RequestBody CodeRequest request) {
        return request.code();
    }
}
