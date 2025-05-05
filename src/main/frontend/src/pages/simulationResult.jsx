import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import StackedBarChart from '../component/StackedBarChart.jsx';

function SimulationPage() {
    const navPage = useNavigate();
    const [simulationList, setSimulationList] = useState([]);
    const [batches, setBatches] = useState({});       // { batchId: [simulation, …] }
    const [activeBatchId, setActiveBatchId] = useState(null);
    const [loggedIn, setLoggedIn] = useState(false);
    const [openSide, setSide] = useState(false);

    useEffect(() => {
        const token = localStorage.getItem('token');
        if (token) setLoggedIn(true);
    }, []);

    useEffect(() => {
        const scenarioId = localStorage.getItem('scenario');
        if (!scenarioId) return;
        axios
            .get(`http://localhost:10000/api/charts/scenarios/${scenarioId}/simulations`)
            .then(res => Array.isArray(res.data) && setSimulationList(res.data)&& console.log(res.data))
            .catch(console.error);
    }, []);

    useEffect(() => {
        const grouped = simulationList.reduce((acc, sim) => {
            if (!acc[sim.batchId]) acc[sim.batchId] = [];
            acc[sim.batchId].push(sim);
            return acc;
        }, {});
        setBatches(grouped);
    }, [simulationList]);

    const toggleSide = () => setSide(s => !s);

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
                <button className="commonButton" onClick={toggleSide}>Menu</button>
                {openSide && (
                    <aside className="sidebar">
                        <button onClick={() => navPage('/Investment')}>View Invest type Status</button>
                        <button onClick={() => navPage('/IncomeSetting')}>View Income Status</button>
                        <button onClick={() => navPage('/ExpenseSetting')}>View Expense Status</button>
                        <button onClick={() => navPage('/ExpenseW')}>Expense Withdrawal Edit</button>
                        <button onClick={() => navPage('/SimulationManagement')}>Invest Event Edit</button>
                        <button onClick={() => navPage('/simulationPage')}>Scenario Simulation</button>
                        <button onClick={() => navPage('/ImportExp')}>Import & Export Data</button>
                    </aside>
                )}
                {loggedIn && (
                    <button className="commonButton" onClick={() => navPage('/Profset')}>
                        Scenario Setting
                    </button>
                )}
            </nav>

            <div className="loginBox">
                <div style={{ marginBottom: '1rem' }}>
                    {Object.entries(batches).map(([bid, arr], idx) => (
                        <button key={bid} onClick={() => setActiveBatchId(bid)}>
                            Simulation {bid} & Count:{arr.length}
                        </button>
                    ))}
                </div>

                {activeBatchId && (
                    <div style={{ width: '100%', height: '400px' }}>
                        <StackedBarChart simulations={batches[activeBatchId]} />
                    </div>
                )}

                {activeBatchId && (
                    <button onClick={() => setActiveBatchId(null)} style={{ marginTop: '1vh' }}>
                        Hide chart
                    </button>
                )}
            </div>
        </div>
    );
}

export default SimulationPage;
