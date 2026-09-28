package com.lecoyo.riscvbackend.assembly.load;

import com.lecoyo.riscvbackend.assembly.Instruction;
import com.lecoyo.riscvbackend.assembly.types.InstType;

import java.util.HashMap;

public class InstructionParser {
    private static final HashMap<String, InstType> TYPEMAP = new HashMap<>();

    static {
        // I Type
        TYPEMAP.put("lb",   InstType.I);
        TYPEMAP.put("lh",   InstType.I);
        TYPEMAP.put("lw",   InstType.I);
        TYPEMAP.put("lbu",  InstType.I);
        TYPEMAP.put("lhu",  InstType.I);
        TYPEMAP.put("addi", InstType.I);
        TYPEMAP.put("slli", InstType.I);
        TYPEMAP.put("slti", InstType.I);
        TYPEMAP.put("sltiu",InstType.I);
        TYPEMAP.put("xori", InstType.I);
        TYPEMAP.put("srli", InstType.I);
        TYPEMAP.put("srai", InstType.I);
        TYPEMAP.put("ori",  InstType.I);
        TYPEMAP.put("andi", InstType.I);
        TYPEMAP.put("jalr", InstType.I);

        // U Type
        TYPEMAP.put("auipc",InstType.U);
        TYPEMAP.put("lui",  InstType.U);

        // S Type
        TYPEMAP.put("sb",   InstType.S);
        TYPEMAP.put("sh",   InstType.S);
        TYPEMAP.put("sw",   InstType.S);

        // R Type
        TYPEMAP.put("add",  InstType.R);
        TYPEMAP.put("sub",  InstType.R);
        TYPEMAP.put("sll",  InstType.R);
        TYPEMAP.put("slt",  InstType.R);
        TYPEMAP.put("sltu", InstType.R);
        TYPEMAP.put("xor",  InstType.R);
        TYPEMAP.put("srl",  InstType.R);
        TYPEMAP.put("sra",  InstType.R);
        TYPEMAP.put("or",   InstType.R);
        TYPEMAP.put("and",  InstType.R);

        // R Type / M extension (?)
        TYPEMAP.put("mul",      InstType.R);
        TYPEMAP.put("mulh",     InstType.R);
        TYPEMAP.put("mulhsu",   InstType.R);
        TYPEMAP.put("mulhu",    InstType.R);
        TYPEMAP.put("div",      InstType.R);
        TYPEMAP.put("divu",     InstType.R);
        TYPEMAP.put("rem",      InstType.R);
        TYPEMAP.put("remu",     InstType.R);

        // B Type
        TYPEMAP.put("beq",  InstType.B);
        TYPEMAP.put("bne",  InstType.B);
        TYPEMAP.put("blt",  InstType.B);
        TYPEMAP.put("bge",  InstType.B);
        TYPEMAP.put("bltu", InstType.B);
        TYPEMAP.put("bgeu", InstType.B);

        // J Type
        TYPEMAP.put("jal",  InstType.J);
    }

    public Instruction parse(String instruction) {
        String[] arguments = clean(instruction);
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
