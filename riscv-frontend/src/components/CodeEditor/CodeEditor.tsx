import { useState } from 'react'
import './CodeEditor.css'
import play from '../../assets/play.svg'

function CodeEditor() {
    const [text, setText] = useState('')

    return (
        <div className={"code-editor"}>
            <textarea
                className={"code-input"}
                value={text}
                onChange={(e) => setText(e.target.value)}
                placeholder="Type RISC-V code here..."
                spellCheck={"false"}
                rows={10}
            />
            <button onClick={() => console.log(text)}><img alt={"play-icon"} src={play} />Simulate</button>
        </div>
    )
}

export default CodeEditor