import { useSimulation } from '../../hooks/useSimulation.ts'
import CodeEditor from "../../components/CodeEditor/CodeEditor.tsx";
import StepsVisualizer from "../../components/StepsVisualizer/StepsVisualizer.tsx";
import './Home.css'
import { useState } from "react";

function Home() {
    const { trace, loading, error, simulate } = useSimulation()
    const [currentIndex, setCurrentIndex] = useState(0)

    const currentSnapshot = trace[currentIndex]

    async function handleSubmit(code: string) {
        await simulate(code)
        setCurrentIndex(0)
    }

    return (
        <div className={"home"}>
            <CodeEditor onSubmit={handleSubmit} loading={loading} currentPc={currentSnapshot?.pc}/>
            {error && <div className="error">{error}</div>}
            <StepsVisualizer trace={trace} index={currentIndex} onIndexChange={setCurrentIndex} />
        </div>
    );
}

export default Home;