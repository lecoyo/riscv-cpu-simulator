import './Footer.css'
import GithubIcon from '../../assets/github.svg?react'
import DiscordIcon from '../../assets/discord.svg?react'

function Footer() {
    return(
        <div className={"footer"}>
            <a
                href={"https://github.com/lecoyo/riscv-cpu-simulator"}
                aria-label={"GitHub Repository"}
            >
                <GithubIcon className={"github-logo"} />
            </a>

            <p>
                Made by <a href={"https://github.com/leon-tbg"}>Leon Thomasberger</a> and{' '}
                <a href={"https://github.com/SortyFix"}>Yonas Nieder Fernández</a>
            </p>

            <p className={"discord-support"}>
                If you encounter any issues, please contact us on{' '}
                <a
                    href={"https://discord.com/users/660594904212176921"}
                    aria-label={"Leon"}
                >
                    <DiscordIcon className={"discord-logo"} />
                </a>
            </p>
        </div>
    );
}

export default Footer;