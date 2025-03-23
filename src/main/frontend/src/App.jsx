import { BrowserRouter, Routes, Route } from "react-router-dom";
import Homepage from "./pages/homepage.jsx";
import Profset from "./pages/profileSetting.jsx"
import Loginpage from "./pages/logInpage.jsx"
import Taxm from "./pages/taxManagement.jsx"
import IncomePage from "./pages/incomePage.jsx"
import InvestEdit from "./pages/investEdit.jsx"
import ExpenseEdit from "./pages/expenseEdit.jsx"
import Signup from "./pages/signup.jsx"
import SimulationPage from "./pages/simulationPage.jsx";
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
                    <Route path="/IncomePage" element={<IncomePage />} />
                    <Route path="/InvestEdit" element={<InvestEdit />} />
                    <Route path="/ExpenseEdit" element={<ExpenseEdit />} />
                    <Route path="/Signup" element={<Signup />} />
                    <Route path="/SimulationPage" element={<SimulationPage />} />
                </Routes>
            </BrowserRouter>
        </>
    );
}

export default App;
