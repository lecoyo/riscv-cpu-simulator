package com.lecoyo.riscvbackend.cpu.controlunit;

import lombok.NoArgsConstructor;

/**
 * Main decoder of the control unit: maps an opcode to the control signals.
 */
@NoArgsConstructor
public class MainControlUnit {
    /**
     * Decodes the opcode into its control signals.
     * Signal order: pcSrc, resultSrc, memWrite, aluSrc, regWrite, ALUOp, immSrc, instruction type.
     * {@code pcSrc} is {@code branchTaken} for branches and always set for {@code jal}.
     * ({@code jalr} is handled separately in the processor.)
     *
     * @param op the 7-bit opcode of the instruction
     * @param branchTaken {@code true} if the branch condition of a B-type instruction is met
     * @return the control signals, where {@code aluControl} holds the intermediate
     *         {@code ALUOp} and not yet the final ALU code
     * @throws IllegalArgumentException if {@code op} is not a known opcode
     */
    public ControlSignals decode(byte op, boolean branchTaken) {
        InstructionFormat instructionFormat = decodeTypes(op);

        return switch (instructionFormat) {
            case I_TYPE_LOAD -> new ControlSignals(
                    false,
                    true,
                    false,
                    true,
                    true,
                    (byte) 0b00,
                    (byte) 0b000,
                    InstructionFormat.I_TYPE_LOAD
            );
            case I_TYPE_ALU -> new ControlSignals(
                    false,
                    false,
                    false,
                    true,
                    true,
                    (byte) 0b11,
                    (byte) 0b000,
                    InstructionFormat.I_TYPE_ALU
            );
            case I_TYPE_JALR -> new ControlSignals(
                    true,
                    false,
                    false,
                    true,
                    true,
                    (byte) 0b11,
                    (byte) 0b000,
                    InstructionFormat.I_TYPE_JALR
            );
            // lui/auipc: ALU adds; the processor sets the first operand to 0 (lui) or pc (auipc)
            case U_TYPE_AUIPC-> new ControlSignals(
                    false,
                    false,
                    false,
                    true,
                    true,
                    (byte) 0b00,
                    (byte) 0b100,
                    InstructionFormat.U_TYPE_AUIPC
            );
            case U_TYPE_LUI -> new ControlSignals(
                    false,
                    false,
                    false,
                    true,
                    true,
                    (byte) 0b00,
                    (byte) 0b100,
                    InstructionFormat.U_TYPE_LUI
            );
            case S_TYPE -> new ControlSignals(
                    false,
                    false,
                    true,
                    true,
                    false,
                    (byte) 0b00,
                    (byte) 0b001,
                    InstructionFormat.S_TYPE
            );
            case R_TYPE -> new ControlSignals(
                    false,
                    false,
                    false,
                    false,
                    true,
                    (byte) 0b10,
                    (byte) 0b000,
                    InstructionFormat.R_TYPE
            );
            // branches: ALU subtracts, pcSrc depends on the branch condition
            case B_TYPE -> new ControlSignals(
                    branchTaken,
                    false,
                    false,
                    false,
                    false,
                    (byte) 0b01,
                    (byte) 0b010,
                    InstructionFormat.B_TYPE
            );
            case J_TYPE -> new ControlSignals(
                    true,
                    false,
                    false,
                    false,
                    true,
                    (byte) 0b00,
                    (byte) 0b011,
                    InstructionFormat.J_TYPE);
        };
    }

    /**
     * Maps the opcode to its instruction format.
     *
     * @param op the 7-bit opcode
     * @return the matching instruction format
     * @throws IllegalArgumentException if {@code op} is not a known opcode
     */
    private InstructionFormat decodeTypes(byte op) {
        return switch (op) {
            case 0b0000011 -> InstructionFormat.I_TYPE_LOAD;
            case 0b0010011 -> InstructionFormat.I_TYPE_ALU;
            case 0b1100111 -> InstructionFormat.I_TYPE_JALR;
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