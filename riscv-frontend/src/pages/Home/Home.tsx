import { useSimulation } from '../../hooks/useSimulation.ts'
import CodeEditor from "../../components/CodeEditor/CodeEditor.tsx";
import StepsVisualizer from "../../components/StepsVisualizer/StepsVisualizer.tsx";
import './Home.css'

function Home() {
    const { trace, loading, error, simulationId, simulate } = useSimulation()

    return (
        <div className={"home"}>
            <CodeEditor onSubmit={simulate} loading={loading} />
            {error && <div className="error">{error}</div>}
            <StepsVisualizer trace={trace} key={simulationId}/>
        </div>
    );
}

export default Home;