package com.lecoyo.riscvbackend.cpu.controlunit;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ControlUnit {
    MainControlUnit mainControlUnit;
    ALUControl aluControl;

    /**
     * Computes the final control signals for an instruction by combining
     * the coarse decoding of {@link MainControlUnit} with the ALU operation
     * decoding of {@link ALUControl}.
     *
     * @param op     the 7-bit opcode of the instruction
     * @param funct3 the 3-bit funct3 field
     * @param funct7 the 7-bit funct7 field
     * @param zero   the ALU's zero flag, used to resolve branch instructions
     * @return the resolved control signals for this instruction
     * @throws IllegalArgumentException if {@code op} or {@code funct3} does not match a known instruction format
     */
    public ControlSignals operate(byte op, byte funct3, byte funct7, boolean zero) {
        // get controlSignals with aluOp stored in aluControl
        ControlSignals controlSignals = mainControlUnit.decode(op);

        // replace aluOp with aluControl
        controlSignals.aluControl = aluControl.decode(controlSignals.getAluControl(), funct3, funct7);

        // TODO J-Type instructions may not function correctly because of wb of pc + 4
        // TODO B-Type instructions may not function correctly because of missing zero flag implementation

        // TODO LUI, AUIPC, JALR instructions need more signals to work properly
        return controlSignals;
    }
}
