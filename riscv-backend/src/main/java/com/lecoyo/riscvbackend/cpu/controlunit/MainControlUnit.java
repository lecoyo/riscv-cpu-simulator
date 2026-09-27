package com.lecoyo.riscvbackend.cpu.controlunit;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MainControlUnit {

    public ControlSignals operate(byte op) {
        InstructionFormat instructionFormat = decode(op);

        return switch (instructionFormat) {
            case I_TYPE_LOAD -> new ControlSignals(false, true, false, true, true, (byte) 0b00, (byte) 0b000);
            case I_TYPE_ALU -> new ControlSignals(false, false, false, true, true, (byte) 0b11, (byte) 0b000);
            case U_TYPE_AUIPC -> new ControlSignals(false, false, false, true, true, (byte) 0b00, (byte) 0b100);
            case U_TYPE_LUI -> new ControlSignals(false, false, false, false, true, (byte) 0b00, (byte) 0b100);
            case S_TYPE -> new ControlSignals(false, false, true, true, false, (byte) 0b00, (byte) 0b001);
            case R_TYPE -> new ControlSignals(false, false, false, false, true, (byte) 0b10, (byte) 0b000);
            case B_TYPE -> new ControlSignals(false, false, false, false, false, (byte) 0b01, (byte) 0b010);
            case J_TYPE -> new ControlSignals(true, false, false, false, true, (byte) 0b00, (byte) 0b011);
        };
    }

    private InstructionFormat decode(byte op) {
        return switch (op) {
            case 0b0000011 -> InstructionFormat.I_TYPE_LOAD;
            case 0b0010011, 0b1100111 -> InstructionFormat.I_TYPE_ALU;
            case 0b0010111 -> InstructionFormat.U_TYPE_AUIPC;
            case 0b0110111 -> InstructionFormat.U_TYPE_LUI;
            case 0b0100011 -> InstructionFormat.S_TYPE;
            case 0b0110011 -> InstructionFormat.R_TYPE;
            case 0b1100011 -> InstructionFormat.B_TYPE;
            case 0b1101111 -> InstructionFormat.J_TYPE;
            default -> throw new IllegalArgumentException("Unknown Opcode: " + op);
        };
    }
}
