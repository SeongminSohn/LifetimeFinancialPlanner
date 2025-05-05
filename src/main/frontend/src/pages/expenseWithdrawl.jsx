import React, { useState, useEffect, useCallback } from 'react';
import './common.css';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

function ExpenseWithdrawlPage() {
    const [investmentTypes, setInvestmentTypes] = useState([]);
    const [investments, setInvestments] = useState([]);
    const [clickedItems, setClickedItems] = useState([]);
    const [formData, setFormData] = useState({ id: undefined, scenarioId: '', sellingOrder: [] });
    const [loggedIn, setLoggedIn] = useState(false);
    const [openSide, setOpenSide] = useState(false);
    const nav = useNavigate();
    const scenarioId = localStorage.getItem('scenario');

    const fetchAll = useCallback(async () => {
        if (!scenarioId) return;
        try {
            const [invRes, typeRes, stratRes] = await Promise.all([
                axios.get(`http://localhost:10000/api/investments/scenario/${scenarioId}`),
                axios.get(`http://localhost:10000/api/investment-types/scenario/${scenarioId}`),
                axios.get(`http://localhost:10000/api/expense-withdrawal-strategies/scenario/${scenarioId}`)
            ]);
            setInvestments(invRes.data);
            setInvestmentTypes(typeRes.data);
            if (stratRes.data) {
                setFormData(stratRes.data);
                setClickedItems(Array.isArray(stratRes.data.sellingOrder) ? stratRes.data.sellingOrder : []);
            }
        } catch (e) {
            console.error(e);
        }
    }, [scenarioId]);

    useEffect(() => {
        if (localStorage.getItem('token')) setLoggedIn(true);
        fetchAll();
    }, [fetchAll]);

    const toggleClickedItem = inv => {
        const type = investmentTypes.find(t => t.id === inv.investmentTypeId);
        if (!type) return;
        const label = `${type.name} ${inv.taxStatus}`;
        setClickedItems(prev =>
            prev.includes(label) ? prev.filter(l => l !== label) : [...prev, label]
        );
    };

    const saveStrategy = async () => {
        const payload = { ...formData, scenarioId, sellingOrder: clickedItems };
        try {
            if (payload.id) {
                await axios.put(
                    `http://localhost:10000/api/expense-withdrawal-strategies/${payload.id}`,
                    payload,
                    { withCredentials: true, headers: { 'Content-Type': 'application/json' } }
                );
                alert("updated!")
            } else {
                const { data } = await axios.post(
                    'http://localhost:10000/api/expense-withdrawal-strategies',
                    payload,
                    { withCredentials: true, headers: { 'Content-Type': 'application/json' } }
                );
                alert("Saved!")
                setFormData(data);
            }
        } catch (e) {
            console.error(e);
        }
    };

    const displayInvestments = investments.filter(inv => {
        const type = investmentTypes.find(t => t.id === inv.investmentTypeId);
        return type && type.name !== 'CASH' && inv.taxStatus !== 'PRE-TAX';
    });

    return (
        <div className="total">
            <nav className="navBarTop">
                <img onClick={() => nav('/Homepage')} src="/public/caffeineOverloadLogo.png" alt="logo" className="logoSize" />
                <p className="logoLetter">Life Time Financial Planner</p>
                <div></div>
            </nav>
            <nav className="navBarSub">
                <button className="commonButton" onClick={() => setOpenSide(p => !p)}>Menu</button>
                {openSide && (
                    <aside className="sidebar">
                        <button onClick={() => nav('/Investment')}>View Invest type Status</button>
                        <button onClick={() => nav('/IncomeSetting')}>View Income Status</button>
                        <button onClick={() => nav('/ExpenseSetting')}>View Expense Status</button>
                        <button onClick={() => nav('/ExpenseW')}>Expense Withdrawal Edit</button>
                        <button onClick={() => nav('/SimulationManagement')}>Invest Event Edit</button>
                        <button onClick={() => nav('/simulationPage')}>Scenario Simulation</button>
                        <button onClick={() => nav('/ImportExp')}>Import & Export Data</button>
                    </aside>
                )}
                {loggedIn && <button className="commonButton" onClick={() => nav('/Profset')}>Scenario Setting</button>}
            </nav>

            <div className="profileSetting">
                <p className="logoLetter" style={{ color: 'black', fontSize: '5vh', marginTop: '30px' }}>
                    Expense WithDrawl. Choose Order.
                </p>

                {displayInvestments.map(inv => (
                    <form key={inv.id} className="investment-form">
                        <div className="login">
                            <label>Status</label>
                            <button type="button" onClick={() => toggleClickedItem(inv)}>
                                {investmentTypes.find(t => t.id === inv.investmentTypeId)?.name} {inv.taxStatus}
                            </button>
                        </div>
                    </form>
                ))}

                <div>
                    <button type="submit" onClick={saveStrategy}>Save</button>
                </div>

                <div className="forOrdering">
                    <p>Orders :</p>
                    {clickedItems.map((l, i) => (
                        <div key={i} className="arrays">{l}</div>
                    ))}
                </div>
            </div>
        </div>
    );
}

export default ExpenseWithdrawlPage;
