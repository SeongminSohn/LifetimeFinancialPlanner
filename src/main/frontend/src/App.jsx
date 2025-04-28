import {BrowserRouter, Routes, Route, useLocation} from "react-router-dom";
import Homepage from "./pages/homepage.jsx";
import Profset from "./pages/profileSetting.jsx"
import Loginpage from "./pages/logInpage.jsx"
import Taxm from "./pages/taxManagement.jsx"
import IncomePage from "./pages/incomePage.jsx"
import InvestEdit from "./pages/investEdit.jsx"
import ExpenseEdit from "./pages/expenseEdit.jsx"
import Signup from "./pages/signup.jsx"
import SimulationPage from "./pages/simulationPage.jsx";
import Investment from "./pages/investment.jsx"
import InvestEvent from "./pages/investEvent.jsx"
import ExpenseW from "./pages/expenseWithdrawl.jsx"
import UserGuide from "./pages/userGuidePage.jsx"
import SimulationResult from "./pages/simulationResult.jsx"
import "./App.css";
import {useEffect} from "react";


function App() {

    function ScrollToTop() {
        const { pathname } = useLocation();

        useEffect(() => {
            window.scrollTo(0, 0);
        }, [pathname]);

        return null;
    }

    return (
        <>
            <BrowserRouter>
                <ScrollToTop />
                <Routes>
                    <Route path="/" element={<Homepage />} />
                    <Route path="/Homepage" element={<Homepage />} />
                    <Route path="/Profset" element={<Profset />} />
                    <Route path="/Loginpage" element={<Loginpage />} />
                    <Route path="/Taxm" element={<Taxm />} />
                    <Route path="/IncomePage" element={<IncomePage />} />
                    <Route path="/InvestEdit" element={<InvestEdit />} />
                    <Route path="/Investment" element={<Investment />} />
                    <Route path="/ExpenseEdit" element={<ExpenseEdit />} />
                    <Route path="/Signup" element={<Signup />} />
                    <Route path="/SimulationPage" element={<SimulationPage />} />
                    <Route path="/InvestEvent" element={<InvestEvent />} />
                    <Route path="/ExpenseW" element={<ExpenseW />} />
                    <Route path="/UserGuide" element={<UserGuide />} />
                    <Route path="/SimulationResult" element={<SimulationResult />} />
                </Routes>
            </BrowserRouter>
        </>
    );
}

export default App;
