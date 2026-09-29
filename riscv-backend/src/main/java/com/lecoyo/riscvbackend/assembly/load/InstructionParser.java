package com.lecoyo.riscvbackend.assembly.load;

import com.lecoyo.riscvbackend.assembly.Instruction;
import com.lecoyo.riscvbackend.assembly.types.IType;
import com.lecoyo.riscvbackend.assembly.types.InstType;

import java.util.HashMap;

public class InstructionParser {
    private static final HashMap<String, InstInfo> INSTMAP = new HashMap<>();

    record InstInfo(InstType type, String opcode, String funct3, String funct7) {}

    private static void put(String name, InstType t, String opcode, String funct3, String funct7) {
        INSTMAP.put(name, new InstInfo(t, opcode, funct3, funct7));
    }

    static {
        // I Type
        put("lb",    InstType.I, "0000011", "000", null);
        put("lh",    InstType.I, "0000011", "001", null);
        put("lw",    InstType.I, "0000011", "010", null);
        put("lbu",   InstType.I, "0000011", "100", null);
        put("lhu",   InstType.I, "0000011", "101", null);
        put("addi",  InstType.I, "0010011", "000", null);
        put("slli",  InstType.I, "0010011", "001", "0000000");
        put("slti",  InstType.I, "0010011", "010", null);
        put("sltiu", InstType.I, "0010011", "011", null);
        put("xori",  InstType.I, "0010011", "100", null);
        put("srli",  InstType.I, "0010011", "101", "0000000");
        put("srai",  InstType.I, "0010011", "101", "0100000");
        put("ori",   InstType.I, "0010011", "110", null);
        put("andi",  InstType.I, "0010011", "111", null);
        put("jalr",  InstType.I, "1100111", "000", null);

        // U Type
        put("auipc", InstType.U, "0010111", null, null);
        put("lui",   InstType.U, "0110111", null, null);

        // S Type
        put("sb",    InstType.S, "0100011", "000", null);
        put("sh",    InstType.S, "0100011", "001", null);
        put("sw",    InstType.S, "0100011", "010", null);

        // R Type
        put("add",   InstType.R, "0110011", "000", "0000000");
        put("sub",   InstType.R, "0110011", "000", "0100000");
        put("sll",   InstType.R, "0110011", "001", "0000000");
        put("slt",   InstType.R, "0110011", "010", "0000000");
        put("sltu",  InstType.R, "0110011", "011", "0000000");
        put("xor",   InstType.R, "0110011", "100", "0000000");
        put("srl",   InstType.R, "0110011", "101", "0000000");
        put("sra",   InstType.R, "0110011", "101", "0100000");
        put("or",    InstType.R, "0110011", "110", "0000000");
        put("and",   InstType.R, "0110011", "111", "0000000");

        // R Type (M extension)
        put("mul",    InstType.R, "0110011", "000", "0000001");
        put("mulh",   InstType.R, "0110011", "001", "0000001");
        put("mulhsu", InstType.R, "0110011", "010", "0000001");
        put("mulhu",  InstType.R, "0110011", "011", "0000001");
        put("div",    InstType.R, "0110011", "100", "0000001");
        put("divu",   InstType.R, "0110011", "101", "0000001");
        put("rem",    InstType.R, "0110011", "110", "0000001");
        put("remu",   InstType.R, "0110011", "111", "0000001");

        // B-Type
        put("beq",   InstType.B, "1100011", "000", null);
        put("bne",   InstType.B, "1100011", "001", null);
        put("blt",   InstType.B, "1100011", "100", null);
        put("bge",   InstType.B, "1100011", "101", null);
        put("bltu",  InstType.B, "1100011", "110", null);
        put("bgeu",  InstType.B, "1100011", "111", null);

        // J-Type
        put("jal",   InstType.J, "1101111", null, null);
    }


    public Instruction parse(int pc, String instruction) {
        String[] arguments = clean(instruction);
        switch(INSTMAP.get(arguments[0])) {
            case : {
                return new IType();
            }
        }
    }

    /**
     * Splits it's
     * @param instruction
     * @return
     */
    public String[] clean(String instruction) {
        instruction = instruction.replaceAll("[-+.^:,]", "");
        return instruction.split(" ");
    }

    public boolean isLabel(String line) {
        return line.endsWith(":");
    }
}
