import { Routes, Route } from "react-router";
import Header from './components/Header/Header.tsx'
import Footer from './components/Footer/Footer.tsx'
import Home from './pages/Home/Home.tsx'
import SingleCycle from './pages/SingleCycle/SingleCycle.tsx'
import './App.css'

function App() {
  return(
      <div className={"app"}>
          <Header />
          <main>
              <Routes>
                  <Route path={"/"} element={<Home />} />
                  <Route path={"/single-cycle"} element={<SingleCycle />} />
              </Routes>
          </main>
          <Footer />
      </div>
  )
}

export default App
