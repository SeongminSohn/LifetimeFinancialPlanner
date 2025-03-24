import React, { useState } from 'react';
import './common.css';
import {useEffect} from "react";
import axios from "axios";
import { useNavigate } from 'react-router-dom';
import { useLocation } from 'react-router-dom';

function homePage(){
    useEffect(() => {
        const token = localStorage.getItem("token");
        if (token) {
            setLoggedIn(true);
        }
    }, []);

    const [openSide, setSide] = useState(false);
    const navPage = useNavigate();
    const [loggedIn, setLoggedIn] = useState(false)
    const [formData, setFormData] = useState({
        // userid: '', // userID
        scenarioId: '',
        name: '', // String name
        startYear: '', // Integer startYear
        initialAmount: '', // Double initialAmount
        annualChange: {
            amountOrPercent: "AMOUNT",
            distributionType: "FIXED",
            value: null,
            lower: null,
            upper: null,
            mean: null,
            stDev: null,
        }, // DistributionDTO annualChange
        inflationAdjustment: 'Y', // String inflationAdjustment      // 'Y' or 'N'
        userPercentage: '', // Double userPercentage
        isSocialSecurity: 'Y' // String isSocialSecurity;        // 'Y' or 'N'
    });


    const popupMenu = () => {
        setSide(prevState => !prevState);
    };

    async function handleSubmit(event) {
        event.preventDefault();
        formData.userId = localStorage.getItem("token")
        console.log(formData)
        try {
            const response = await axios.post("http://localhost:10000/api/income-events", formData, { withCredentials: true, headers: { "Content-Type": "application/json" } });
            console.log("Scenario ID:", response.data);
        } catch (error) {
            console.error("Scenario Error:", error);
            alert("Try again");
        }
    }

    function chooseMone() {
        return (
            <div>
                {formData.annualChange.distributionType === "FIXED" && (
                    <input
                        type="number"
                        name="annualChange.value"
                        id="annualChangeFIXED"
                        placeholder="value"
                        value={formData.annualChange.value || ""}
                        onChange={handleChange}
                        required
                    />
                )}
                {formData.annualChange.distributionType === "UNIFORM" && (
                    <div>
                        <input
                            type="number"
                            name="annualChange.lower"
                            id="annualChangeLOWER"
                            placeholder="Lower"
                            value={formData.annualChange.lower || ""}
                            onChange={handleChange}
                            required
                        />
                        <input
                            type="number"
                            name="annualChange.upper"
                            id="annualChangeUPPER"
                            placeholder="Upper"
                            value={formData.annualChange.upper || ""}
                            onChange={handleChange}
                            required
                        />
                    </div>
                )}
                {formData.annualChange.distributionType === "NORMAL" && (
                    <div>
                        <input
                            type="number"
                            name="annualChange.mean"
                            id="annualChangeMEAN"
                            placeholder="mean"
                            value={formData.annualChange.mean || ""}
                            onChange={handleChange}
                            required
                        />
                        <input
                            type="number"
                            name="annualChange.stDev"
                            id="annualChangeSTDEV"
                            placeholder="standard deviation"
                            value={formData.annualChange.stDev || ""}
                            onChange={handleChange}
                            required
                        />
                    </div>
                )}
            </div>
        );
    }

    function incomeManager(){
        return (<form onSubmit={handleSubmit} className="profileSetting">
            <div className="logoLetter" style={{color: 'black', fontSize: '5vh', marginTop: "30px"}} >Edit Income Information</div>
            <div className="login">Event Name<label htmlFor="name"></label>
                <input
                    type="text"
                    id="name"
                    name="name"
                    value={formData.name}
                    onChange={handleChange}
                    placeholder="Event Name"
                    required
                /></div>
            <div className="login"><label htmlFor="startYear">Start Year</label>
                <input
                    type="number"
                    id="startYear"
                    name="startYear"
                    value={formData.startYear}
                    onChange={handleChange}
                    placeholder="start Year"
                    required
                /></div>
            <div className="login">Initial Amount<label htmlFor="initialAmount"></label>
                <input
                    type="number"
                    id="initialAmount"
                    name="initialAmount"
                    value={formData.initialAmount}
                    onChange={handleChange}
                    placeholder="Initial Amount"
                    required
                /></div>
            <div className="login"><label htmlFor="annualChange.amountOrPercent">Annual Year </label>
                <select
                    name="annualChange.amountOrPercent"
                    id="annualChange.amountOrPercent"
                    value={formData.annualChange.amountOrPercent}
                    onChange={handleChange} required>
                    <option value = "AMOUNT">Amount</option>
                    <option value = "PERCENT">Percent</option>
                </select></div>
            <div className="login"><label htmlFor="annualChangedistributionType">Distribution Type </label>
                <select name="annualChange.distributionType" id="annualChangedistributionType" value={formData.annualChange.distributionType} onChange={handleChange} required>
                    <option value = "FIXED">FIXED</option>
                    <option value = "UNIFORM">UNIFORM</option>
                    <option value = "NORMAL">NORMAL</option>
                </select></div>
            {chooseMone()}
            <div className="login"><label htmlFor="inflationAdjustment">InflationAdjustment</label>
                <select name="inflationAdjustment" id="inflationAdjustment" value={formData.inflationAdjustment} onChange={handleChange} required>
                    <option value = "Y">Yes</option>
                    <option value = "N">No</option>
                </select></div>
            <div className="login"><label htmlFor="userPercentage">User Percentage </label>
                <input
                    type="number"
                    id="userPercentage"
                    name="userPercentage"
                    value={formData.userPercentage}
                    onChange={handleChange}
                    placeholder="User Percentage"
                    style={{width: "140px"}}
                    required
                /></div>
            <div className="login"><label htmlFor="isSocialSecurity">Is SocialSecurity</label>
                <select name="isSocialSecurity" id="isSocialSecurity" value={formData.isSocialSecurity} onChange={handleChange} required>
                    <option value = "Y">Yes</option>
                    <option value = "N">No</option>
                </select></div>
            <div>
                <button className="submitButton" type="submit">Save Changes</button></div>
        </form>);
    }

    const handleChange = (e) => {
        const { name, value } = e.target;
        if (name === "userPercentage") {
            let numericValue = parseFloat(value);
            if (isNaN(numericValue)) {
                numericValue = 0;
            }
            if (numericValue < 0) numericValue = 0;
            if (numericValue > 1) numericValue = 1;

            setFormData(prev => ({
                ...prev,
                userPercentage: numericValue,
            }));
            return;
        }
        if (name === "startYear") {
            const currentYear = new Date().getFullYear();
            const numericValue = parseInt(value, 10);
            if (!isNaN(numericValue) && numericValue > currentYear) {
                setFormData(prev => ({
                    ...prev,
                    [name]: currentYear,
                }));
                return;
            }
        }
        if (name.includes('.')) {
            const [parentKey, childKey] = name.split('.');
            setFormData(prevState => ({
                ...prevState,
                [parentKey]: {
                    ...prevState[parentKey],
                    [childKey]: value
                }
            }));
        } else {
            setFormData(prevState => ({
                ...prevState,
                [name]: value
            }));
        }
    }


    function sideElements(){
        return openSide && (
            <aside className="sidebar">
                <button onClick={toIncome}>Income Edit</button>
                <button onClick={toExpense}>Expense Edit</button>
                <button onClick={toInvest}>Invest Edit</button>
                <button onClick={toSim}>Scenario Simulation</button>
                <button>Reports & Logs</button>
                <button>Import & Export Date</button>
            </aside>
        )
    }
    function toHome(){
        navPage('/Homepage')
    }

    function toIncome(){
        navPage('/IncomePage')
    }

    function toExpense(){
        navPage('/ExpenseEdit');
    }

    function toInvest(){
        navPage('/InvestEdit')
    }

    function toSim(){
        navPage('/simulationPage')
    }

    function toProfile(){
        navPage('/Profset');
    }

    return (<div className="total">
        <nav className="navBarTop">
            <img onClick={toHome} src ="/public/caffeineOverloadLogo.png" className = "logoSize"></img>
            <p className= "logoLetter">Life Time Financial Planner</p>
            <div></div>
        </nav>
        <nav className= "navBarSub">
            <button className="commonButton" onClick={popupMenu}>Menu</button>
            {sideElements()}
            {loggedIn === true && (<button className="commonButton" onClick={toProfile}>
                Scenario Setting
            </button>)}
        </nav>
        {incomeManager()}
    </div>);
}
export default homePage;
