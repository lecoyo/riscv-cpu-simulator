import CodeEditor from "../../components/CodeEditor/CodeEditor.tsx";
import StepsVisualizer from "../../components/StepsVisualizer/StepsVisualizer.tsx";
import './Home.css'

function Home() {
    return (
        <div className={"home"}>
            <CodeEditor />
            <StepsVisualizer />
        </div>
    );
}

export default Home;