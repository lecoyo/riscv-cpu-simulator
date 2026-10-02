import './StepsVisualizer.css'
import type {CpuSnapshot} from '../../types/types.ts'
import React, {useState} from 'react'

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
            <div className={"general"}>
                <div className={"step-counter"}>
                    <button className={"button-left"} disabled={index === 0} onClick={() => setIndex(i => i - 1)}>
                        Back
                    </button>
                    <span className={"step"}>{index + 1} / {trace.length}</span>
                    <button className={"button-right"} disabled={index === trace.length - 1} onClick={() => setIndex(i => i + 1)}>
                        Next
                    </button>
                </div>
                <div className={"general-output"}>
                    <div>
                        Instruction-binary: {
                        (current.instruction >>> 0).toString(2).padStart(32, '0')
                    }
                    </div>
                    <div>
                        Instruction-hexadecimal: 0x{
                        (current.instruction >>> 0).toString(16).padStart(8, '0')
                    }
                    </div>
                    <div>PC: 0x{current.pc.toString(16)}</div>
                    <div>ALU Result: {current.aluResult}</div>
                </div>
            </div>

            <div className="registers">
                {current.registers.map((val, i) => (
                    <React.Fragment key={i}>
                        {i <= 9 ? (
                            <div className="register register-single-digit">
                                x{i} = {val}
                            </div>
                        ) : (
                            <div className="register">
                                x{i} = {val}
                            </div>
                        )}
                    </React.Fragment>
                ))}
            </div>
        </div>
    )
}

export default StepsVisualizer