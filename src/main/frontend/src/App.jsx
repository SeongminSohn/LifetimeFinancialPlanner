import {BrowserRouter, Routes, Route, useLocation} from "react-router-dom";
import Homepage from "./pages/homepage.jsx";
import Profset from "./pages/profileSetting.jsx"
import Loginpage from "./pages/logInpage.jsx"
import ImportExp from "./pages/impExp.jsx"
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
import IncomeSetting from "./pages/incomeSetting.jsx"
import ExpenseSetting from "./pages/expenseSetting.jsx"
import SimulationManagement from "./pages/simluationManagement.jsx"
import InvestEventEditPage from "./pages/investEventEditPage.jsx";
import IncomeEventEdit from "./pages/incomeEventEdit.jsx";
import ExpenseEventEdit from "./pages/expenseEventEditPage.jsx"
import InvestTypeEdit from "./pages/investTypeEditPage.jsx"
import InvestTypeManage from "./pages/investTypeManage.jsx"
import useDisableNumberWheel from "./hooks/useDisableNumberWheel";
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
    useDisableNumberWheel();

    return (
        <>
            <BrowserRouter>
                <ScrollToTop />
                <Routes>
                    <Route path="/" element={<Homepage />} />
                    <Route path="/Homepage" element={<Homepage />} />
                    <Route path="/Profset" element={<Profset />} />
                    <Route path="/Loginpage" element={<Loginpage />} />
                    <Route path="/ImportExp" element={<ImportExp />} />
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
                    <Route path="/IncomeSetting" element={<IncomeSetting />} />
                    <Route path="/ExpenseSetting" element={<ExpenseSetting />} />
                    <Route path="/SimulationManagement" element={<SimulationManagement />} />
                    <Route path="/invest-events/edit/:id" element={<InvestEventEditPage />} />
                    <Route path="/income-events/edit/:id" element={<IncomeEventEdit />} />
                    <Route path="/expense-events/edit/:id" element={<ExpenseEventEdit />} />
                    <Route path="/investment-types/edit/:id" element = {<InvestTypeEdit/>} />
                    <Route path="/investments/edit/:id" element = {<InvestTypeManage/>} />
                </Routes>
            </BrowserRouter>
        </>
    );
}

export default App;
