export interface ControlSignals {
    pcSrc: boolean;
    resultSrc: boolean
    memWrite: boolean
    aluSrc: boolean
    regWrite: boolean
    aluControl: number
    immSrc: number
}

export interface CpuSnapshot {
    pc: number
    instruction: number
    controlSignals: ControlSignals
    registers: number[]
    aluResult: number
    immExt: number
    memWrite: boolean
    instructionType: InstructionFormat
}

export type InstructionFormat =
    | "I_TYPE_LOAD"
    | "I_TYPE_ALU"
    | "I_TYPE_JALR"
    | "U_TYPE_LUI"
    | "U_TYPE_AUIPC"
    | "S_TYPE"
    | "R_TYPE"
    | "B_TYPE"
    | "J_TYPE"

export interface Package {
    cpuSnapshot: CpuSnapshot
    instructionString: string
}