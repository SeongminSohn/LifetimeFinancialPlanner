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

    // 1) 로그인 상태 확인
    useEffect(() => {
        const token = localStorage.getItem('token');
        if (token) setLoggedIn(true);
    }, []);

    // 2) 시나리오 ID 기반으로 invest-event 와 simulation summary 동시에 불러오기
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

    // 3) formData.assetAllocations → { investmentKey: ratio } 매핑
    useEffect(() => {
        const map = {};
        (formData.assetAllocations || []).forEach(a => {
            map[a.investmentKey] = a.ratio;
        });
        setAllocationValues(map);
    }, [formData.assetAllocations]);

    // 사이드 메뉴
    const popupMenu = () => setSide(s => !s);
    const sideElements = () =>
        openSide && (
            <aside className="sidebar">
                <button onClick={() => navPage('/IncomePage')}>Income Edit</button>
                <button onClick={() => navPage('/ExpenseEdit')}>Expense Edit</button>
                <button onClick={() => navPage('/InvestEdit')}>Invest Edit</button>
                <button onClick={() => navPage('/ExpenseW')}>Withdrawal Edit</button>
                <button onClick={() => navPage('/InvestEvent')}>Invest Event Edit</button>
                <button disabled>Import & Export Data</button>
            </aside>
        );

    // 차트 토글 & 렌더링
    const simulationSetting = () => (
        <div className="loginBox">
            <div style={{ marginBottom: '1rem' }}>
                {simulationResult.map((item, idx) => (
                    <button
                        key={item.id}
                        onClick={() =>
                            setActiveEvent(prev =>
                                prev && prev.id === item.id ? null : item
                            )
                        }>
                        Simulation {idx + 1}
                        {/*({new Date(item.createdAt).toLocaleDateString()},{' '}*/}
                        {/*{new Date(item.createdAt).toLocaleTimeString()})*/}
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

            <button
                onClick={() => setActiveEvent(null)}
                style={{ marginTop: '1vh' }}
            >
                {activeEvent ? 'Hide Chart' : 'Back to Simulation Setting'}
            </button>
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
