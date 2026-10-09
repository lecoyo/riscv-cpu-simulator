package com.lecoyo.riscvbackend.pipelined;

public enum InstructionStates {
    FETCH,
    DECODE,
    EXECUTE,
    MEMORY,
    WRITEBACK,
    FLUSHED,
    NONE
}
