import React, { useState } from 'react';
import './common.css';
import {useEffect} from "react";
import axios from "axios";
import { useNavigate } from 'react-router-dom';

function investPage(){
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
        scenarioId: '',// private Long scenarioId;
        name: 'Cash',// private String name; // 'Cash', 'S&P 500' or 'Municipal bonds'
        description: '',// private String description;
        expectedAnnualReturn: {
            amountOrPercent: "AMOUNT",
            distributionType: "FIXED",
            value: null,
            lower: null,
            upper: null,
            mean: null,
            stDev: null
        },// private DistributionDTO expectedAnnualReturn;
        expenseRatio: '',// private Double expenseRatio;
        expectedAnnualIncome: {
            amountOrPercent: "AMOUNT",
            distributionType: "FIXED",
            value: null,
            lower: null,
            upper: null,
            mean: null,
            stDev: null
        },// private DistributionDTO expectedAnnualIncome;
        taxability: 'Y'// private String taxability; // 'Y' or 'N'
    });

    const popupMenu = () => {
        setSide(prevState => !prevState);
    };

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

    function toHome(){
        navPage('/Homepage')
    }
    function toProfile(){
        navPage('/Profset');
    }

    async function handleSubmit(event) {
        event.preventDefault();
        formData.scenarioId = localStorage.getItem("scenario")
        console.log(formData)
        try {
            const response = await axios.post("http://localhost:10000/api/investment-types", formData, { withCredentials: true, headers: { "Content-Type": "application/json" } });
            console.log("Scenario ID:", response.data);
            navPage('/Investment')
        } catch (error) {
            console.error("Scenario Error:", error);
            alert("Try again");
        }
    }

    const handleChange = (e) => {
        const { name, value } = e.target;
        if (name === "name") {
            setFormData(prevState => {
                let newState = {
                    ...prevState,
                    name: value,
                };
                if (value === "S&P 500") {
                    newState.taxability = "Y";
                } else if (value === "TAX-EXEMPT BONDS") {
                    newState.taxability = "N";
                }
                return newState;
            });
            return;
        }
        if (name === "taxability") {
            if (formData.name === "S&P 500" && value === "N") {
                return;
            }
            if (formData.name === "TAX-EXEMPT BONDS" && value === "Y") {
                return;
            }
            setFormData(prevState => ({
                ...prevState,
                taxability: value,
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

    function chooseMone() {
        return (
            <div>
                {formData.expectedAnnualReturn.distributionType === "FIXED" && (
                    <input
                        type="number"
                        name="expectedAnnualReturn.value"
                        id="expectedAnnualReturn.FIXED"
                        placeholder="value"
                        value={formData.expectedAnnualReturn.value || ""}
                        onChange={handleChange}
                        required
                    />
                )}
                {formData.expectedAnnualReturn.distributionType === "UNIFORM" && (
                    <div>
                        <input
                            type="number"
                            name="expectedAnnualReturn.lower"
                            id="expectedAnnualReturn.LOWER"
                            placeholder="Lower"
                            value={formData.expectedAnnualReturn.lower || ""}
                            onChange={handleChange}
                            required
                        />
                        <input
                            type="number"
                            name="expectedAnnualReturn.upper"
                            id="expectedAnnualReturn.UPPER"
                            placeholder="Upper"
                            value={formData.expectedAnnualReturn.upper || ""}
                            onChange={handleChange}
                            required
                        />
                    </div>
                )}
                {formData.expectedAnnualReturn.distributionType === "NORMAL" && (
                    <div>
                        <input
                            type="number"
                            name="expectedAnnualReturn.mean"
                            id="expectedAnnualReturn.MEAN"
                            placeholder="mean"
                            value={formData.expectedAnnualReturn.mean || ""}
                            onChange={handleChange}
                            required
                        />
                        <input
                            type="number"
                            name="expectedAnnualReturn.stDev"
                            id="expectedAnnualReturn.STDEV"
                            placeholder="standard deviation"
                            value={formData.expectedAnnualReturn.stDev || ""}
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
                {formData.expectedAnnualIncome.distributionType === "FIXED" && (
                    <input
                        type="number"
                        name="expectedAnnualIncome.value"
                        id="expectedAnnualIncome.FIXED"
                        placeholder="value"
                        value={formData.expectedAnnualIncome.value || ""}
                        onChange={handleChange}
                        required
                    />
                )}
                {formData.expectedAnnualIncome.distributionType === "UNIFORM" && (
                    <div>
                        <input
                            type="number"
                            name="expectedAnnualIncome.lower"
                            id="expectedAnnualIncome.LOWER"
                            placeholder="Lower"
                            value={formData.expectedAnnualIncome.lower || ""}
                            onChange={handleChange}
                            required
                        />
                        <input
                            type="number"
                            name="expectedAnnualIncome.upper"
                            id="expectedAnnualIncome.UPPER"
                            placeholder="Upper"
                            value={formData.expectedAnnualIncome.upper || ""}
                            onChange={handleChange}
                            required
                        />
                    </div>
                )}
                {formData.expectedAnnualIncome.distributionType === "NORMAL" && (
                    <div>
                        <input
                            type="number"
                            name="expectedAnnualIncome.mean"
                            id="expectedAnnualIncome.MEAN"
                            placeholder="mean"
                            value={formData.expectedAnnualIncome.mean || ""}
                            onChange={handleChange}
                            required
                        />
                        <input
                            type="number"
                            name="expectedAnnualIncome.stDev"
                            id="expectedAnnualIncome.STDEV"
                            placeholder="standard deviation"
                            value={formData.expectedAnnualIncome.stDev || ""}
                            onChange={handleChange}
                            required
                        />
                    </div>
                )}
            </div>
        );
    }

    function investManage(){
        return (<form onSubmit={handleSubmit} className="profileSetting">
            <div className="logoLetter" style={{color: 'black', fontSize: '5vh', marginTop: "30px"}} >Edit Invest Information</div>
            <div className="login"><label htmlFor="name">name </label>
                <select
                    name="name"
                    id="name"
                    value={formData.name}
                    onChange={handleChange} required>
                    <option value = "CASH">Cash</option>
                    <option value = "S&P 500">S&P 500</option>
                    <option value = "TAX-EXEMPT BONDS">Tax-exempt Bonds</option>
                </select></div>
            <div className="login"><label htmlFor="description">Description </label>
                <input
                    type="text"
                    id="description"
                    name="description"
                    value={formData.description}
                    onChange={handleChange}
                    placeholder="Description"
                    required
                /></div>
            <div className="login"><label htmlFor="expectedAnnualReturn.amountOrPercent">Expected Annual Return</label>
                <select
                    name="expectedAnnualReturn.amountOrPercent"
                    id="expectedAnnualReturn.amountOrPercent"
                    value={formData.expectedAnnualReturn.amountOrPercent}
                    onChange={handleChange} required>
                    <option value = "AMOUNT">Amount</option>
                    <option value = "PERCENT">Percent</option>
                </select></div>
            <div className="login"><label htmlFor="expectedAnnualReturndistributionType">Distribution Type </label>
                <select name="expectedAnnualReturn.distributionType" id="expectedAnnualReturndistributionType" value={formData.expectedAnnualReturn.distributionType} onChange={handleChange} required>
                    <option value = "FIXED">FIXED</option>
                    <option value = "UNIFORM">UNIFORM</option>
                    <option value = "NORMAL">NORMAL</option>
                </select></div>
            {chooseMone()}
            <div className="login"><label htmlFor="expenseRatio">User Percentage </label>
                <input
                    type="number"
                    id="expenseRatio"
                    name="expenseRatio"
                    value={formData.expenseRatio}
                    onChange={handleChange}
                    placeholder="Expense Ratio"
                    style={{width: "140px"}}
                    required
                /></div>
            <div className="login"><label htmlFor="expectedAnnualIncome.amountOrPercent">Expected Annual Return</label>
                <select
                    name="expectedAnnualIncome.amountOrPercent"
                    id="expectedAnnualIncome.amountOrPercent"
                    value={formData.expectedAnnualIncome.amountOrPercent}
                    onChange={handleChange} required>
                    <option value = "AMOUNT">Amount</option>
                    <option value = "PERCENT">Percent</option>
                </select></div>
            <div className="login"><label htmlFor="expectedAnnualIncomedistributionType">Distribution Type </label>
                <select name="expectedAnnualIncome.distributionType" id="expectedAnnualIncome.distributionType" value={formData.expectedAnnualIncome.distributionType} onChange={handleChange} required>
                    <option value = "FIXED">FIXED</option>
                    <option value = "UNIFORM">UNIFORM</option>
                    <option value = "NORMAL">NORMAL</option>
                </select></div>
            {chooseKone()}
            <div className="login"><label htmlFor="taxability">Tax Ability </label>
                <select
                    name="taxability"
                    id="taxability"
                    value={formData.taxability}
                    onChange={handleChange} required>
                    <option value = "Y">Yes </option> {/*Cash -> YES or NO || S&P 500 -> YES || TAX-EXEMPT BONDS -> NO*/}
                    <option value = "N">No </option>
                </select></div>
            <div>
                <button className="submitButton" type="submit">Save Changes</button></div>
        </form>);
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
        {investManage()}
    </div>);
}
export default investPage;
