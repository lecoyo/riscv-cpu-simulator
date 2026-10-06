package com.lecoyo.riscvbackend.api;

import com.lecoyo.riscvbackend.cpu.SingleCycleProcessor;

public record Package(SingleCycleProcessor.CpuSnapshot cpuSnapshot, String instructionString) {}
