import React, { useState, useEffect } from 'react';
import './common.css';
import axios from 'axios';
import { useNavigate, useParams } from 'react-router-dom';

function ExpensePage() {
    const { id } = useParams();
    const currentYear = new Date().getFullYear();
    const [loggedIn, setLoggedIn] = useState(false);
    const [openSide, setSide] = useState(false);
    const [formData, setFormData] = useState({
        scenarioId: '',
        name: '',
        startYear: {
            amountOrPercent: "AMOUNT",
            distributionType: "FIXED",
            value: null,
            lower: null,
            upper: null,
            mean: null,
            stDev: null
        },
        duration: {
            amountOrPercent: "AMOUNT",
            distributionType: "FIXED",
            value: null,
            lower: null,
            upper: null,
            mean: null,
            stDev: null
        },
        eventType: 'EXPENSE',
        initialAmount: '',
        annualChange: {
            amountOrPercent: "AMOUNT",
            distributionType: "FIXED",
            value: null,
            lower: null,
            upper: null,
            mean: null,
            stDev: null
        },
        inflationAdjustment: 'Y',
        userPercentage: '',
        isDiscretionary: 'Y'
    });
    const navPage = useNavigate();

    useEffect(() => {
        const token = localStorage.getItem("token");
        if (token) setLoggedIn(true);
    }, []);

    useEffect(() => {
        if (!id) return;
        axios.get(`http://localhost:10000/api/expense-events/${id}`)
            .then(res => res.data && setFormData(res.data))
            .catch(() => {});
        console.log("The form Data and id: ",formData, id)
    }, [id]);

    const popupMenu = () => setSide(prev => !prev);

    function sideElements() {
        return openSide && (
            <aside className="sidebar">
                <button onClick={() => navPage('/IncomePage')}>Income Edit</button>
                <button onClick={() => navPage('/Investment')}>Investment</button>
                <button onClick={() => navPage('/ExpenseW')}>Expense Withdrawal Edit</button>
                <button onClick={() => navPage('/InvestEvent')}>Invest Event Edit</button>
                <button onClick={() => navPage('/simulationPage')}>Scenario Simulation</button>
                <button disabled>Import & Export Data</button>
            </aside>
        );
    }

    function handleChange(e) {
        const { name, value } = e.target;
        if (name.includes('.')) {
            const [parent, child] = name.split('.');
            setFormData(prev => ({
                ...prev,
                [parent]: {
                    ...prev[parent],
                    [child]: value
                }
            }));
        } else {
            setFormData(prev => ({ ...prev, [name]: value }));
        }
    }

    async function handleSubmit(e) {
        e.preventDefault();
        setFormData(prev => ({ ...prev, scenarioId: localStorage.getItem("scenario") }));
        await axios.put(
            `http://localhost:10000/api/expense-events/${id}`,
            formData,
            { withCredentials: true, headers: { "Content-Type": "application/json" } }
        );
        alert("Updated");
    }

    function chooseMone() {
        return (
            <div>
                {formData.startYear.distributionType === "FIXED" && (
                    <input
                        type="number"
                        name="startYear.value"
                        placeholder="Current Year"
                        min={currentYear}
                        value={formData.startYear.value || ""}
                        onChange={handleChange}
                        required
                    />
                )}
                {formData.startYear.distributionType === "UNIFORM" && (
                    <>
                        <input
                            type="number"
                            name="startYear.lower"
                            placeholder="Lower"
                            value={formData.startYear.lower || ""}
                            onChange={handleChange}
                            required
                        />
                        <input
                            type="number"
                            name="startYear.upper"
                            placeholder="Upper"
                            min={formData.startYear.lower ?? currentYear}
                            value={formData.startYear.upper || ""}
                            onChange={handleChange}
                            required
                        />
                    </>
                )}
                {formData.startYear.distributionType === "NORMAL" && (
                    <>
                        <input
                            type="number"
                            name="startYear.mean"
                            placeholder="mean"
                            value={formData.startYear.mean || ""}
                            onChange={handleChange}
                            required
                        />
                        <input
                            type="number"
                            name="startYear.stDev"
                            placeholder="standard deviation"
                            value={formData.startYear.stDev || ""}
                            onChange={handleChange}
                            required
                        />
                    </>
                )}
            </div>
        );
    }

    function chooseKone() {
        return (
            <div>
                {formData.duration.distributionType === "FIXED" && (
                    <input
                        type="number"
                        name="duration.value"
                        placeholder="value"
                        value={formData.duration.value || ""}
                        onChange={handleChange}
                        required
                    />
                )}
                {formData.duration.distributionType === "UNIFORM" && (
                    <>
                        <input
                            type="number"
                            name="duration.lower"
                            placeholder="Lower"
                            value={formData.duration.lower || ""}
                            onChange={handleChange}
                            required
                        />
                        <input
                            type="number"
                            name="duration.upper"
                            placeholder="Upper"
                            value={formData.duration.upper || ""}
                            onChange={handleChange}
                            required
                        />
                    </>
                )}
                {formData.duration.distributionType === "NORMAL" && (
                    <>
                        <input
                            type="number"
                            name="duration.mean"
                            placeholder="mean"
                            value={formData.duration.mean || ""}
                            onChange={handleChange}
                            required
                        />
                        <input
                            type="number"
                            name="duration.stDev"
                            placeholder="standard deviation"
                            value={formData.duration.stDev || ""}
                            onChange={handleChange}
                            required
                        />
                    </>
                )}
            </div>
        );
    }

    function chooseLone() {
        return (
            <div>
                {formData.annualChange.distributionType === "FIXED" && (
                    <input
                        type="number"
                        name="annualChange.value"
                        placeholder="value"
                        value={formData.annualChange.value || ""}
                        onChange={handleChange}
                        required
                    />
                )}
                {formData.annualChange.distributionType === "UNIFORM" && (
                    <>
                        <input
                            type="number"
                            name="annualChange.lower"
                            placeholder="Lower"
                            value={formData.annualChange.lower || ""}
                            onChange={handleChange}
                            required
                        />
                        <input
                            type="number"
                            name="annualChange.upper"
                            placeholder="Upper"
                            value={formData.annualChange.upper || ""}
                            onChange={handleChange}
                            required
                        />
                    </>
                )}
                {formData.annualChange.distributionType === "NORMAL" && (
                    <>
                        <input
                            type="number"
                            name="annualChange.mean"
                            placeholder="mean"
                            value={formData.annualChange.mean || ""}
                            onChange={handleChange}
                            required
                        />
                        <input
                            type="number"
                            name="annualChange.stDev"
                            placeholder="standard deviation"
                            value={formData.annualChange.stDev || ""}
                            onChange={handleChange}
                            required
                        />
                    </>
                )}
            </div>
        );
    }

    function expenseManagement() {
        return (
            <form onSubmit={handleSubmit} className="profileSetting">
                <p className="logoLetter" style={{ color: 'black', fontSize: '5vh', marginTop: "30px" }}>
                    Edit Expense Information
                </p>
                <div className="login">
                    <label htmlFor="name">Name </label>
                    <input
                        type="text"
                        id="name"
                        name="name"
                        value={formData.name}
                        onChange={handleChange}
                        required
                    />
                </div>
                <div className="login">
                    <label>Start Year </label>
                    <select
                        name="startYear.distributionType"
                        value={formData.startYear.distributionType}
                        onChange={handleChange}
                        required
                    >
                        <option value="FIXED">FIXED</option>
                        <option value="UNIFORM">UNIFORM</option>
                        <option value="NORMAL">NORMAL</option>
                    </select>
                </div>
                {chooseMone()}
                <div className="login">
                    <label>Duration </label>
                    <select
                        name="duration.distributionType"
                        value={formData.duration.distributionType}
                        onChange={handleChange}
                        required
                    >
                        <option value="FIXED">FIXED</option>
                        <option value="UNIFORM">UNIFORM</option>
                        <option value="NORMAL">NORMAL</option>
                    </select>
                </div>
                {chooseKone()}
                <div className="login">
                    <label htmlFor="initialAmount">Initial Amount </label>
                    <input
                        type="number"
                        id="initialAmount"
                        name="initialAmount"
                        value={formData.initialAmount}
                        onChange={handleChange}
                        required
                    />
                </div>
                <div className="login">
                    <label>Annual Change </label>
                    <select
                        name="annualChange.distributionType"
                        value={formData.annualChange.distributionType}
                        onChange={handleChange}
                        required
                    >
                        <option value="FIXED">FIXED</option>
                        <option value="UNIFORM">UNIFORM</option>
                        <option value="NORMAL">NORMAL</option>
                    </select>
                </div>
                {chooseLone()}
                <div className="login">
                    <label htmlFor="inflationAdjustment">InflationAdjustment </label>
                    <select
                        name="inflationAdjustment"
                        value={formData.inflationAdjustment}
                        onChange={handleChange}
                        required
                    >
                        <option value="Y">Yes</option>
                        <option value="N">No</option>
                    </select>
                </div>
                <div className="login">
                    <label htmlFor="userPercentage">User Percentage </label>
                    <input
                        type="number"
                        id="userPercentage"
                        name="userPercentage"
                        value={formData.userPercentage}
                        onChange={handleChange}
                        required
                    />
                </div>
                <div className="login">
                    <label htmlFor="isDiscretionary">Discretionary </label>
                    <select
                        name="isDiscretionary"
                        value={formData.isDiscretionary}
                        onChange={handleChange}
                        required
                    >
                        <option value="Y">Yes</option>
                        <option value="N">No</option>
                    </select>
                </div>
                <button className="submitButton" type="submit">Save Changes</button>
            </form>
        );
    }

    return (
        <div className="total">
            <nav className="navBarTop">
                <img onClick={() => navPage('/Homepage')} src="/public/caffeineOverloadLogo.png" className="logoSize" alt="logo" />
                <p className="logoLetter">Life Time Financial Planner</p>
                <div></div>
            </nav>
            <nav className="navBarSub">
                <button className="commonButton" onClick={popupMenu}>Menu</button>
                {sideElements()}
                {loggedIn && (
                    <button className="commonButton" onClick={() => navPage('/Profset')}>
                        Profile Setting
                    </button>
                )}
            </nav>
            {expenseManagement()}
        </div>
    );
}

export default ExpensePage;
