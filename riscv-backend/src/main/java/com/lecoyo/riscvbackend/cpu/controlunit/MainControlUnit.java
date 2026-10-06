package com.lecoyo.riscvbackend.cpu.controlunit;

import com.lecoyo.riscvbackend.assembly.Instruction;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MainControlUnit {
    /**
     * Decodes the instruction opcode into its control signals.
     * For branch instructions, {@code pcSrc} is set to {@code branchTaken}. For jumps, it is always set.
     *
     * @param op the 7-bit opcode of the instruction
     * @param branchTaken {@code true} if the branch condition of a B-type instruction is met
     * @return the control signals for this instruction, with {@code aluControl}
     *         holding the intermediate {@code ALUOp} rather than the final ALU code
     * @throws IllegalArgumentException if {@code op} does not match a known instruction format
     */
    public ControlSignals decode(byte op, boolean branchTaken) {
        InstructionFormat instructionFormat = decodeTypes(op);

        return switch (instructionFormat) {
            case I_TYPE_LOAD -> new ControlSignals(false, true, false, true, true, (byte) 0b00, (byte) 0b000, InstructionFormat.I_TYPE_LOAD);
            case I_TYPE_ALU -> new ControlSignals(false, false, false, true, true, (byte) 0b11, (byte) 0b000, InstructionFormat.I_TYPE_ALU);
            case U_TYPE -> new ControlSignals(false, false, false, true, true, (byte) 0b00, (byte) 0b100, InstructionFormat.U_TYPE);
            case S_TYPE -> new ControlSignals(false, false, true, true, false, (byte) 0b00, (byte) 0b001, InstructionFormat.S_TYPE);
            case R_TYPE -> new ControlSignals(false, false, false, false, true, (byte) 0b10, (byte) 0b000, InstructionFormat.R_TYPE);
            case B_TYPE -> new ControlSignals(branchTaken, false, false, false, false, (byte) 0b01, (byte) 0b010, InstructionFormat.B_TYPE);
            case J_TYPE -> new ControlSignals(true, false, false, false, true, (byte) 0b00, (byte) 0b011, InstructionFormat.J_TYPE);
        };
    }

    private InstructionFormat decodeTypes(byte op) {
        return switch (op) {
            case 0b0000011 -> InstructionFormat.I_TYPE_LOAD;
            case 0b0010011, 0b1100111 -> InstructionFormat.I_TYPE_ALU;
            case 0b0010111, 0b0110111 -> InstructionFormat.U_TYPE;
            case 0b0100011 -> InstructionFormat.S_TYPE;
            case 0b0110011 -> InstructionFormat.R_TYPE;
            case 0b1100011 -> InstructionFormat.B_TYPE;
            case 0b1101111 -> InstructionFormat.J_TYPE;
            default -> throw new IllegalArgumentException("Unknown Opcode: " + op);
        };
    }
}
