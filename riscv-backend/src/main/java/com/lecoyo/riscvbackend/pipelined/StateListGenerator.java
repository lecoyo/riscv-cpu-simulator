package com.lecoyo.riscvbackend.pipelined;

import com.lecoyo.riscvbackend.assembly.Instruction;
import com.lecoyo.riscvbackend.assembly.ProgramData;
import com.lecoyo.riscvbackend.cpu.SingleCycleProcessor;
import com.lecoyo.riscvbackend.cpu.SingleCycleProcessor.CpuSnapshot;

import java.util.List;

public class StateListGenerator {
    private List<InstructionStates> stateList;
    private ProgramData programData;

    private List<Instruction> getExecutedInstructions(byte[] instructions, int memorySize) {
        SingleCycleProcessor temp_cpu = new SingleCycleProcessor(instructions, memorySize);
        List<Integer> all_executed = temp_cpu.simulate().stream().map(CpuSnapshot::instruction).toList();

        all_executed.
    }
}
