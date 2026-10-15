# RISC-V CPU Simulator

> **Work in progress.** This project is under active development.

This project aims to help understand the underlying architecture of RISC-V processors. 
It is largely based on the processor design and explanations presented in **Digital Design and Computer Architecture** by Sarah L. Harris and David Harris.

## Live Demo

**[Try the simulator here](https://riscv-cpu-simulator.onrender.com)**

> The demo runs on a free hosting tier. If nobody has used it for a while, the first load can take up to a minute while the server wakes up.

## Single-Cycle Processor
A web-based RISC-V (RV32I) simulator.
Write assembly code in the browser, a backend service assembles it and runs it on a simulated **single-cycle processor**.
The execution is returned as a step-by-step trace to show every register and property in each step.
The processor design is based on the single-cycle RISC-V microarchitecture by Harris & Harris:
![SingleCycleProcessor](docs/images/SingleCycleProcessor.png)
*Although there are some changes and extensions made to ensure that all J-Type, U-Type and I-Type (JALR) Instructions work properly.*

### Features

- Assembler for almost every RV32I instruction (see below)
- Single-cycle processor model with fetch, decode, execute, memory and write-back in one cycle
- Step-by-step trace with the program counter, the instruction, the registers, the ALU result and the control signals for each instruction.
- Image to show the used paths inside the processor
- Web editor with a simulate button

### Supported instructions

| Type | Instructions |
|---|---|
| Arithmetic / logic | `add`, `sub`, `and`, `or`, `xor`, `sll`, `srl`, `sra`, `slt`, `sltu` |
| Immediate | `addi`, `andi`, `ori`, `xori`, `slti`, `sltiu`, `slli`, `srli`, `srai` |
| Upper immediate | `lui`, `auipc` |
| Load | `lw`, `lh`, `lhu`, `lb`, `lbu` |
| Store | `sw`, `sh`,  `sb` |
| Branch | `beq`, `bne`, `blt`, `bge`, `bltu`, `bgeu` |
| Jump | `jal`, `jalr` |

### Assembly syntax

- **Comments** start with `#` and run to the end of the line.
- **Labels** end with a colon (`loop:`) and can be used as targets for branches and jumps.
- Branch and jump targets can also be written as relative byte offsets, for example `beq x1, x2, 8`.
- Register names can be written in their corresponding ABI names:

| Register | ABI Name |
| --- | --- |
| x0 | zero |
| x1 | ra |
| x2 | sp |
| x3 | gp |
| x4 | tp |
| x5-7 | t0-2 |
| x8 | s0 / fp |
| x9 | s1 |
| x10-11 | a0-1 |
| x12-17 | a2-7 |
| x18-27 | s2-11 |
| x28-31 | t3-6 |

### Example

```
addi t0, zero, 20    # t0 = 0 + 20
addi t1, t0, 2       # t1 = t0 + 2
add t3, t0, t1       # t3 = t0 + t1
```

After running this program, `t3` contains `42`.

### Architecture

- **Registers:** 32 registers, `x0` (`zero`) is hardwired to zero, `x2` (`sp`) starts at half of the memory size
- **Data memory:** byte-addressable, little-endian, supports byte, halfword and word accesses, base size is 2048
- **Instruction memory:** the assembled program, little-endian
- **Components:** register file, ALU, immediate extender, main control unit and ALU control

The simulation ends when the program counter moves past the last instruction.

## Project Structure

```
riscv-cpu-simulator/
├── riscv-backend/     -> Spring Boot backend (Gradle)
└── riscv-frontend/    -> React + TypeScript frontend (Vite)
```

### Backend (`riscv-backend`)

Spring Boot application that assembles the code and simulates the processor.
It runs on port `8080` and provides `POST /api/simulate`, which takes the assembly source and returns the execution trace.

```
cd riscv-backend
./gradlew bootRun
```

Run the tests with:

```
./gradlew test
```

### Frontend (`riscv-frontend`)

React + TypeScript app built with Vite. During development, requests to `/api` are proxied to the backend on port `8080`.

```
cd riscv-frontend
npm install
npm run dev
```

The app is then available at `http://localhost:5173`.

## Requirements

- JDK (for the Spring Boot backend)
- Node.js and npm (for the frontend)