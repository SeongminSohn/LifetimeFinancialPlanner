import { BrowserRouter, Routes, Route } from "react-router-dom";
import Homepage from "./pages/homepage.jsx";
import Profset from "./pages/profileSetting.jsx"
import Loginpage from "./pages/logInpage.jsx"
import Taxm from "./pages/taxManagement.jsx"
import Imex from "./pages/imexData.jsx"
import EveM from "./pages/eventManagement.jsx"
import FinP from "./pages/financialPlanning.jsx"
import Signup from "./pages/signup.jsx"
import "./App.css";


function App() {
    return (
        <>
            <BrowserRouter>
                <Routes>
                    <Route path="/" element={<Homepage />} />
                    <Route path="/Homepage" element={<Homepage />} />
                    <Route path="/Profset" element={<Profset />} />
                    <Route path="/Loginpage" element={<Loginpage />} />
                    <Route path="/Taxm" element={<Taxm />} />
                    <Route path="/Imex" element={<Imex />} />
                    <Route path="/EveM" element={<EveM />} />
                    <Route path="/FinP" element={<FinP />} />
                    <Route path="/Signup" element={<Signup />} />
                </Routes>
            </BrowserRouter>
        </>
    );
}

export default App;
