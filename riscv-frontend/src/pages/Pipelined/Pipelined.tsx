import {useSimulation} from "../../hooks/useSimulation.ts";
import {useState} from "react";
import CodeEditor from "../../components/CodeEditor/CodeEditor.tsx";
import StepsVisualizer from "../../components/StepsVisualizer/StepsVisualizer.tsx";
import './Pipelined.css'

function Pipelined() {
    const { trace, loading, error, simulate } = useSimulation()
    const [currentIndex, setCurrentIndex] = useState(0)

    async function handleSubmit(code: string) {
        await simulate(code)
        setCurrentIndex(0)
    }

    return (
        <div className={"pipelined"}>
            <CodeEditor onSubmit={handleSubmit} loading={loading} />
            {error && <div className="error">{error}</div>}
            <StepsVisualizer trace={trace} index={currentIndex} onIndexChange={setCurrentIndex} />
        </div>
    );
}

export default Pipelined;