import React, { useState, useEffect } from 'react';
import './common.css';
import axios from 'axios';
import { useNavigate, useParams } from 'react-router-dom';

function InvestEventPage() {
    const currentYear = new Date().getFullYear();
    const { id } = useParams();
    const [investmentTypes, setInvestmentTypes] = useState([]);
    const [existingInvestments, setExistingInvestments] = useState([]);
    const [selectedInvestment, setSelectedInvestment] = useState(null);
    const [loggedIn, setLoggedIn] = useState(false);
    const [openSide, setSide] = useState(false);
    const [allocationValues, setAllocationValues] = useState({});
    const [formData, setFormData] = useState({
        scenarioId: "",
        eventSeriesId: "",
        name: "",
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
        eventType: "INVESTMENT",
        assetAllocations: [],
        maxCash: ""
    });
    const navPage = useNavigate();

    useEffect(() => {
        const token = localStorage.getItem("token");
        if (token) {
            setLoggedIn(true);
        }
    }, []);

    useEffect(() => {
        if (!id) return;
        axios.get(`http://localhost:10000/api/invest-events/${id}`)
            .then(response => {
                if (response.data) {
                    setFormData(response.data);
                }
            })
            .catch(() => {});
    }, [id]);

    useEffect(() => {
        const scenarioId = localStorage.getItem("scenario");
        if (scenarioId) {
            axios.get(`http://localhost:10000/api/investment-types/scenario/${scenarioId}`)
                .then(response => {
                    setInvestmentTypes(response.data);
                })
                .catch(() => {});
        }
    }, []);

    useEffect(() => {
        const scenarioId = localStorage.getItem("scenario");
        if (scenarioId) {
            axios.get(`http://localhost:10000/api/investments/scenario/${scenarioId}`)
                .then(response => {
                    setExistingInvestments(response.data);
                })
                .catch(() => {});
        }
    }, []);

    useEffect(() => {
        if (formData.assetAllocations && Array.isArray(formData.assetAllocations)) {
            const newAllocationValues = {};
            formData.assetAllocations.forEach(allocation => {
                newAllocationValues[allocation.investmentKey] = allocation.ratio;
            });
            setAllocationValues(newAllocationValues);
        }
    }, [formData.assetAllocations]);

    const popupMenu = () => setSide(prev => !prev);

    function sideElements() {
        return openSide && (
            <aside className="sidebar">
                <button onClick={() => navPage('/Investment')}>Investment</button>
                <button onClick={() => navPage('/IncomePage')}>Income Edit</button>
                <button onClick={() => navPage('/ExpenseEdit')}>Expense Edit</button>
                <button onClick={() => navPage('/ExpenseW')}>Expense Withdrawal Edit</button>
                <button onClick={() => navPage('/InvestEvent')}>Invest Event Edit</button>
                <button onClick={() => navPage('/simulationPage')}>Scenario Simulation</button>

                <button>Import & Export Data</button>
            </aside>
        );
    }

    const handleChange = (e) => {
        const { name, value } = e.target;

        if (name === 'startYear.value') {
            const raw = parseInt(value, 10);
            const clamped = isNaN(raw) ? currentYear : Math.max(currentYear, raw);
            setFormData(prev => ({
                ...prev,
                startYear: { ...prev.startYear, value: clamped }
            }));
            return;
        }
        if (name === 'startYear.lower') {
            const raw = parseInt(value, 10);
            const clamped = isNaN(raw) ? currentYear : Math.max(currentYear, raw);
            setFormData(prev => ({
                ...prev,
                startYear: { ...prev.startYear, lower: clamped }
            }));
            return;
        }
        if (name === 'startYear.upper') {
            const raw = parseInt(value, 10);
            const minUpper = formData.startYear.lower ?? currentYear;
            const clamped = isNaN(raw) ? '' : Math.max(raw, minUpper);
            setFormData(prev => ({
                ...prev,
                startYear: { ...prev.startYear, upper: clamped }
            }));
            return;
        }

        if (name === "startYear.distributionType") {
            setFormData(prev => ({
                ...prev,
                startYear: {
                    ...prev.startYear,
                    distributionType: value,
                    value: null,
                    lower: null,
                    upper: null,
                    mean: null,
                    stDev: null
                }
            }));
            return;
        }

        if (name === "duration.distributionType") {
            setFormData(prev => ({
                ...prev,
                duration: {
                    ...prev.duration,
                    distributionType: value,
                    value: null,
                    lower: null,
                    upper: null,
                    mean: null,
                    stDev: null
                }
            }));
            return;
        }

        if (name === "annualChange.distributionType") {
            setFormData(prev => ({
                ...prev,
                annualChange: {
                    ...prev.annualChange,
                    distributionType: value,
                    value: null,
                    lower: null,
                    upper: null,
                    mean: null,
                    stDev: null
                }
            }));
            return;
        }
        if (name.includes('.')) {
            const [parentKey, childKey] = name.split('.');
            setFormData(prev => ({
                ...prev,
                [parentKey]: {
                    ...prev[parentKey],
                    [childKey]: value
                }
            }));
        } else {
            setFormData(prev => ({
                ...prev,
                [name]: value
            }));
        }
    };

    function chooseMone() {
        return (
            <div>
                {formData.startYear.distributionType === "FIXED" && (
                    <input
                        type="number"
                        name="startYear.value"
                        id="startYear.FIXED"
                        placeholder="value"
                        value={formData.startYear.value || ""}
                        onChange={handleChange}
                        required
                    />
                )}
                {formData.startYear.distributionType === "UNIFORM" && (
                    <div>
                        <input
                            type="number"
                            name="startYear.lower"
                            id="startYear.LOWER"
                            placeholder="Lower"
                            value={formData.startYear.lower || ""}
                            onChange={handleChange}
                            required
                        />
                        <input
                            type="number"
                            name="startYear.upper"
                            id="startYear.UPPER"
                            placeholder="Upper"
                            value={formData.startYear.upper || ""}
                            onChange={handleChange}
                            required
                        />
                    </div>
                )}
                {formData.startYear.distributionType === "NORMAL" && (
                    <div>
                        <input
                            type="number"
                            name="startYear.mean"
                            id="startYear.MEAN"
                            placeholder="mean"
                            value={formData.startYear.mean || ""}
                            onChange={handleChange}
                            required
                        />
                        <input
                            type="number"
                            name="startYear.stDev"
                            id="startYear.STDEV"
                            placeholder="standard deviation"
                            value={formData.startYear.stDev || ""}
                            onChange={handleChange}
                            required
                        />
                    </div>
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
                        id="duration.FIXED"
                        placeholder="value"
                        value={formData.duration.value || ""}
                        onChange={handleChange}
                        required
                    />
                )}
                {formData.duration.distributionType === "UNIFORM" && (
                    <div>
                        <input
                            type="number"
                            name="duration.lower"
                            id="duration.LOWER"
                            placeholder="Lower"
                            value={formData.duration.lower || ""}
                            onChange={handleChange}
                            required
                        />
                        <input
                            type="number"
                            name="duration.upper"
                            id="duration.UPPER"
                            placeholder="Upper"
                            value={formData.duration.upper || ""}
                            onChange={handleChange}
                            required
                        />
                    </div>
                )}
                {formData.duration.distributionType === "NORMAL" && (
                    <div>
                        <input
                            type="number"
                            name="duration.mean"
                            id="duration.MEAN"
                            placeholder="mean"
                            value={formData.duration.mean || ""}
                            onChange={handleChange}
                            required
                        />
                        <input
                            type="number"
                            name="duration.stDev"
                            id="duration.STDEV"
                            placeholder="standard deviation"
                            value={formData.duration.stDev || ""}
                            onChange={handleChange}
                            required
                        />
                    </div>
                )}
            </div>
        );
    }

    function handleButtonClick(item) {
        setSelectedInvestment(item);
        const savedRecord = existingInvestments.find(inv => inv.investmentTypeId === item.id);
        setFormData(prev => ({
            ...prev,
            investmentTypeId: savedRecord ? savedRecord.investmentTypeId : item.id
        }));
    }

    function setupAssetAllocation() {
        return (
            <div>
                {existingInvestments.map((item, index) => {
                    const matchedType = investmentTypes.find(type => type.id === item.investmentTypeId);
                    const allocationKey = matchedType
                        ? `${matchedType.name} ${item.taxStatus}`
                        : item.investmentTypeId;
                    return (
                        <form key={item.investmentTypeId || index} className="investment-form">
                            <div className="login">
                                <label htmlFor={`name-${index}`}></label>
                                {!(matchedType && matchedType.name === "CASH") && (
                                    <button
                                        type="button"
                                        id={`name-${index}`}
                                        name="name"
                                        onClick={() => handleButtonClick(item)}
                                    >
                                        <span>{matchedType ? matchedType.name : ""} </span>
                                        {item.taxStatus}
                                        <input
                                            type="number"
                                            step="any"
                                            value={allocationValues[allocationKey] !== undefined ? allocationValues[allocationKey] : ""}
                                            onChange={(e) => {
                                                const val = parseFloat(e.target.value);
                                                setAllocationValues(prev => ({
                                                    ...prev,
                                                    [allocationKey]: isNaN(val) ? 0 : val
                                                }));
                                            }}
                                        />
                                    </button>
                                )}
                            </div>
                        </form>
                    );
                })}
            </div>
        );
    }

    function handleSaveList() {
        const savedList = existingInvestments.reduce((acc, item) => {
            const matchedType = investmentTypes.find(type => type.id === item.investmentTypeId);
            const allocationKey = matchedType
                ? `${matchedType.name} ${item.taxStatus}`
                : item.investmentTypeId;
            if (allocationValues.hasOwnProperty(allocationKey)) {
                acc.push({ investmentKey: allocationKey, ratio: allocationValues[allocationKey] });
            }
            return acc;
        }, []);
        setFormData(prev => ({ ...prev, assetAllocations: savedList }));
        alert("List is Saved!");
    }

    async function handleSubmit() {
        const scenarioId = localStorage.getItem("scenario");
        const updatedData = { ...formData, scenarioId };
        await axios.put(
            `http://localhost:10000/api/invest-events/${id}`,
            updatedData,
            { withCredentials: true, headers: { "Content-Type": "application/json" } }
        );
        alert("Updated");
        navPage('/SimulationManagement')
    }

    function investmentSetting() {
        return (
            <div>
                <form>
                    <div className="login">
                        <label htmlFor="name">name:</label>
                        <input
                            type="text"
                            id="name"
                            name="name"
                            value={formData.name}
                            onChange={(e) => setFormData(prev => ({ ...prev, name: e.target.value }))}
                            required
                        />
                    </div>
                    <div className="login">
                        <label htmlFor="startYear.amountOrPercent">Start Year: </label>
                        <select
                            name="startYear.amountOrPercent"
                            id="startYear.amountOrPercent"
                            value={formData.startYear.amountOrPercent}
                            onChange={handleChange}
                            required
                        >
                            <option value="AMOUNT">Amount</option>
                            {/*<option value="PERCENT">Percent</option>*/}
                        </select>
                    </div>
                    <div className="login">
                        <label htmlFor="startYear.distributionType">Distribution Type </label>
                        <select
                            name="startYear.distributionType"
                            id="startYear.distributionType"
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
                        <label htmlFor="duration.amountOrPercent">Duration: </label>
                        <select
                            name="duration.amountOrPercent"
                            id="duration.amountOrPercent"
                            value={formData.duration.amountOrPercent}
                            onChange={handleChange}
                            required
                        >
                            <option value="AMOUNT">Amount</option>
                            <option value="PERCENT">Percent</option>
                        </select>
                    </div>
                    <div className="login">
                        <label htmlFor="duration.distributionType">Distribution Type </label>
                        <select
                            name="duration.distributionType"
                            id="duration.distributionType"
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
                    {setupAssetAllocation()}
                    <button type="button" onClick={handleSaveList}>Save to List</button>
                    <div className="login">
                        <label htmlFor="maxCash">Max Cash: </label>
                        <input
                            type="number"
                            id="maxCash"
                            name="maxCash"
                            value={formData.maxCash}
                            onChange={(e) => setFormData(prev => ({ ...prev, maxCash: e.target.value }))}
                            required
                        />
                    </div>
                </form>
                <button type="button" onClick={handleSubmit}>Submit</button>
            </div>
        );
    }

    function investmentPage() {
        return (
            <div className="profileSetting">
                <p className="logoLetter" style={{ color: 'black', fontSize: '5vh', marginTop: "30px" }}>
                    Invest Events
                </p>
                {investmentSetting()}
            </div>
        );
    }

    return (
        <div className="total">
            <nav className="navBarTop">
                <img onClick={() => navPage('/Homepage')} src="/public/caffeineOverloadLogo.png" alt="logo" className="logoSize" />
                <p className="logoLetter">Life Time Financial Planner</p>
                <div></div>
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
            {investmentPage()}
        </div>
    );
}

export default InvestEventPage;
