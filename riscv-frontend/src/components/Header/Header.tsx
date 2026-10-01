import './Header.css'
import icon from '../../assets/processor-icon.svg'
import { NavLink } from "react-router";

function Header() {
    return(
        <div className={"header"}>
            <div className={"header-brand"}>
                <img src={icon} alt={"Lecoyo-logo"} className={"header-logo"}/>
                <h1>RISC-V CPU Simulator</h1>
            </div>
            <ul className={"navbar"}>
                <li><NavLink to={"/"} end>Home</NavLink></li>
                <li><NavLink to={"/single-cycle"}>Single-cycle Processor</NavLink></li>
                <li><NavLink to={"/multi-cycle"}>Multi-cycle Processor</NavLink></li>
                <li><NavLink to={"/pipelined"}>Pipelined Processor</NavLink></li>
            </ul>
        </div>
    )
}

export default Header;