import { useSimulation } from '../../hooks/useSimulation.ts'
import CodeEditor from "../../components/CodeEditor/CodeEditor.tsx";
import StepVisualizer from "../../components/StepsVisualizer/StepVisualizer.tsx";
import './SingleCycle.css'
import { useState } from "react";
import RegisterView from "../../components/RegisterView/RegisterView.tsx";

function SingleCycle() {
    const { trace, loading, error, simulate } = useSimulation()
    const [currentIndex, setCurrentIndex] = useState(0)

    async function handleSubmit(code: string) {
        await simulate(code)
        setCurrentIndex(0)
    }

    const current = trace[currentIndex]

    return (
        <div className={"single-cycle"}>
            <CodeEditor onSubmit={handleSubmit} loading={loading} />
            {error && <div className="error">{error}</div>}
            <StepVisualizer trace={trace} index={currentIndex} onIndexChange={setCurrentIndex} />
            <RegisterView registers={current?.cpuSnapshot.registers}></RegisterView>
        </div>
    )
}

export default SingleCycle