package com.lecoyo.riscvbackend.cpu.controlunit;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ControlUnit {
    MainControlUnit mainControlUnit;
    ALUControl aluControl;

    public ControlSignals operate(byte op, byte funct3, byte funct7, boolean zero) {
        // get controlSignals with aluOp stored in aluControl
        ControlSignals controlSignals = mainControlUnit.operate(op);

        // replace aluOp with aluControl
        controlSignals.aluControl = aluControl.decode(controlSignals.getAluControl(), funct3, funct7);

        // TODO J-Type instructions may not function correctly because of wb of pc + 4
        // TODO B-Type instructions may not function correctly because of missing zero flag implementation

        // TODO LUI, AUIPC, JALR instructions need more signals to work properly
        return controlSignals;
    }
}
