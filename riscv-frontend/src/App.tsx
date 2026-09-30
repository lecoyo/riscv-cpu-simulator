import { Routes, Route } from "react-router";
import Header from './components/Header/Header.tsx'
import Home from './pages/Home.tsx'
import SingleCycle from './pages/SingleCycle.tsx'
import MultiCycle from './pages/MultiCycle.tsx'
import Pipelined from './pages/Pipelined.tsx'

function App() {
  return(
      <>
        <Header />
        <main>
          <Routes>
            <Route path={"/"} element={<Home />} />
            <Route path={"/single-cycle"} element={<SingleCycle />} />
            <Route path="/multi-cycle" element={<MultiCycle />} />
            <Route path="/pipelined" element={<Pipelined />} />
          </Routes>
        </main>
      </>
  )
}

export default App
