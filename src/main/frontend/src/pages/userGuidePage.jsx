import React, { useState, useEffect } from 'react';
import './common.css';
import { useNavigate } from 'react-router-dom';

function UserGuidePage() {
    const navigate = useNavigate();
    const [loggedIn, setLoggedIn] = useState(false);
    const [selectedInstruction, setSelectedInstruction] = useState(null);
    const [openSide, setSide] = useState(false);
    const navPage = useNavigate();

    useEffect(() => {
        const token = localStorage.getItem("token");
        if (token) {
            setLoggedIn(true);
        }
    }, []);

    function sideElements() {
        return (
            openSide && (
                <aside className="sidebar">
                    <button onClick={() => navPage('/Investment')}>View Invest type Status</button>
                    <button onClick={() => navPage('/IncomeSetting')}>View Income Status</button>
                    <button onClick={() => navPage('/ExpenseSetting')}>View Expense Status</button>
                    <button onClick={() => navPage('/ExpenseW')}>Expense Withdrawal Edit</button>
                    <button onClick={() => navPage('/SimulationManagement')}>Invest Event Edit</button>
                    <button onClick={() => navPage('/simulationPage')}>Scenario Simulation</button>
                    <button onClick={() => navPage('/simulationResult')}>Scenario Simulation</button>
                    <button onClick={() => navPage('/ImportExp')}>Import & Export Data</button>
                </aside>
            )
        );
    }

    function toWithDrawal(){
        navPage('/ExpenseW');
    }
    function toIncome() {
        navPage('/IncomePage');
    }
    function toExpense() {
        navPage('/ExpenseEdit');
    }
    function toInvest() {
        navPage('/InvestEdit');
    }
    function toSim() {
        navPage('/simulationPage');
    }
    function toHome() {
        navPage('/Homepage');
    }
    function toProfile() {
        navPage('/Profset');
    }
    function toInvestEvent(){
        navPage("/InvestEvent")
    }

    function toHome(){
        navigate("/Homepage")
    }

    const popupMenu = () => {
        setSide(prev => !prev);
    };

    function instructionInvestment() {
        return (
            <div className = "instructions">
                <h2>Investment Instruction</h2>
                <div>
                    <ul>
                <li>This page shows which investments the user currently holds.</li>
                <li>Click the plus button at the top to set an investment type, which will then be displayed in the investments section.</li>
                <li>In the Investment Type section, you can set the value of the entered investment type and configure its corresponding tax status.</li>
                    <li>Clicking the X button will delete only the value and tax status entered for that investment.</li>
                    </ul>
                </div>
            </div>
        );
    }

    function instructionIncomeEdit() {
        return (
            <div className = "instructions">
                <h2>Income Edit Instruction</h2>
                <div>
                    <ul>
                        <li>On this page, the user can enter their income information.</li>
                        <li>Depending on whether Social Security is marked as Y or N, a total of two different income events can be generated.</li>
                    </ul>
                </div>
            </div>
        );
    }

    function instructionExpenseEdit() {
        return (
            <div className = "instructions">
                <h2>Expense Edit</h2>
                <div>
                    <ul>
                        <li>On this page, the user can enter their expense information.</li>
                        <li>The "Is Discretionary" option at the bottom always functions as "No."</li>
                    </ul>
                </div>
            </div>
        );
    }

    function instructionInvestEdit() {
        return (
            <div className = "instructions">
                <h2>Invest Edit Instruction</h2>
                <div>
                    <ul>
                        <li>On this page, the user can enter their Invest information.</li>
                       <li>Only the following selections are allowed:
                           Cash → YES or NO
                           S&P 500 → YES
                           TAX-EXEMPT BONDS → NO</li>
                    </ul>
                </div>
            </div>
        );
    }

    function instructionExpenseWithdrawal() {
        return (
            <div className = "instructions">
                <h2>Expense Withdrawal Instruction</h2>
                <div>
                    <ul>
                        <li>On this page, user can set the selling order of the investments you hold.</li>
                        <li>When you click, the elements are stacked in the order you click them. If you want to remove an element from the list, simply find and click the name of the element you want to remove within the list.</li>
                        <li>Only the elements for which value and tax status have been entered in the investment section will appear as selectable options.</li>
                    </ul>
                </div>
            </div>
        );
    }

    function instructionInvestEventEdit() {
        return (
            <div className = "instructions">
                <h2>Income Edit Instruction</h2>
                <div>
                    <ul>
                        <li>On this page, the user can enter their income information.</li>
                        <li>Depending on whether Social Security is marked as Y or N, a total of two different income events can be generated.</li>
                    </ul>
                </div>
            </div>
        );
    }

    function instructionSimulation() {
        return (
            <div className = "instructions">
                <h2>Run Simulation Page</h2>
                <div>
                    <ul>
                        <li>Set how many times the simulation will be run.</li>
                    </ul>
                </div>
            </div>
        );
    }

    function homeManage() {
        return (
            <div>
                <p className="logoLetter" style={{ fontSize: 'xx-large', color: 'black', fontWeight: 'bold' }}>
                    Life Financial Planner User Guide
                </p>
                <div className="horizontal">
                <button className="guider" onClick={() => setSelectedInstruction(prev => prev === "investment" ? null : "investment")}>
                    Investment
                </button>
                <button className="guider" onClick={() => setSelectedInstruction(prev => prev === "income" ? null : "income")}>
                    Income Edit
                </button>
                <button className="guider" onClick={() => setSelectedInstruction(prev => prev === "expense" ? null : "expense")}>
                    Expense Edit
                </button>
                <button className="guider" onClick={() => setSelectedInstruction(prev => prev === "invest" ? null : "invest")}>
                    Invest Edit
                </button>
                <button className="guider" onClick={() => setSelectedInstruction(prev => prev === "withdrawal" ? null : "withdrawal")}>
                    Expense Withdrawal Edit
                </button>
                <button className="guider" onClick={() => setSelectedInstruction(prev => prev === "investEvent" ? null : "investEvent")}>
                    Invest Event Edit
                </button>
                <button className="guider" onClick={() => setSelectedInstruction(prev => prev === "Simulation" ? null : "Simulation")}>
                    Scenario Simulation
                </button>
                <button className="guider" disabled style={{backgroundColor: 'grey'}}>
                    Import & Export Data
                </button>
                </div>
            </div>
        );
    }

    function renderInstruction() {
        switch (selectedInstruction) {
            case "investment":
                return instructionInvestment();
            case "income":
                return instructionIncomeEdit();
            case "expense":
                return instructionExpenseEdit();
            case "invest":
                return instructionInvestEdit();
            case "withdrawal":
                return instructionExpenseWithdrawal();
            case "investEvent":
                return instructionInvestEventEdit();
            case "Simulation":
                return instructionSimulation();
            default:
                return null;
        }
    }

    function handleLogout() {
        localStorage.removeItem("token");
        localStorage.removeItem("scenario");
        setLoggedIn(false);
        navigate("/Homepage");
    }

    return (
        <div className="total">
            <nav className="navBarTop">
                <img onClick={toHome} src="/public/caffeineOverloadLogo.png" className="logoSize" alt="logo" />
                <p className="logoLetter">Life Time Financial Planner</p>
                {/*{loggedIn && <button className="commonButton" onClick={handleLogout}>Log Out</button>}*/}
                <div></div>
            </nav>
            <nav className="navBarSub">
                <button className="commonButton" onClick={popupMenu}>Menu</button>
                {sideElements()}
            </nav>
            {homeManage()}
            {renderInstruction()}
        </div>
    );
}

export default UserGuidePage;
