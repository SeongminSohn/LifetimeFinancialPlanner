import React, { useState, useEffect } from 'react';
import './common.css';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

function expenseManage() {
    const [loggedIn, setLoggedIn] = useState(false);
    const [openSide, setSide] = useState(false);
    const [viewedId, setViewedId] = useState(null);
    const [events, setEvents] = useState([]);
    const navPage = useNavigate();

    useEffect(() => {
        const token = localStorage.getItem("token");
        if (token) setLoggedIn(true);
    }, []);

    useEffect(() => {
        const scenarioId = localStorage.getItem("scenario");
        if (scenarioId) {
            axios.get(`http://localhost:10000/api/expense-events/scenario/${scenarioId}`)
                .then(res => res.data && setEvents(res.data));
        }
    }, []);

    // useEffect(() => {
    //     const scenarioId = localStorage.getItem("scenario");
    //     if (scenarioId) {
    //         axios.get(`http://localhost:10000/api/expense-events/4`)
    //             .then(res => res.data && console.log("The Data: ",res.data));
    //     }
    // }, []);

    const popupMenu = () => setSide(prev => !prev);

    function sideElements() {
        return openSide && (
            <aside className="sidebar">
                <button onClick={() => navPage('/IncomeSetting')}>View Income Status</button>
                <button onClick={() => navPage('/ExpenseSetting')}>view Expense Status</button>
                <button onClick={() => navPage('/ExpenseW')}>Expense Withdrawal Edit</button>
                <button onClick={() => navPage('/SimulationManagement')}>Invest Event Edit</button>
                <button onClick={() => navPage('/simulationPage')}>Scenario Simulation</button>
                <button>Import & Export Data</button>
            </aside>
        );
    }

    function handleViewClick(id) {
        setViewedId(prev => (prev === id ? null : id));
    }

    function incomeEventList() {
        return (
            <div className="profileSetting">
                <div style={{ display: 'flex', justifyContent: 'space-between', margin: '10px' }}>
                    <p className="logoLetter" style={{ color: 'black', fontSize: '5vh', marginTop: '30px', marginRight: '50px' }}>
                        Expense Events
                    </p>
                    <button onClick={() => navPage('/ExpenseEdit')} className="addButton">
                        Add Expense Event
                    </button>
                </div>
                {events.map((item, index) => {
                    const sy = item.startYear;
                    const syDisplay =
                        sy.distributionType === 'FIXED' ? sy.value :
                            sy.distributionType === 'UNIFORM' ? `${sy.lower} - ${sy.upper}` :
                                sy.distributionType === 'NORMAL' ? `${sy.mean} ± ${sy.stDev}` :
                                    '';
                    const du = item.duration;
                    const duDisplay =
                        du.distributionType === 'FIXED' ? du.value :
                            du.distributionType === 'UNIFORM' ? `${du.lower} - ${du.upper}` :
                                du.distributionType === 'NORMAL' ? `${du.mean} ± ${du.stDev}` :
                                    '';
                    const ac = item.annualChange;
                    const acDisplay =
                        ac.distributionType === 'FIXED' ? ac.value :
                            ac.distributionType === 'UNIFORM' ? `${ac.lower} - ${ac.upper}` :
                                ac.distributionType === 'NORMAL' ? `${ac.mean} ± ${ac.stDev}` :
                                    '';
                    return (
                        <form key={item.eventSeriesId || index} className="investment-form">
                            <div className="login">
                                <label htmlFor={`name-${index}`}>Name:</label>
                                <button type="button" id={`name-${index}`}>
                                    {item.name}
                                </button>
                                <button
                                    type="button"
                                    onClick={() => handleViewClick(item.eventSeriesId)}
                                    style={{ backgroundColor: 'black', color: 'white' }}
                                >
                                    {viewedId === item.eventSeriesId ? 'Hide Details' : 'View Expense Event Detail'}
                                </button>
                                {viewedId === item.eventSeriesId && (
                                    <div className="investment-details">
                                        <p><strong style={{color:"darkcyan"}}>Start Year:</strong> {syDisplay}</p>
                                        <p><strong style={{color:"darkcyan"}}>Duration:</strong> {duDisplay}</p>
                                        <p><strong style={{color:"darkcyan"}}>Initial Amount:</strong> {item.initialAmount}</p>
                                        <p><strong style={{color:"darkcyan"}}>Annual Change:</strong> {acDisplay}</p>
                                        <p><strong style={{color:"darkcyan"}}>Inflation Adjustment:</strong> {item.inflationAdjustment}</p>
                                        <p><strong style={{color:"darkcyan"}}>User Percentage:</strong> {item.userPercentage}</p>
                                        <p><strong style={{color:"darkcyan"}}>Discretionary:</strong> {item.isDiscretionary}</p>
                                        <button
                                            type="button"
                                            style={{ backgroundColor: 'black', color: 'white', marginTop: '8px' }}
                                            onClick={() => navPage(`/expense-events/edit/${item.eventSeriesId}`)}
                                        >
                                            Edit Expense Event
                                        </button>
                                    </div>
                                )}
                            </div>
                        </form>
                    );
                })}
            </div>
        );
    }

    return (
        <div className="total">
            <nav className="navBarTop">
                <img onClick={() => navPage('/Homepage')} src="/public/caffeineOverloadLogo.png" alt="logo" className="logoSize" />
                <p className="logoLetter">Life Time Financial Planner</p>
                <button onClick={() => navPage('/UserGuide')}>User Guide</button>
            </nav>
            <nav className="navBarSub">
                <button className="commonButton" onClick={popupMenu}>Menu</button>
                {sideElements()}
                {loggedIn && (
                    <button className="commonButton" onClick={() => navPage('/Profset')}>
                        Scenario Setting
                    </button>
                )}
            </nav>
            {incomeEventList()}
        </div>
    );
}
export default expenseManage;
