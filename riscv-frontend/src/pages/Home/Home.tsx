import './Home.css'
import scp from '../../assets/SingleCycleProcessor.svg'
import { Link } from 'react-router'

function Home() {
    return (
        <div className="home">
            <div className="article-single-cycle">
                <h2>RISC-V Single-Cycle Processor Simulator</h2>
                <p>
                    A web-based RISC-V (RV32I) simulator.
                    Write assembly code in the browser, a backend service assembles it and runs it on a simulated single-cycle processor.
                    The execution is returned as a step-by-step trace to show every register and property in each step.
                    The processor design is mostly based on the single-cycle RISC-V microarchitecture by Harris & Harris:
                </p>

                <img src={scp} alt={"Single-Cycle Processor"}/>

                <p className={"remark"}>Although there are some changes and extensions made to ensure that all J-Type, U-Type and I-Type (JALR) Instructions work properly.</p>

                <ul className="home-features">
                    <li>Step forward and backward through the entire program</li>
                    <li>Inspect each instruction's type, binary and hex encoding</li>
                    <li>Follow the datapath diagram for every instruction</li>
                    <li>View registers and control signals at each step</li>
                </ul>

                <Link to="/single-cycle" className="home-link">
                    Single-Cycle Simulator
                </Link>
            </div>
        </div>
    );
}

export default Home