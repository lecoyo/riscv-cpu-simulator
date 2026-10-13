import './RegisterView.css'

interface RegisterViewProps {
    registers?: number[]
}

function RegisterView({ registers }: RegisterViewProps) {
    if (!registers) return null;

    return (
        <div className={"registers"}>
            {registers.map((val, i) => (
                <div key={i} className={"register"}>
                    x{i} = {val}
                </div>
            ))}
        </div>
    )
}

export default RegisterView