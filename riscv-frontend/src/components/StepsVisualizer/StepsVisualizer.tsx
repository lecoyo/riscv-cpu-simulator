import './StepsVisualizer.css'
import type { CpuSnapshot } from '../../types/types.ts'
import { useState } from 'react'

interface StepsVisualizerProps {
    trace: CpuSnapshot[]
}

function StepsVisualizer({ trace }: StepsVisualizerProps) {
    const [index, setIndex] = useState(0)

    if (trace.length === 0) {
        return <div className="step-visualizer">No program simulated yet.</div>
    }

    const current = trace[index]

    return (
        <div className="step-visualizer">
            <div>Instruction: 0x{current.instruction.toString(16)}</div>
            <div>Instruction-binary: {current.instruction.toString(2).padStart(32, '0')}</div>
            <div>PC: 0x{current.pc.toString(16)}</div>
            <div>ALU Result: {current.aluResult}</div>
            <div>Registers: {current.registers.map((val, i) => `x${i}=${val}`).join(' ')}</div>

            <button disabled={index === 0} onClick={() => setIndex(i => i - 1)}>
                ← Back
            </button>
            <span> Step {index + 1} / {trace.length} </span>
            <button disabled={index === trace.length - 1} onClick={() => setIndex(i => i + 1)}>
                Next →
            </button>
        </div>
    )
}

export default StepsVisualizer