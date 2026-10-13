import './StepVisualizer.css'
import type {Package} from '../../types/types.ts'
import skipB from '../../assets/skip-backward.svg'
import skipF from '../../assets/skip-forward.svg'
import scp_i_type_alu from '../../assets/scp-i-type-alu.jpg'
import scp_i_type_load from '../../assets/scp-i-type-load.jpg'
import scp_i_type_jalr from '../../assets/scp-i-type-jalr.jpg'
import scp_r_type from '../../assets/scp-r-type.jpg'
import scp_s_type from '../../assets/scp-s-type.jpg'
import scp_b_type from '../../assets/scp-b-type.jpg'
import scp_j_type from '../../assets/scp-j-type.jpg'
import scp_u_type_auipc from '../../assets/scp-u-type-auipc.jpg'
import scp_u_type_lui from '../../assets/scp-u-type-lui.jpg'

interface StepsVisualizerProps {
    trace: Package[]
    index: number
    onIndexChange: (index: number) => void
}

function StepVisualizer({ trace, index, onIndexChange }: StepsVisualizerProps) {
    if (trace.length === 0) {
        return <div className="step-visualizer">No program simulated yet.</div>
    }

    const current = trace[index]

    const bin = (current.cpuSnapshot.instruction >>> 0).toString(2).padStart(32, '0')
    const groupedBin = bin.replace(/(.{4})(?=.)/g, '$1 ')

    const INSTRUCTION_VIEWS = {
        I_TYPE_LOAD: <span>I-Type (Load)</span>,
        I_TYPE_ALU: <span>I-Type (ALU)</span>,
        I_TYPE_JALR: <span>I-Type (JALR)</span>,
        U_TYPE_LUI: <span>U-Type (LUI)</span>,
        U_TYPE_AUIPC: <span>U-Type (AUIPC)</span>,
        S_TYPE: <span>S-Type</span>,
        R_TYPE: <span>R-Type</span>,
        B_TYPE: <span>B-Type</span>,
        J_TYPE: <span>J-Type</span>,
    };

    const INSTRUCTION_IMG = {
        I_TYPE_LOAD: <img src={scp_i_type_load} alt={"scp_i_type_load"} />,
        I_TYPE_ALU: <img src={scp_i_type_alu} alt={"scp_i_type_alu"} />,
        I_TYPE_JALR: <img src={scp_j_type} alt={"scp_i_type_jalr"} />,
        U_TYPE_LUI: <img src={scp_u_type_lui} alt={"scp_u_type_lui"} />,
        U_TYPE_AUIPC: <img src={scp_u_type_auipc} alt={"scp_u_type_auipc"} />,
        S_TYPE: <img src={scp_s_type} alt={"scp_s_type"} />,
        R_TYPE: <img src={scp_r_type} alt={"scp_r_type"} />,
        B_TYPE: <img src={scp_b_type} alt={"scp_b_type"} />,
        J_TYPE: <img src={scp_i_type_jalr} alt={"scp_j_type"} />,
    };

    return (
        <div className="step-visualizer">
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
                    Instruction-Type: {
                    INSTRUCTION_VIEWS[current.cpuSnapshot.instructionType]
                }
                </div>
                <div>
                    Binary: {groupedBin}
                </div>
                <div>
                    Hexadecimal: 0x{
                    (current.cpuSnapshot.instruction >>> 0).toString(16).padStart(8, '0')
                }
                </div>
                <div>PC: 0x{current.cpuSnapshot.pc.toString(16)}</div>
                <div>ALU Result: {current.cpuSnapshot.aluResult}</div>
            </div>

            <div className={"image-output"}>
                {
                    INSTRUCTION_IMG[current.cpuSnapshot.instructionType]
                }
            </div>
        </div>
    )
}

export default StepVisualizer