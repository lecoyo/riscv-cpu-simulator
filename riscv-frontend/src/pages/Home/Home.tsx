import './Home.css'
import { Link } from 'react-router'

function Home() {
    return (
        <div className="home">
            <div className="article-single-cycle">
                <h2>RISC-V Single-Cycle Processor Simulator</h2>
                <p>
                    Write RISC-V assembly, run it, and step through the execution
                    instruction by instruction. See all 32 registers and various properties
                    update with every step.
                </p>

                <ul className="home-features">
                    <li>Step forward and backward through the whole program</li>
                    <li>Instruction type, binary and hex encoding at each step</li>
                    <li>Datapath diagram for every instruction format</li>
                    <li>Live register view</li>
                </ul>

                <Link to="/single-cycle" className="home-link">
                    Single-Cycle Simulator
                </Link>
            </div>
        </div>
    );
}

export default Home