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
}