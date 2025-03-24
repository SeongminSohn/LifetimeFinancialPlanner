import React, { useState } from 'react';
import './common.css';
import {useEffect} from "react";
import axios from "axios";
import profileImage from '/public/back.jpg';
import { useNavigate } from 'react-router-dom';

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
        name: '', // String name
        startYear: null, // Integer startYear
        initialAmount: null, // Double initialAmount
        annualChange: {
            amountOrPercent: "AMOUNT",
            distributionType: "FIXED",
            value: null,
            lower: null,
            upper: null,
            mean: null,
            stDev: null,
        }, // DistributionDTO annualChange
        inflationAdjustment: '', // String inflationAdjustment      // 'Y' or 'N'
        userPercentage: 0, // Double userPercentage
        isSocialSecurity: '' // String isSocialSecurity;        // 'Y' or 'N'
    });


    const popupMenu = () => {
        setSide(prevState => !prevState);
    };

    async function handleSubmit(event) {
        event.preventDefault();
        console.log(formData)
        try {
            const response = await axios.post("http://localhost:10000/api/scenarios", formData, { withCredentials: true, headers: { "Content-Type": "application/json" } });
            console.log("Scenario success:", response.data);
        } catch (error) {
            console.error("Scenario Error:", error);
            alert("Try again");
        }
    }

    function chooseMone() {
        return (
            <div>
                {formData.lifeExpectancyUser.distributionType === "FIXED" && (
                    <input
                        type="number"
                        name="lifeExpectancyUser.value"
                        id="distributionTypeFIXED"
                        placeholder="value"
                        value={formData.lifeExpectancyUser.value || ""}
                        onChange={handleChange}
                        required
                    />
                )}
                {formData.lifeExpectancyUser.distributionType === "UNIFORM" && (
                    <div>
                        <input
                            type="number"
                            name="lifeExpectancyUser.lower"
                            id="distributionTypeUNIFORM_lower"
                            placeholder="Lower"
                            value={formData.lifeExpectancyUser.lower || ""}
                            onChange={handleChange}
                            required
                        />
                        <input
                            type="number"
                            name="lifeExpectancyUser.upper"
                            id="distributionTypeUNIFORM_upper"
                            placeholder="Upper"
                            value={formData.lifeExpectancyUser.upper || ""}
                            onChange={handleChange}
                            required
                        />
                    </div>
                )}
                {formData.lifeExpectancyUser.distributionType === "NORMAL" && (
                    <div>
                        <input
                            type="number"
                            name="lifeExpectancyUser.mean"
                            id="distributionTypeNORMAL_mean"
                            placeholder="mean"
                            value={formData.lifeExpectancyUser.mean || ""}
                            onChange={handleChange}
                            required
                        />
                        <input
                            type="number"
                            name="lifeExpectancyUser.stDev"
                            id="distributionTypeNORMAL_stDev"
                            placeholder="standard deviation"
                            value={formData.lifeExpectancyUser.stDev || ""}
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
            <div className="logoLetter" style={{fontSize: '50px', marginTop: "30px"}} >Edit Income Information</div>
            <div className="login"><label htmlFor="name"></label>
                <input
                    type="text"
                    id="name"
                    name="name"
                    value={formData.name}
                    onChange={handleChange}
                    placeholder="Event Name"
                    required
                /></div>
            <div className="login"><label htmlFor="name"></label>
                <input
                    type="number"
                    id="startYear"
                    name="startYear"
                    value={formData.startYear}
                    onChange={handleChange}
                    placeholder="start Year"
                    required
                /></div>
            <div className="login"><label htmlFor="initialAmount"></label>
                <input
                    type="number"
                    id="initialAmount"
                    name="initialAmount"
                    value={formData.initialAmount}
                    onChange={handleChange}
                    placeholder="Initial Amount"
                    required
                /></div>
            <div className="login"><label htmlFor="initialAmount"></label>
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
            {chooseMone()}
            <div>
                <button className="submitButton" type="submit">Create An Account</button></div>
        </form>);
    }

    const handleChange = (e) => {
        const { name, value } = e.target;
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
                profile Setting
            </button>)}
        </nav>
        {incomeManager()}
    </div>);
}
export default homePage;
