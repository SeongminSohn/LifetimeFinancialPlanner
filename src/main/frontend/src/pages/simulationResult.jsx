import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import StackedBarChart from '../component/StackedBarChart.jsx';

function SimulationPage() {
    const [simulationResult, setSimulationResult] = useState([]);
    const [activeEvent, setActiveEvent] = useState(null);
    const [loggedIn, setLoggedIn] = useState(false);
    const [openSide, setSide] = useState(false);
    const navPage = useNavigate();

    useEffect(() => {
        const token = localStorage.getItem('token');
        if (token) setLoggedIn(true);
    }, []);

    useEffect(() => {
        const scenarioId = localStorage.getItem('scenario');
        if (!scenarioId) return;
        axios
            .get(`http://localhost:10000/api/charts/scenarios/${scenarioId}/simulations`)
            .then(res => {
                if (Array.isArray(res.data)) setSimulationResult(res.data);
            })
            .catch(console.error);
    }, []);

    const popupMenu = () => setSide(s => !s);

    const sideElements = () =>
        openSide && (
            <aside className="sidebar">
                <button onClick={() => navPage('/Investment')}>View Invest type Status</button>
                <button onClick={() => navPage('/IncomeSetting')}>View Income Status</button>
                <button onClick={() => navPage('/ExpenseSetting')}>View Expense Status</button>
                <button onClick={() => navPage('/ExpenseW')}>Expense Withdrawal Edit</button>
                <button onClick={() => navPage('/SimulationManagement')}>Invest Event Edit</button>
                <button onClick={() => navPage('/simulationPage')}>Scenario Simulation</button>
                <button onClick={() => navPage('/ImportExp')}>Import & Export Data</button>
            </aside>
        );

    const simulationSetting = () => (
        <div className="loginBox">
            <div style={{ marginBottom: '1rem' }}>
                {simulationResult.map((item, idx) => (
                    <button
                        key={item.id}
                        onClick={() => setActiveEvent(prev => (prev && prev.id === item.id ? null : item))}
                    >
                        Simulation {idx + 1}
                    </button>
                ))}
            </div>
            {activeEvent && (
                <div style={{ width: '100%', height: '400px' }}>
                    <StackedBarChart simulationId={activeEvent.id} />
                </div>
            )}
            {activeEvent && (
                <button onClick={() => setActiveEvent(null)} style={{ marginTop: '1vh' }}>
                    Hide chart
                </button>
            )}
        </div>
    );

    return (
        <div className="total">
            <nav className="navBarTop">
                <img
                    src="/public/caffeineOverloadLogo.png"
                    className="logoSize"
                    onClick={() => navPage('/Homepage')}
                    alt="Logo"
                />
                <p className="logoLetter">Life Time Financial Planner</p>
                <button onClick={() => navPage('/UserGuide')}>User Guide</button>
            </nav>
            <nav className="navBarSub">
                <button className="commonButton" onClick={popupMenu}>
                    Menu
                </button>
                {sideElements()}
                {loggedIn && (
                    <button className="commonButton" onClick={() => navPage('/Profset')}>
                        Scenario Setting
                    </button>
                )}
            </nav>
            {simulationSetting()}
        </div>
    );
}

export default SimulationPage;
