import React, { useState, useEffect } from 'react';
import './common.css';
import axios from 'axios';
import { useNavigate, useParams } from 'react-router-dom';

function InvestTypeManage() {
    const { id } = useParams();
    const navPage = useNavigate();

    const [investmentTypes, setInvestmentTypes] = useState([]);
    const [loggedIn, setLoggedIn] = useState(false);
    const [openSide, setSide] = useState(false);
    const [investments, setInvestments] = useState([]);
    const [showAdd, setShowAdd] = useState(false);
    const [newInvestment, setNewInvestment] = useState({
        investmentTypeId: '',
        value: '',
        taxStatus: 'NON-RETIREMENT',
    });

    function toHome() {
        navPage('/Homepage');
    }

    function toProfile() {
        navPage('/Profset');
    }

    useEffect(() => {
        const token = localStorage.getItem('token');
        if (token) setLoggedIn(true);
    }, []);

    useEffect(() => {
        const scenarioId = localStorage.getItem("scenario");
        if (scenarioId) {
            axios.get(`http://localhost:10000/api/investment-types/scenario/${scenarioId}`)
                .then(response => {
                    console.log("Investment Types:", response.data);
                    setInvestmentTypes(response.data);
                })
                .catch(error => {
                    console.error("Error fetching investment types:", error);
                });
        }
    }, []);

    useEffect(() => {
        axios
            .get(`http://localhost:10000/api/investments/type/${id}`)
            .then((res) => {
                const data = Array.isArray(res.data) ? res.data : [res.data];
                setInvestments(data);
                console.log(data)
            })
            .catch(console.error);
    }, [id]);

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

    function popupMenu() {
        setSide((prev) => !prev);
    }

    async function handleAdd(e) {
        e.preventDefault();
        const scenarioId = localStorage.getItem('scenario');
        const payload = { ...newInvestment, scenarioId, investmentTypeId: id };
        console.log("This is Payload", payload)
        try {
            await axios.post('http://localhost:10000/api/investments', payload, {
                withCredentials: true,
                headers: { 'Content-Type': 'application/json' },
            });
            alert("created!")
            setInvestments((prev) => [...prev, payload]);
            setNewInvestment({ id: '', investmentTypeId: '', value: '', taxStatus: 'NON-RETIREMENT' });
            setShowAdd(false);
        } catch (e) {
            console.error(e);
        }
    }

    async function handleUpdate(index) {
        const inv = investments[index];
        await axios.put(`http://localhost:10000/api/investments/${inv.id}`, inv, {
            withCredentials: true,
            headers: { 'Content-Type': 'application/json' },
        });
        alert("Updated!");
    }

    async function handleDelete(invId) {
        await axios.delete(`http://localhost:10000/api/investments/${invId}`, {
            withCredentials: true,
        });
        setInvestments((prev) => prev.filter((i) => i.id !== invId));
    }

    function renderInvestments() {
        if (!investments.length) return <p style={{ padding: 20 }}>No investment data found.</p>;
        return (
            <div className="profileSetting">
                <p className="logoLetter" style={{ color: 'black', fontSize: '5vh', marginTop: 30, marginBottom: 10 }}>
                    Investment List
                </p>
                {investments.map((item, idx) => (
                    <form key={item.id || idx} className="investment-form" onSubmit={(e) => e.preventDefault()}>
                        <div className="login">
                            <label htmlFor={`value-${idx}`}>Value: </label>
                            <input
                                type="number"
                                id={`value-${idx}`}
                                value={item.value}
                                onChange={(e) =>
                                    setInvestments((prev) =>
                                        prev.map((v, i) => (i === idx ? { ...v, value: e.target.value } : v))
                                    )
                                }
                                required
                            />
                            <hr style={{border:"none"}}/>
                            <label htmlFor={`tax-${idx}`}>Tax Status</label>
                            <select
                                id={`tax-${idx}`}
                                value={item.taxStatus}
                                onChange={(e) =>
                                    setInvestments((prev) =>
                                        prev.map((v, i) => (i === idx ? { ...v, taxStatus: e.target.value } : v))
                                    )
                                }
                                required
                            >
                                <option value="NON-RETIREMENT">NON-RETIREMENT</option>
                                <option value="PRE-TAX">PRE-TAX</option>
                                <option value="AFTER-TAX">AFTER-TAX</option>
                            </select>
                        </div>
                        <button type="submit" onClick={() => handleUpdate(idx)}>
                            Edit
                        </button>
                        <button type="submit" onClick={() => handleDelete(item.id)}>
                            Delete
                        </button>
                    </form>
                ))}
            </div>
        );
    }

    return (
        <div className="total">
            <nav className="navBarTop">
                <img onClick={toHome} src="/public/caffeineOverloadLogo.png" alt="logo" className="logoSize" />
                <p className="logoLetter">Life Time Financial Planner</p>
                <div />
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

            <div className="profileSetting">
            </div>
            {renderInvestments()}
            {!showAdd && (
                <button style = {{backgroundColor:"black", color: "white"}} onClick={() => setShowAdd(true)}>
                    Add Investment
                </button>
            )}
            {showAdd && (
                <form onSubmit={handleAdd} style={{ marginBottom: 20 }}>
                    <hr style = {{borderRadius: "20px"}}/>
                    <div className="login">
                        {/*<label htmlFor="invType">Investment Type ID</label>*/}
                        {/*<input*/}
                        {/*    style = {{width: "100px"}}*/}
                        {/*    id="invType"*/}
                        {/*    value={newInvestment.id}*/}
                        {/*    onChange={(e) => setNewInvestment({ ...newInvestment, investmentTypeId: e.target.value })}*/}
                        {/*    required*/}
                        {/*/>*/}

                        <label htmlFor="invValue">Value: </label>
                        <input
                            type="number"
                            id="invValue"
                            value={newInvestment.value}
                            onChange={(e) => setNewInvestment({ ...newInvestment, value: e.target.value })}
                            required/>
                        <hr style={{border:"none"}}/>
                        <label htmlFor="invTax">Tax Status</label>
                        <select
                            id="invTax"
                            value={newInvestment.taxStatus}
                            onChange={(e) => setNewInvestment({ ...newInvestment, taxStatus: e.target.value })}
                            required>
                            <option value="NON-RETIREMENT">NON-RETIREMENT</option>
                            <option value="PRE-TAX">PRE-TAX</option>
                            <option value="AFTER-TAX">AFTER-TAX</option>
                        </select>
                    </div>
                    <button type="submit">Save</button>
                    <button type="button" onClick={() => setShowAdd(false)}>
                        Cancel
                    </button>
                </form>
            )}
            <button onClick={() => navPage('/Investment')}>Back</button>
        </div>
    );
}

export default InvestTypeManage;
