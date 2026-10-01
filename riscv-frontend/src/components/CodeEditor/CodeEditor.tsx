import { useState } from 'react'
import './CodeEditor.css'
import play from '../../assets/play.svg'

function CodeEditor() {
    const [text, setText] = useState('')

    async function simulate() {
        const res = await fetch('/api/simulate', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ code: text })
            })
        const data = await res.text();
        console.log(data);
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
            <button onClick={simulate}><img alt={"play-icon"} src={play} />Simulate</button>
        </div>
    )
}

export default CodeEditor