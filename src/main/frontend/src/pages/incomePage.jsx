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

    // useEffect(() => {
    //     axios.get(`http://localhost:10000/api/income-events/2`)
    //         .then(response => {
    //             console.log("Existing IncomeEvents:", response.data);
    //             setFormData(prevState => ({
    //                 ...prevState,
    //                 ...response.data
    //             }));
    //         })
    //         .catch(error => {
    //             console.error("Error fetching incomes:", error);
    //         });
    // }, []);

    const [openSide, setSide] = useState(false);
    const navPage = useNavigate();
    const [loggedIn, setLoggedIn] = useState(false)
    const [formData, setFormData] = useState({
        // userid: '', // userID
        scenarioId: '',
        name: '', // String name
        startYear: {
            amountOrPercent: "AMOUNT",
            distributionType: "FIXED",
            value: null,
            lower: null,
            upper: null,
            mean: null,
            stDev: null
        },// private DistributionDTO startYear;
        duration: {
            amountOrPercent: "AMOUNT",
            distributionType: "FIXED",
            value: null,
            lower: null,
            upper: null,
            mean: null,
            stDev: null
        },// private DistributionDTO duration;
        eventType: '',// private String eventType;  'INCOME', 'EXPENSE', 'INVEST'
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


        const { distributionType: distU, lower: lowU, upper: upU } = formData.startYear;
        if (distU === "UNIFORM" && Number(upU) <= Number(lowU)) {
            alert("Upper Value has to be greater than lower value for Start Year");
            return;
        }

        const { distributionType: distI, lower: lowI, upper: upI } = formData.annualChange;
        if (distI === "UNIFORM" && Number(upI) <= Number(lowI)) {
            alert("Upper Value has to be greater than lower value for Annual Change.");
            return;
        }

        const { distributionType: distD, lower: lowD, upper: upD } = formData.duration;
        if (distD === "UNIFORM" && Number(upD) <= Number(lowD)){
            alert("Upper Value has to be greater than lower value for Duration.");
            return;
        }

        formData.scenarioId = localStorage.getItem("scenario")
        console.log(formData)
        try {
            const response = await axios.post("http://localhost:10000/api/income-events", formData, { withCredentials: true, headers: { "Content-Type": "application/json" } });
            console.log("Scenario ID:", response.data);
            toExpense()
        } catch (error) {
            console.error("Scenario Error:", error);
            alert("Try again");
        }
    }

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
                        value={formData.duration.value ?? ""}
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
                            value={formData.duration.lower ?? ""}
                            onChange={handleChange}
                            required
                        />
                        <input
                            type="number"
                            name="duration.upper"
                            id="duration.UPPER"
                            placeholder="Upper"
                            value={formData.duration.upper ?? ""}
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
                            value={formData.duration.mean ?? ""}
                            onChange={handleChange}
                            required
                        />
                        <input
                            type="number"
                            name="duration.stDev"
                            id="duration.STDEV"
                            placeholder="standard deviation"
                            value={formData.duration.stDev ?? ""}
                            onChange={handleChange}
                            required
                        />
                    </div>
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
                        id="annualChange.FIXED"
                        placeholder="value"
                        value={formData.annualChange.value ?? ""}
                        onChange={handleChange}
                        required
                    />
                )}
                {formData.annualChange.distributionType === "UNIFORM" && (
                    <div>
                        <input
                            type="number"
                            name="annualChange.lower"
                            id="annualChange.LOWER"
                            placeholder="Lower"
                            value={formData.annualChange.lower ?? ""}
                            onChange={handleChange}
                            required
                        />
                        <input
                            type="number"
                            name="annualChange.upper"
                            id="annualChange.UPPER"
                            placeholder="Upper"
                            value={formData.annualChange.upper ?? ""}
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
                            id="annualChange.MEAN"
                            placeholder="mean"
                            value={formData.annualChange.mean ?? ""}
                            onChange={handleChange}
                            required
                        />
                        <input
                            type="number"
                            name="annualChange.stDev"
                            id="annualChange.STDEV"
                            placeholder="standard deviation"
                            value={formData.annualChange.stDev ?? ""}
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
            <div className="login"><label htmlFor="name">Name </label>
                <input
                    type="text"
                    id="name"
                    name="name"
                    value={formData.name}
                    onChange={handleChange}
                    placeholder="Name"
                    required
                /></div>
            <div className="login"><label htmlFor="startYear.amountOrPercent">Start Year </label>
                <select
                    name="startYear.amountOrPercent"
                    id="startYear.amountOrPercent"
                    value={formData.startYear.amountOrPercent}
                    onChange={handleChange} required>
                    <option value = "AMOUNT">Amount</option>
                    {/*<option value = "PERCENT">Percent</option>*/}
                </select></div>
            <div className="login"><label htmlFor="startYear.distributionType">Distribution Type </label>
                <select name="startYear.distributionType" id="startYear.distributionType" value={formData.startYear.distributionType} onChange={handleChange} required>
                    <option value = "FIXED">FIXED</option>
                    <option value = "UNIFORM">UNIFORM</option>
                    <option value = "NORMAL">NORMAL</option>
                </select></div>
            {chooseMone()}

            <div className="login"><label htmlFor="duration.amountOrPercent">Duration: </label>
                <select
                    name="duration.amountOrPercent"
                    id="duration.amountOrPercent"
                    value={formData.duration.amountOrPercent}
                    onChange={handleChange} required>
                    <option value = "AMOUNT">Amount</option>
                    <option value = "PERCENT">Percent</option>
                </select></div>
            <div className="login"><label htmlFor="duration.distributionType">Distribution Type </label>
                <select name="duration.distributionType" id="duration.distributionType" value={formData.duration.distributionType} onChange={handleChange} required>
                    <option value = "FIXED">FIXED</option>
                    <option value = "UNIFORM">UNIFORM</option>
                    <option value = "NORMAL">NORMAL</option>
                </select></div>
            {chooseKone()}

            <div className="login"><label htmlFor="eventType">Event Type </label>
                <input
                    type="text"
                    id="eventType"
                    name="eventType"
                    value={formData.eventType}
                    onChange={handleChange}
                    placeholder="eventType"
                    required
                /></div>

            <div className="login"><label htmlFor="initialAmount">Initial Amount </label>
                <input
                    type="number"
                    id="initialAmount"
                    name="initialAmount"
                    value={formData.initialAmount}
                    onChange={handleChange}
                    placeholder="initialAmount"
                    required
                /></div>

            <div className="login"><label htmlFor="annualChange.amountOrPercent">Annual Change </label>
                <select
                    name="annualChange.amountOrPercent"
                    id="annualChange.amountOrPercent"
                    value={formData.annualChange.amountOrPercent}
                    onChange={handleChange} required>
                    <option value = "AMOUNT">Amount</option>
                    <option value = "PERCENT">Percent</option>
                </select></div>
            <div className="login"><label htmlFor="annualChange.distributionType">Distribution Type </label>
                <select name="annualChange.distributionType" id="annualChange.distributionType" value={formData.annualChange.distributionType} onChange={handleChange} required>
                    <option value = "FIXED">FIXED</option>
                    <option value = "UNIFORM">UNIFORM</option>
                    <option value = "NORMAL">NORMAL</option>
                </select></div>
            {chooseLone()}

            <div className="login"><label htmlFor="inflationAdjustment">InflationAdjustment </label>
                <select
                    name="inflationAdjustment"
                    id="inflationAdjustment"
                    value={formData.inflationAdjustment}
                    onChange={handleChange} required>
                    <option value = "Y">Yes </option> {/*Cash -> YES or NO || S&P 500 -> YES || TAX-EXEMPT BONDS -> NO*/}
                    <option value = "N">No </option>
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

    function handleChange(e) {
        const { name, value } = e.target;

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

        if (name === "birthYearUser" || name === "birthYearSpouse") {
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

        if (name === "financialGoal") {
            let numericValue = parseFloat(value);
            if (isNaN(numericValue)) numericValue = 1;
            if (numericValue < 0) numericValue = 1;

            setFormData(prev => ({
                ...prev,
                financialGoal: numericValue,
            }));
            return;
        }

        if (name.includes('.')) {
            const [parentKey, childKey] = name.split('.');

            if (
                (parentKey === "startYear" || parentKey === "annualChange" || parentKey === "duration") &&
                formData[parentKey].amountOrPercent === "PERCENT"
            ) {
                let num = parseFloat(value);
                if (isNaN(num)) num = "";
                else num = Math.max(0, Math.min(1, num));

                setFormData(prev => ({
                    ...prev,
                    [parentKey]: {
                        ...prev[parentKey],
                        [childKey]: num
                    }
                }));
                return;
            }

            setFormData(prevState => ({
                ...prevState,
                [parentKey]: {
                    ...prevState[parentKey],
                    [childKey]: value
                }
            }));
        } else {
            if (name === "maritalStatus") {
                setFormData(prevState => ({
                    ...prevState,
                    maritalStatus: value,
                    lifeExpectancySpouse: {
                        ...prevState.lifeExpectancySpouse,
                        amountOrPercent: value === "Y" ? "AMOUNT" : value === "N" ? null : prevState.lifeExpectancySpouse?.amountOrPercent,
                        distributionType: value === "Y" ? "FIXED" : value === "N" ? null : prevState.lifeExpectancySpouse?.distributionType,
                    }
                }));
            } else {
                setFormData(prevState => ({
                    ...prevState,
                    [name]: value
                }));
            }
        }
    }


    function sideElements() {
        return openSide && (
            <aside className="sidebar">
                <button onClick={toInvestment}>Investment</button>
                <button onClick={toExpense}>Expense Edit</button>
                {/*<button onClick={toInvest}>Invest Edit</button>*/}
                <button onClick={toWithDrawal}>Expense Withdrawal Edit</button>
                <button onClick={toInvestEvent}>Invest Event Edit</button>
                <button onClick={toSim}>Scenario Simulation</button>
                <button disabled>Import & Export Data</button>
            </aside>
        );
    }

    function toInvestment(){
        navPage('/Investment')
    }
    function toWithDrawal(){
        navPage('/ExpenseW');
    }
    function toIncome() {
        navPage('/IncomePage');
    }
    function toExpense() {
        navPage('/ExpenseEdit');
    }
    function toInvest() {
        navPage('/InvestEdit');
    }
    function toSim() {
        navPage('/simulationPage');
    }
    function toHome() {
        navPage('/Homepage');
    }
    function toProfile() {
        navPage('/Profset');
    }
    function toInvestEvent(){
        navPage("/InvestEvent")
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
