// src/pages/SimulationPage.jsx
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import StackedBarChart from '../component/StackedBarChart.jsx';

function SimulationPage() {
    const [formData, setFormData] = useState({
        assetAllocations: []
    });
    const [allocationValues, setAllocationValues] = useState({});
    const [simulationResult, setSimulationResult] = useState([]);
    const [activeEvent, setActiveEvent] = useState(null);
    const [loggedIn, setLoggedIn] = useState(false);
    const [openSide, setSide] = useState(false);

    const navPage = useNavigate();

    useEffect(() => {
        const token = localStorage.getItem('token');
        if (token) setLoggedIn(true);
    }, []);

    // get investEvent data and simulation data at once by simulation id
    useEffect(() => {
        const scenarioId = localStorage.getItem('scenario');
        if (!scenarioId) return;

        axios.get(`http://localhost:10000/api/invest-events/scenario/${scenarioId}`).then(res => {
                if (res.data[0]) setFormData(res.data[0]);
            })
            .catch(err => console.error(err));

        axios
            .get(`http://localhost:10000/api/charts/scenarios/${scenarioId}/simulations`)
            .then(res => {
                if (Array.isArray(res.data)) setSimulationResult(res.data);
            })
            .catch(err => console.error(err));
    }, []);

    // mapping the asset allocation Data to multiply each data
    useEffect(() => {
        const map = {};
        (formData.assetAllocations || []).forEach(a => {
            map[a.investmentKey] = a.ratio;
        });
        setAllocationValues(map);
    }, [formData.assetAllocations]);

    const popupMenu = () => setSide(s => !s);
    const sideElements = () =>
        openSide && (
            <aside className="sidebar">
                <button onClick={() => navPage('/IncomePage')}>Income Edit</button>
                <button onClick={() => navPage('/ExpenseEdit')}>Expense Edit</button>
                <button onClick={() => navPage('/InvestEdit')}>Invest Edit</button>
                <button onClick={() => navPage('/ExpenseW')}>Withdrawal Edit</button>
                <button onClick={() => navPage('/InvestEvent')}>Invest Event Edit</button>
                <button onClick={toSim}>Scenario Simulation</button>
                <button disabled>Import & Export Data</button>
            </aside>
        );

    function toSim() {
        navPage('/simulationPage');
    }

    const simulationSetting = () => (
        <div className="loginBox">
            <div style={{ marginBottom: '1rem' }}>
                {simulationResult.map((item, idx) => (
                    <button
                        key={item.id}
                        onClick={() =>
                            setActiveEvent(prev => prev && prev.id === item.id ? null : item)}>
                        Simulation {idx + 1}
                    </button>))}
            </div>

            {activeEvent && (
                <div style={{ width: '100%', height: '400px' }}>
                    <StackedBarChart
                        simulationId={activeEvent.id}
                        allocations={allocationValues}
                    />
                </div>
            )}

            {activeEvent && (<button onClick={() => setActiveEvent(null)} style={{ marginTop: '1vh' }}>{activeEvent && "Hide chart"}</button>)}
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
                    <button
                        className="commonButton"
                        onClick={() => navPage('/Profset')}
                    >
                        Scenario Setting
                    </button>
                )}
            </nav>
            {simulationSetting()}
        </div>
    );
}

export default SimulationPage;
