import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { ResponsiveContainer, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from 'recharts';
import StackedBarChart from '../component/StackedBarChart.jsx';
function SimulationPage() {
    const [loggedIn, setLoggedIn] = useState(false);
    useEffect(() => {
        const token = localStorage.getItem('token');
        if (token) setLoggedIn(true);
    }, []);

    const [openSide, setSide] = useState(false);
    const navPage = useNavigate();

    const popupMenu = () => setSide(prev => !prev);
    const sideElements = () =>
        openSide && (
            <aside className="sidebar">
                <button onClick={toIncome}>Income Edit</button>
                <button onClick={toExpense}>Expense Edit</button>
                <button onClick={toInvest}>Invest Edit</button>
                <button onClick={toWithDrawal}>Expense Withdrawal Edit</button>
                <button onClick={toInvestEvent}>Invest Event Edit</button>
                <button disabled>Import & Export Data</button>
            </aside>
        );

    function toSim() {
        navPage('/simulationPage');
    }
    function toUserGuide() { navPage('/UserGuide'); }
    function toWithDrawal() { navPage('/ExpenseW'); }
    function toIncome() { navPage('/IncomePage'); }
    function toExpense() { navPage('/ExpenseEdit'); }
    function toInvest() { navPage('/InvestEdit'); }
    function toInvestEvent() { navPage('/InvestEvent'); }
    function toHome() { navPage('/Homepage'); }
    function toProfile() { navPage('/Profset'); }

    function simulationSetting() {
        return (
            <div className="loginBox" >
                <div>
                    <StackedBarChart />
                </div>
                <button onClick={toSim} style={{marginTop: "1vh"}}>Back to Simulation Setting</button>
        </div>
        );
    }

    return (
        <div className="total">
            <nav className="navBarTop">
                <img
                    src="/public/caffeineOverloadLogo.png"
                    className="logoSize"
                    onClick={toHome}
                    alt="Logo"
                />
                <p className="logoLetter">Life Time Financial Planner</p>
                <button onClick={toUserGuide}>User Guide</button>
            </nav>
            <nav className="navBarSub">
                <button className="commonButton" onClick={popupMenu}>
                    Menu
                </button>
                {sideElements()}
                {loggedIn && (
                    <button className="commonButton" onClick={toProfile}>
                        Scenario Setting
                    </button>
                )}
            </nav>
            {simulationSetting()}
        </div>
    );
}

export default SimulationPage;
