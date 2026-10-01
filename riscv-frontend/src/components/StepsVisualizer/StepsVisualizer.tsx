import './StepsVisualizer.css'

function StepsVisualizer() {
    return (
        <div className={"step-visualizer"}>
            <div>Instruction: </div>
            <div>Instruction-binary: </div>
            <div>PC: </div>
            <div>ALU Result: </div>
            <div>Registers: </div>
        </div>
    );
}

export default StepsVisualizer;