import React, {useEffect, useState} from 'react';
import './common.css';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

function investEventManagement(){
    const [loggedIn, setLoggedIn] = useState(false);
    const [openSide, setSide] = useState(false);
    const [viewedId, setViewedId] = useState(null);
    const [formData, setFormData] = useState([]);

    const navPage = useNavigate();

    useEffect(() => {
        const token = localStorage.getItem("token");
        if (token) {
            setLoggedIn(true);
        }
    }, []);

    useEffect(() => {
        const scenarioId = localStorage.getItem("scenario");
        if (scenarioId) {
            axios.get(`http://localhost:10000/api/invest-events/scenario/${scenarioId}`)
                .then(response => {
                    if(response.data !== undefined){
                        setFormData(response.data);
                    }
                });
        }
    }, []);

    const popupMenu = () => setSide(prev => !prev);

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
                    <button onClick={() => navPage('/ImportExp')}>Import & Export Data</button>
                </aside>
            )
        );
    }

    function handleViewClick(id) {
        setViewedId(prev => prev === id ? null : id);
    }

    function investEventList() {
        return (
            <div className="profileSetting">
                <div style={{ display: 'flex', justifyContent: 'space-between', margin: '10px' }}>
                    <p className="logoLetter" style={{ color: 'black', fontSize: '5vh', marginTop: '30px', marginRight: '50px' }}>Investment Event</p>
                    <button onClick={() => navPage('/InvestEvent')} className="addButton">Add Invest Event</button>
                </div>
                {formData.map((item, index) => {
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
                    return (
                        <form key={item.eventSeriesId || index} className="investment-form">
                            <div className="login">
                                <label htmlFor={`name-${index}`}>Name:</label>
                                <button
                                    type="button"
                                    id={`name-${index}`}
                                >
                                    {item.name}
                                </button>
                                <button
                                    type="button"
                                    onClick={() => handleViewClick(item.eventSeriesId)}
                                    style={{ backgroundColor: 'black', color: 'white' }}
                                >
                                    {viewedId === item.eventSeriesId ? 'Hide Details' : 'View Invest Event Detail'}
                                </button>
                                {viewedId === item.eventSeriesId && (
                                    <div className="investment-details">
                                        <p><strong style={{color: "darkcyan"}}>Start Year:</strong> {syDisplay}</p>
                                        <p><strong style={{color: "darkcyan"}}>Duration:</strong> {duDisplay}</p>
                                        <p><strong style={{color: "darkcyan"}}>Max Cash:</strong> {item.maxCash}</p>
                                        <p style={{color: "darkcyan"}}><strong>Asset Allocations:</strong></p>
                                        <div>
                                            {item.assetAllocations.map((alloc, i) => (
                                                <p key={i} style={{fontSize:"x-small"}}>
                                                    {alloc.investmentKey}: {(alloc.ratio * 100).toFixed(2)} %
                                                </p>
                                            ))}
                                        </div>
                                        <button
                                            type="button"
                                            style={{ backgroundColor: 'black', color: 'white', marginTop: '8px' }}
                                            onClick={() => navPage(`/invest-events/edit/${item.eventSeriesId}`)}
                                        >
                                            Edit Investment
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
                    <button className="commonButton" onClick={() => navPage('/Profset')}>Scenario Setting</button>
                )}
            </nav>
            {investEventList()}
        </div>
    );
}

export default investEventManagement;
