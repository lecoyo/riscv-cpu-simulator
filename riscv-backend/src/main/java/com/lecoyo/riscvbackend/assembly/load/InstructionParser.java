package com.lecoyo.riscvbackend.assembly.load;

import com.lecoyo.riscvbackend.assembly.Instruction;
import com.lecoyo.riscvbackend.assembly.types.*;

import java.util.HashMap;

public class InstructionParser {
    private static final HashMap<String, InstInfo> INSTMAP = new HashMap<>();
    private static final HashMap<String, Byte> REGMAP = new HashMap<>();

    private HashMap<String, Integer> labels;

    static final int NONE = -1;

    record InstInfo(InstType type, int opcode, int funct3, int funct7) {}

    public InstructionParser(HashMap<String, Integer> labels) {
        this.labels = labels;
    }

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

        if(args == null) throw new IllegalArgumentException("Unknown or invalid instruction: " + instruction);

        // check instruction length
        int needed = opLengthByType(args.type);
        if (arguments.length < needed)
            throw new IllegalArgumentException(arguments[0] + ": expected " + (needed - 1) + " operands");

        switch(args.type) {
            case InstType.I: {
                byte rd = reg(arguments[1]);
                byte rs1;
                int imm;

                if (args.opcode == 0b0000011) {
                    rs1 = reg(arguments[3]);
                    imm = Integer.decode(arguments[2]);
                }
                else if (args.opcode == 0b1100111){
                    rs1 = reg(arguments[2]);
                    imm = isNumber(arguments[3]) ? Integer.decode(arguments[3])
                            : labels.get(arguments[3]);
                }
                else {
                    rs1 = reg(arguments[2]);
                    imm = Integer.decode(arguments[3]);
                }

                if (args.funct7 != NONE) {
                    if (imm < 0 || imm > 31)
                        throw new IllegalArgumentException(arguments[0] + ": shift amount out of range: " + imm);
                    imm |= args.funct7 << 5;
                } else if (imm < -2048 || imm > 2047) {
                    throw new IllegalArgumentException(arguments[0] + ": immediate out of range: " + imm);
                }

                return new IType(arguments[0], (byte) args.opcode, rd, (byte) args.funct3, rs1, (short) imm);
            }
            case InstType.U: {
                int imm = Integer.decode(arguments[2]);
                if(imm < 0 || imm > 0xFFFFF) throw new IllegalArgumentException(arguments[0] + ": immediate out of range: " + imm);
                return new UType(
                        arguments[0],
                        (byte) args.opcode,
                        reg(arguments[1]),
                        imm
                );
            }
            case InstType.B: {
                byte rs1 = reg(arguments[1]);
                byte rs2 = reg(arguments[2]);
                int target = resolveTarget(arguments[3], pc);
                int offset = target - pc;
                checkOffset(offset, 13, arguments[0]);

                // imm[12|10:5]
                byte imm1 = (byte) ((((offset >> 12) & 0x1) << 6)
                        |  ((offset >> 5)  & 0x3F));
                // imm[4:1|11]
                byte imm2 = (byte) ((((offset >> 1) & 0xF) << 1)
                        |  ((offset >> 11) & 0x1));

                return new BType(arguments[0], (byte) args.opcode(), target, pc, imm1, (byte) args.funct3, rs1, rs2, imm2);
            }
            case InstType.J: {
                byte rd;
                String targetArg;
                if (arguments.length == 2) {
                    rd = reg("ra");
                    targetArg = arguments[1];
                } else {
                    rd = reg(arguments[1]);
                    targetArg = arguments[2];
                }
                int offset = resolveTarget(targetArg, pc) - pc;
                checkOffset(offset, 21, arguments[0]);

                return new JType(arguments[0], (byte) args.opcode, rd, offset);
            }
            case InstType.R: {
                return new RType(
                        arguments[0],
                        (byte) args.opcode,
                        reg(arguments[1]),
                        (byte) args.funct3,
                        reg(arguments[2]),
                        reg(arguments[3]),
                        (byte) args.funct7
                );
            }
            case InstType.S: {
                int imm = Integer.decode(arguments[2]);
                if (imm < -2048 || imm > 2047)
                    throw new IllegalArgumentException(arguments[0] + ": immediate out of range: " + imm);

                return new SType(
                        arguments[0],
                        (byte) args.opcode,
                        (byte) (imm & 0x1F),
                        (byte) args.funct3,
                        reg(arguments[3]),
                        reg(arguments[1]),
                        (byte) ((imm >> 5) & 0x7F)
                );
            }
            default: {
                throw new IllegalStateException("Unknown instruction type " + args.type);
            }
        }
    }

    /**
     * Splits the given
     * @param instruction
     * @return
     */
    public String[] clean(String instruction) {
        instruction = instruction.replaceAll("[+^:,()]", " ");
        return instruction.trim().split("\\s+");
    }

    public static InstType getType(Instruction inst) {
        return INSTMAP.get(inst.mnemonic).type();
    }

    public static boolean isLabel(String line) {
        return line.endsWith(":");
    }

    private int resolveTarget(String arg, int pc) {
        Integer address = labels.get(arg);
        if (address != null) return address;
        try {
            return pc + Integer.decode(arg);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Unknown label or offset: " + arg);
        }
    }

    /**
     * Verifies that a jump offset is encodable in given immediate width.
     * Offset must be even and must fit into a signed value of {@code bits} bits.
     * @param offset
     * @param mnemonic
     */
    private void checkOffset(int offset, int bits, String mnemonic) {
        int max = (1 << (bits - 1)) - 1;
        int min = -(1 << (bits - 1));
        if ((offset & 1) != 0 || offset < min || offset > max) {
            throw new IllegalArgumentException(mnemonic + ": offset " + offset
                    + " out of range or not 2-byte aligned");
        }
    }

    private boolean isNumber(String str) {
        return str.matches("[0-9]+");
    }

    private int opLengthByType(InstType type) {
        return switch (type) {
            case I, R, B, S -> 4;
            case U -> 3;
            case J -> 2;
        };
    }

    private byte reg(String name) {
        Byte r = REGMAP.get(name);
        if (r == null) throw new IllegalArgumentException("Unknown register: " + name);
        return r;
    }
}
