import { useState } from 'react'
import './CodeEditor.css'
import play from '../../assets/play.svg'

interface CodeEditorProps {
    onSubmit: (code: string) => void
    loading: boolean
    currentPc?: number
}

function CodeEditor({ onSubmit, loading }: CodeEditorProps) {
    const [text, setText] = useState('')

    function handleSimulate() {
        onSubmit(text)
    }

    return (
        <div className={"code-editor"}>
            <textarea
                className={"code-input"}
                value={text}
                onChange={(e) => setText(e.target.value)}
                placeholder="Type RISC-V code here..."
                spellCheck={false}
                rows={10}
            />
            <button onClick={handleSimulate} disabled={loading}>
                <img alt={"play-icon"} src={play} />
                {loading ? 'Simulating...' : 'Simulate'}
            </button>
        </div>
    )
}

export default CodeEditor