import './StepsVisualizer.css'
import type {Package} from '../../types/types.ts'
import skipB from '../../assets/skip-backward.svg'
import skipF from '../../assets/skip-forward.svg'
import React from "react"

interface StepsVisualizerProps {
    trace: Package[]
    index: number
    onIndexChange: (index: number) => void
}

function StepsVisualizer({ trace, index, onIndexChange }: StepsVisualizerProps) {
    if (trace.length === 0) {
        return <div className="step-visualizer">No program simulated yet.</div>
    }

    const current = trace[index]

    return (
        <div className="step-visualizer">
            <div className={"general"}>
                <div className={"step-counter"}>
                    <button className={"step-counter-outer skip-right"} disabled={index === 0} onClick={() => onIndexChange(0)}>
                        <img alt={"skipB"} src={skipB} />
                    </button>
                    <div className={"step-counter-inner"}>
                        <button className={"button-left"} disabled={index === 0} onClick={() => onIndexChange(index - 1)}>
                            Back
                        </button>
                        <span className={"step"}>{index + 1} / {trace.length}</span>
                        <button className={"button-right"} disabled={index === trace.length - 1} onClick={() => onIndexChange(index + 1)}>
                            Next
                        </button>
                    </div>
                    <button className={"step-counter-outer skip-right"} disabled={index === trace.length - 1} onClick={() => onIndexChange(trace.length - 1)}>
                        <img alt={"skipF"} src={skipF} />
                    </button>
                </div>

                <div className={"general-output"}>
                    <div>
                        Instruction: {
                        (current.instructionString)
                    }
                    </div>
                    <div>
                        Binary: {
                        (current.cpuSnapshot.instruction >>> 0).toString(2).padStart(32, '0')
                    }
                    </div>
                    <div>
                        Hexadecimal: 0x{
                        (current.cpuSnapshot.instruction >>> 0).toString(16).padStart(8, '0')
                    }
                    </div>
                    <div>PC: 0x{current.cpuSnapshot.pc.toString(16)}</div>
                    <div>ALU Result: {current.cpuSnapshot.aluResult}</div>
                </div>
            </div>

            <div className="registers">
                {current.cpuSnapshot.registers.map((val, i) => (
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