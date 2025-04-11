import React, { useState } from 'react';
import './common.css';
import {useEffect} from "react";
import axios from "axios";
import profileImage from '/public/back.jpg';
import { useNavigate } from 'react-router-dom';


function profileSetting(){
    //useEffect() get data from server and refect those data
    // if there is data, then print list of scenarios settings.....
    // const [openSide, setSide] = useState(false);
    const navPage = useNavigate();
    const [formData, setFormData] = useState({
        userId: '',
        name: '',
        maritalStatus: 'N',
        birthYearUser: '',
        birthYearSpouse: '',
        lifeExpectancyUser: {
            amountOrPercent: "AMOUNT",
            distributionType: "FIXED",
            value: null,
            lower: null,
            upper: null,
            mean: null,
            stDev: null
        },
        lifeExpectancySpouse: {
            amountOrPercent: null,
            distributionType: null,
            value: null,
            lower: null,
            upper: null,
            mean: null,
            stDev: null
        },
        financialGoal: '',
        afterTaxContributionLimit: '',
        stateOfResidence: 'AL',
        inflationAssumption: {
            amountOrPercent: "PERCENT",
            distributionType: "FIXED",
            value: null,
            lower: null,
            upper: null,
            mean: null,
            stDev: null,
        }
    });
    const [loggedIn, setLoggedIn] = useState(false);

    useEffect(() => {
        const token = localStorage.getItem("token");
        if (token) {
            setLoggedIn(true);
        }
    }, []);

    useEffect(() => {
        const scenarioId = localStorage.getItem("scenario");
        console.log(scenarioId)
        if (scenarioId) {
            axios.get(`http://localhost:10000/api/scenarios/${scenarioId}`)
                .then(response => {
                    console.log("Existing Investments:", response.data);
                    setFormData(response.data);
                })
                .catch(error => {
                    console.error("Error fetching investments:", error);
                });
        }
    }, []);

    //
    // const popupMenu = () => {
    //     setSide(prevState => !prevState);
    // };
    //
    // function sideElements(){
    //     return openSide && (
    //         <aside className="sidebar">
    //             <button onClick={toIncome}>Income Edit</button>
    //             <button onClick={toExpense}>Expense Edit</button>
    //             <button onClick={toInvest}>Invest Edit</button>
    //             <button onClick={toSim}>Scenario Simulation</button>
    //             <button>Reports & Logs</button>
    //             <button>Import & Export Date</button>
    //         </aside>
    //     )
    // }

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

    function toInvestment(){
        navPage('/Investment')
    }

    function handleChange(e) {
        const { name, value } = e.target;

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


    async function handleSubmit(event) {
        event.preventDefault();
        formData.userId = localStorage.getItem("token")
        const scenarioId =  localStorage.getItem("scenario")
        console.log("hi",scenarioId )
        try {
            if (scenarioId) {
                const response = await axios.put(`http://localhost:10000/api/scenarios/${scenarioId}`, formData, {
                    withCredentials: true,
                    headers: {"Content-Type": "application/json"}
                });
                // console.log("Scenario ID:", response.data);
                // localStorage.setItem("scenario", response.data.scenarioId);
                toInvestment()
            }else{
                const response = await axios.post(
                    `http://localhost:10000/api/scenarios`, formData, { withCredentials: true, headers: { "Content-Type": "application/json" } });console.log("Created Investment:", response.data);
                console.log("Scenario ID:", response.data);
                localStorage.setItem("scenario", response.data.scenarioId);
                toInvestment()
            }
        } catch (error) {
            console.error("Scenario Error:", error);
            alert("Try again");
        }
    }

    function changerSpouse() {
        if (formData.maritalStatus === "Y") {
            setFormData(prev => ({
                ...prev,
                lifeExpectancySpouse: {
                    ...prev.lifeExpectancySpouse,
                    amountOrPercent: "AMOUNT",
                    distributionType: "FIXED"
                }
            }));
        }
    }

    function stateSelection() {
        return (<div className="login">
                <label htmlFor="stateOfResidence">State Selection</label>
                <select name="stateOfResidence" id="stateOfResidence" value={formData.stateOfResidence} onChange={handleChange}>
                    <option value="AL">AL</option>
                    <option value="AK">AK</option>
                    <option value="AZ">AZ</option>
                    <option value="AR">AR</option>
                    <option value="CA">CA</option>
                    <option value="CO">CO</option>
                    <option value="CT">CT</option>
                    <option value="DE">DE</option>
                    <option value="FL">FL</option>
                    <option value="GA">GA</option>
                    <option value="HI">HI</option>
                    <option value="ID">ID</option>
                    <option value="IL">IL</option>
                    <option value="IN">IN</option>
                    <option value="IA">IA</option>
                    <option value="KS">KS</option>
                    <option value="KY">KY</option>
                    <option value="LA">LA</option>
                    <option value="ME">ME</option>
                    <option value="MD">MD</option>
                    <option value="MA">MA</option>
                    <option value="MI">MI</option>
                    <option value="MN">MN</option>
                    <option value="MS">MS</option>
                    <option value="MO">MO</option>
                    <option value="MT">MT</option>
                    <option value="NE">NE</option>
                    <option value="NV">NV</option>
                    <option value="NH">NH</option>
                    <option value="NJ">NJ</option>
                    <option value="NM">NM</option>
                    <option value="NY">NY</option>
                    <option value="NC">NC</option>
                    <option value="ND">ND</option>
                    <option value="OH">OH</option>
                    <option value="OK">OK</option>
                    <option value="OR">OR</option>
                    <option value="PA">PA</option>
                    <option value="RI">RI</option>
                    <option value="SC">SC</option>
                    <option value="SD">SD</option>
                    <option value="TN">TN</option>
                    <option value="TX">TX</option>
                    <option value="UT">UT</option>
                    <option value="VT">VT</option>
                    <option value="VA">VA</option>
                    <option value="WA">WA</option>
                    <option value="WV">WV</option>
                    <option value="WI">WI</option>
                    <option value="WY">WY</option>

                </select>
            </div>
        );
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

    function chooseFone() {
        return (
            <div>
                {formData.lifeExpectancySpouse.distributionType === "FIXED" && (
                    <input
                        type="number"
                        name="lifeExpectancySpouse.value"
                        id="distributionTypeSpouseFIXED"
                        placeholder="value"
                        value={formData.lifeExpectancySpouse.value || ""}
                        onChange={handleChange}
                        required
                    />
                )}
                {formData.lifeExpectancySpouse.distributionType === "UNIFORM" && (
                    <div>
                        <input
                            type="number"
                            name="lifeExpectancySpouse.lower"
                            id="distributionTypeSpouse_lower"
                            placeholder="Lower"
                            value={formData.lifeExpectancySpouse.lower || ""}
                            onChange={handleChange}
                            required
                        />
                        <input
                            type="number"
                            name="lifeExpectancySpouse.upper"
                            id="distributionTypeSpouse_upper"
                            placeholder="Upper"
                            value={formData.lifeExpectancySpouse.upper || ""}
                            onChange={handleChange}
                            required
                        />
                    </div>
                )}
                {formData.lifeExpectancySpouse.distributionType === "NORMAL" && (
                    <div>
                        <input
                            type="number"
                            name="lifeExpectancySpouse.mean"
                            id="distributionTypeSpouse_mean"
                            placeholder="mean"
                            value={formData.lifeExpectancySpouse.mean || ""}
                            onChange={handleChange}
                            required
                        />
                        <input
                            type="number"
                            name="lifeExpectancySpouse.stDev"
                            id="distributionTypeSpouse_stDev"
                            placeholder="standard deviation"
                            value={formData.lifeExpectancySpouse.stDev || ""}
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
                {formData.inflationAssumption.distributionType === "FIXED" && (
                    <input
                        type="number"
                        name="inflationAssumption.value"
                        id="distributionTypeinflationAssumptionFIXED"
                        placeholder="value"
                        value={formData.inflationAssumption.value || ""}
                        onChange={handleChange}
                        required
                    />
                )}
                {formData.inflationAssumption.distributionType === "UNIFORM" && (
                    <div>
                        <input
                            type="number"
                            name="inflationAssumption.lower"
                            id="distributionTypeinflationAssumption_lower"
                            placeholder="Lower"
                            value={formData.inflationAssumption.lower || ""}
                            onChange={handleChange}
                            required
                        />
                        <input
                            type="number"
                            name="inflationAssumption.upper"
                            id="distributionTypeinflationAssumption_upper"
                            placeholder="Upper"
                            value={formData.inflationAssumption.upper || ""}
                            onChange={handleChange}
                            required
                        />
                    </div>
                )}
                {formData.inflationAssumption.distributionType === "NORMAL" && (
                    <div>
                        <input
                            type="number"
                            name="inflationAssumption.mean"
                            id="distributionTypeinflationAssumption_mean"
                            placeholder="mean"
                            value={formData.inflationAssumption.mean || ""}
                            onChange={handleChange}
                            required
                        />
                        <input
                            type="number"
                            name="inflationAssumption.stDev"
                            id="distributionTypeinflationAssumption_stDev"
                            placeholder="standard deviation"
                            value={formData.inflationAssumption.stDev || ""}
                            onChange={handleChange}
                            required
                        />
                    </div>
                )}
            </div>
        );
    }

    function profileSetup(){
        return (<form onSubmit={handleSubmit} className="profileSetting"> <div className="logoLetter" style={{color: 'black',fontSize: '5vh', marginTop: "30px"}} >Scenario Setting</div>
            <div className="login"><label htmlFor="name">Scenario Name</label>
                <input
                    type="text"
                    id="name"
                    name="name"
                    value={formData.name}
                    onChange={handleChange}
                    placeholder="Scenario Name"
                    required
                /></div>
            <div className="login"><label htmlFor="maritalStatus">Are you Married? </label>
                <select name="maritalStatus" id="maritalStatus" value={formData.maritalStatus} onChange={handleChange}>
                    <option value = "Y">I am married</option>
                    <option value = "N">No, I am not</option>
                </select></div>
            <div className="login">Your Born Year<label htmlFor="birthYearUser"></label>
                <input
                    type="number"
                    name="birthYearUser"
                    id="birthYearUser"
                    placeholder="YYYY"
                    value={formData.birthYearUser}
                    onChange={handleChange}
                    max={new Date().getFullYear()}
                    required
                /></div>
            {formData.maritalStatus === "Y" && (
                <div className="login">Spouse Born Year
                    <label htmlFor="birthYearSpouse"></label>
                    <input
                        placeholder="YYYY"
                        type="number"
                        id="birthYearSpouse"
                        name="birthYearSpouse"
                        value={formData.birthYearSpouse}
                        onChange={handleChange}
                        required
                    />
                </div>
            )}
            <div className="login"><label htmlFor="lifeExpectancyUseramountOrPercent">Life Expectancy User </label>
                <select name="lifeExpectancyUser.amountOrPercent" id="lifeExpectancyUseramountOrPercent" value={formData.lifeExpectancyUser.amountOrPercent} onChange={handleChange} required>
                    <option value = "AMOUNT">Amount</option>
                    <option value = "PERCENT">Percent</option>
                </select></div>
            <div className="login"><label htmlFor="lifeExpectancyUserdistributionType">Distribution Type </label>
                <select name="lifeExpectancyUser.distributionType" id="lifeExpectancyUserdistributionType" value={formData.lifeExpectancyUser.distributionType} onChange={handleChange}>
                    <option value = "FIXED">FIXED</option>
                    <option value = "UNIFORM">UNIFORM</option>
                    <option value = "NORMAL">NORMAL</option>
                </select></div>
            {chooseMone()}
            {formData.maritalStatus === "Y" && (<div className="login"><label htmlFor="lifeExpectancySpouse">Life Expectancy Spouse: </label>
                <select name="lifeExpectancySpouse.amountOrPercent" id="lifeExpectancySpouseamountOrPercent" value={formData.lifeExpectancySpouse.amountOrPercent} onChange={handleChange} required>
                    <option value = "AMOUNT">Amount</option>
                    <option value = "PERCENT">Percent</option>
                </select></div>
            )}
            {formData.maritalStatus === "Y" && <div className="login"><label htmlFor="lifeExpectancySpousedistributionType">Distribution Type: </label>
                <select name="lifeExpectancySpouse.distributionType" id="lifeExpectancySpousedistributionType" value={formData.lifeExpectancySpouse.distributionType} onChange={handleChange}>
                    <option value = "FIXED">FIXED</option>
                    <option value = "UNIFORM">UNIFORM</option>
                    <option value = "NORMAL">NORMAL</option>
                </select></div>}
            {formData.maritalStatus === "Y" && chooseFone()}
            <div className="login"><label htmlFor="financialGoal">Financial Goal</label>
                <input type="number" name="financialGoal"  id="financialGoal"  placeholder="Financial Goal" value={formData.financialGoal} onChange={handleChange} required/></div>
            {/*<div className="login"><label htmlFor="preTaxContributionLimit"></label>*/}
            {/*    <input type="number" name="preTaxContributionLimit"  id="preTaxContributionLimit"  placeholder="pre TaxContribution Limit" value={formData.preTaxContributionLimit} onChange={handleChange} required/></div>*/}
            <div className="login"><label htmlFor="afterTaxContributionLimit">after TaxContribution Limit</label>
                <input type="number" name="afterTaxContributionLimit"  id="afterTaxContributionLimit"  placeholder="after TaxContribution Limit" value={formData.afterTaxContributionLimit} onChange={handleChange} required/></div>
            <div className="login"><label htmlFor="stateOfResidence"></label>
                {stateSelection()}</div>
            <div className="login"><label htmlFor="inflationAssumptionamountOrPercent">Inflation Assumption </label>
                <select name="inflationAssumption.amountOrPercent" id="inflationAssumptionamountOrPercent" value={formData.inflationAssumption.amountOrPercent} onChange={handleChange} required>
                    <option value = "PERCENT">Percent</option>
                </select></div>
            <div className="login"><label htmlFor="inflationAssumptiondistributionType">Distribution Type </label>
                <select name="inflationAssumption.distributionType" id="inflationAssumptiondistributionType" value={formData.inflationAssumption.distributionType} onChange={handleChange}>
                    <option value = "FIXED">FIXED</option>
                    <option value = "UNIFORM">UNIFORM</option>
                    <option value = "NORMAL">NORMAL</option>
                </select></div>
            {chooseKone()}<div>
                <button onClick={toHome} className="submitButton" type="button" style={{marginBottom:"20px"}}>Back</button><button className="submitButton" type="submit" style={{marginBottom:"20px"}}>Save Changes</button>
                </div>

        </form>);
    }

    return (<div className="total">
        <nav className="navBarTop">
            <img onClick={toHome} src ="/public/caffeineOverloadLogo.png" className = "logoSize"></img>
            <p className= "logoLetter">Life Time Financial Planner</p>
            <div></div>
        </nav>
        <nav className= "navBarSub">
            {/*<button className="commonButton" onClick={popupMenu}>Menu</button>*/}
            {/*{sideElements()}*/}
        </nav>
        {profileSetup()}
    </div>);
}
export default profileSetting;