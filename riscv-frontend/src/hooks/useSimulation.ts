import { useState } from 'react'
import type { CpuSnapshot } from "../types/types.ts";

interface UseSimulationResult {
    trace: CpuSnapshot[]
    loading: boolean
    error: string | null
    simulationId: number
    simulate: (code: string) => Promise<void>
}

export function useSimulation(): UseSimulationResult {
    const [trace, setTrace] = useState<CpuSnapshot[]>([])
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)
    const [simulationId, setSimulationId] = useState(0)

    async function simulate(code: string) {
        setLoading(true)
        setError(null)
        try {
            const res = await fetch('/api/simulate', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ code })
            })

            if (!res.ok) {
                throw new Error(`Simulation failed: ${res.status}`)
            }

            const data: CpuSnapshot[] = await res.json();
            setTrace(data)
            setSimulationId(id => id + 1)
        } catch (e) {
            setError(e instanceof Error ? e.message : 'Unknown error')
            setTrace([])
        } finally {
            setLoading(false)
        }
    }

    return { trace, loading, error, simulationId, simulate }
}