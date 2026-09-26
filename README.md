# RISC-V CPU Simulator

> 🚧 **Work in progress.** This project is under active development. Features, structure, and APIs may change without notice.

A web-based RISC-V (Assembly) simulator: write assembly code in the browser and have it assembled/executed by a backend service.

## Project Structure

```
riscv-cpu-simulator/
├── riscv-backend/     # Spring Boot backend (Gradle)
└── riscv-frontend/    # React + TypeScript frontend (Vite)
```

## Backend (`riscv-backend`)

Spring Boot application handling assembly/execution logic.

```bash
cd riscv-backend
./gradlew bootRun
```

## Frontend (`riscv-frontend`)

React + TypeScript app built with Vite.

```bash
cd riscv-frontend
npm install
npm run dev
```
