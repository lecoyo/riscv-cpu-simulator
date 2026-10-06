package com.lecoyo.riscvbackend.cpu.controlunit;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ControlSignals {
    private boolean pcSrc, resultSrc, memWrite, aluSrc, regWrite;
    public byte aluControl;
    private byte immSrc;
    private InstructionFormat instructionType;
}
