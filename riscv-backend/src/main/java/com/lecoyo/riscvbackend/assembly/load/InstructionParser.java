package com.lecoyo.riscvbackend.assembly.load;

import com.lecoyo.riscvbackend.assembly.Instruction;
import com.lecoyo.riscvbackend.assembly.types.IType;
import com.lecoyo.riscvbackend.assembly.types.InstType;

import java.util.HashMap;

public class InstructionParser {
    private static final HashMap<String, InstInfo> INSTMAP = new HashMap<>();
    private static final HashMap<String, Byte> REGMAP = new HashMap<>();

    static final int NONE = -1;

    record InstInfo(InstType type, int opcode, int funct3, int funct7) {}

    private static void put(String name, InstType t, int opcode, int funct3, int funct7) {
        INSTMAP.put(name, new InstInfo(t, opcode, funct3, funct7));
    }

    // TODO this sucks ASS so please don't hate, I will make a JSON for this in the future
    static {
        // I Type
        put("lb",    InstType.I, 0b0000011, 0b000, NONE);
        put("lh",    InstType.I, 0b0000011, 0b001, NONE);
        put("lw",    InstType.I, 0b0000011, 0b010, NONE);
        put("lbu",   InstType.I, 0b0000011, 0b100, NONE);
        put("lhu",   InstType.I, 0b0000011, 0b101, NONE);
        put("addi",  InstType.I, 0b0010011, 0b000, NONE);
        put("slli",  InstType.I, 0b0010011, 0b001, 0b0000000);
        put("slti",  InstType.I, 0b0010011, 0b010, NONE);
        put("sltiu", InstType.I, 0b0010011, 0b011, NONE);
        put("xori",  InstType.I, 0b0010011, 0b100, NONE);
        put("srli",  InstType.I, 0b0010011, 0b101, 0b0000000);
        put("srai",  InstType.I, 0b0010011, 0b101, 0b0100000);
        put("ori",   InstType.I, 0b0010011, 0b110, NONE);
        put("andi",  InstType.I, 0b0010011, 0b111, NONE);
        put("jalr",  InstType.I, 0b1100111, 0b000, NONE);

        // U Type
        put("auipc", InstType.U, 0b0010111, NONE, NONE);
        put("lui",   InstType.U, 0b0110111, NONE, NONE);

        // S Type
        put("sb",    InstType.S, 0b0100011, 0b000, NONE);
        put("sh",    InstType.S, 0b0100011, 0b001, NONE);
        put("sw",    InstType.S, 0b0100011, 0b010, NONE);

        // R Type
        put("add",   InstType.R, 0b0110011, 0b000, 0b0000000);
        put("sub",   InstType.R, 0b0110011, 0b000, 0b0100000);
        put("sll",   InstType.R, 0b0110011, 0b001, 0b0000000);
        put("slt",   InstType.R, 0b0110011, 0b010, 0b0000000);
        put("sltu",  InstType.R, 0b0110011, 0b011, 0b0000000);
        put("xor",   InstType.R, 0b0110011, 0b100, 0b0000000);
        put("srl",   InstType.R, 0b0110011, 0b101, 0b0000000);
        put("sra",   InstType.R, 0b0110011, 0b101, 0b0100000);
        put("or",    InstType.R, 0b0110011, 0b110, 0b0000000);
        put("and",   InstType.R, 0b0110011, 0b111, 0b0000000);

        // R Type (M extension)
        put("mul",    InstType.R, 0b0110011, 0b000, 0b0000001);
        put("mulh",   InstType.R, 0b0110011, 0b001, 0b0000001);
        put("mulhsu", InstType.R, 0b0110011, 0b010, 0b0000001);
        put("mulhu",  InstType.R, 0b0110011, 0b011, 0b0000001);
        put("div",    InstType.R, 0b0110011, 0b100, 0b0000001);
        put("divu",   InstType.R, 0b0110011, 0b101, 0b0000001);
        put("rem",    InstType.R, 0b0110011, 0b110, 0b0000001);
        put("remu",   InstType.R, 0b0110011, 0b111, 0b0000001);

        // B Type
        put("beq",   InstType.B, 0b1100011, 0b000, NONE);
        put("bne",   InstType.B, 0b1100011, 0b001, NONE);
        put("blt",   InstType.B, 0b1100011, 0b100, NONE);
        put("bge",   InstType.B, 0b1100011, 0b101, NONE);
        put("bltu",  InstType.B, 0b1100011, 0b110, NONE);
        put("bgeu",  InstType.B, 0b1100011, 0b111, NONE);

        // J Type
        put("jal",   InstType.J, 0b1101111, NONE, NONE);
    }

    static {
        REGMAP.put("zero", (byte) 0);
        REGMAP.put("ra", (byte) 1);
        REGMAP.put("sp", (byte) 2);
        REGMAP.put("gp", (byte) 3);
        REGMAP.put("tp", (byte) 4);

        for (int i = 0; i <= 2; i++) REGMAP.put("t" + i, (byte) (5 + i));

        REGMAP.put("s0", (byte) 8);
        REGMAP.put("fp", (byte) 8);
        REGMAP.put("s1", (byte) 9);

        for (int i = 0; i <= 7; i++) REGMAP.put("a" + i, (byte) (10 + i));
        for (int i = 2; i <= 11; i++) REGMAP.put("s" + i, (byte) (18 + (i - 2)));
        for (int i = 3; i <= 6; i++) REGMAP.put("t" + i, (byte) (28 + (i - 3)));
        for (int i = 0; i <= 31; i++) REGMAP.put("x" + i, (byte) i);
    }

    public Instruction parse(int pc, String instruction) {
        String[] arguments = clean(instruction);
        InstInfo args = INSTMAP.get(arguments[0]);

        switch(args.type) {
            case InstType.I: {
                return new IType(
                        (byte) args.opcode,
                        REGMAP.get(arguments[1]),
                        (byte) args.funct3,
                        REGMAP.get(arguments[2]),
                        Byte.parseByte(arguments[3])
                );
            }
            default: {
                System.out.println("Unknown or non implemented instruction " + instruction + ".");
            }
        }
    }

    /**
     * Splits the given
     * @param instruction
     * @return
     */
    public String[] clean(String instruction) {
        instruction = instruction.replaceAll("[+.^:,]", "");
        return instruction.split(" ");
    }

    public boolean isLabel(String line) {
        return line.endsWith(":");
    }
}
