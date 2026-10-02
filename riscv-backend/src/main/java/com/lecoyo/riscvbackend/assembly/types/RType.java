package com.lecoyo.riscvbackend.assembly.types;

import com.lecoyo.riscvbackend.assembly.Instruction;
import lombok.Getter;

@Getter
public class RType extends Instruction {
    private final byte rd;
    private final byte funct3;
    private final byte rs1;
    private final byte rs2;
    private final byte funct7;

    public RType(String mnemonic, byte opcode, byte rd, byte funct3, byte rs1, byte rs2, byte funct7) {

        this.mnemonic = mnemonic;
        this.opcode = opcode;
        this.rd = rd;
        this.funct3 = funct3;
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.funct7 = funct7;
    }

    @Override
    protected int assemble() {
        return (opcode & 0x7F)
                | ((getRd() & 0x1F) << 7)
                | ((getFunct3() & 0x7) << 12)
                | ((getRs1() & 0x1F) << 15)
                | ((getRs2() & 0x1F) << 20)
                | ((getFunct7() & 0x7F) << 25);
    }
}
