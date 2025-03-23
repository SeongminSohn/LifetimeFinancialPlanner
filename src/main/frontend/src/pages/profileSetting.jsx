import React, { useState } from 'react';
import './common.css';
import {useEffect} from "react";
import axios from "axios";
import profileImage from '/public/back.jpg';
import { useNavigate } from 'react-router-dom';


function profileSetting(){

    const [openSide, setSide] = useState(false);
    const navPage = useNavigate();
    const [formData, setFormData] = useState({
        name: '',
        maritalStatus: 'N',
        birthYearUser: '',
        birthYearSpouse: 'null',
        lifeExpectancyUser: {
            amountOrPercent: "AMOUNT",
            distributionType: "FIXED",
            value: null,
            lower: null,
            upper: null,
            mean: null,
            stDev: null,
        },
        lifeExpectancySpouse: {
            amountOrPercent: "AMOUNT",
            distributionType: "FIXED",
            value: null,
            lower: null,
            upper: null,
            mean: null,
            stDev: null,
        },
        financialGoal: '',
        preTaxContributionLimit: '',
        afterTaxContributionLimit: '',
        stateOfResidence: 'AL',
        inflationAssumptionId: {
            amountOrPercent: "PERCENT",
            distributionType: "FIXED",
            value: null,
            lower: null,
            upper: null,
            mean: null,
            stDev: null,
        }
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
        navPage('/Imex')
    }

    function toHome(){
        navPage('/Homepage')
    }

    function handleChange(e) {
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


    const handleSubmit = (e) => {
        e.preventDefault();
        console.log('Name:', formData.name);
        console.log('Gender:', formData.maritalStatus);
        console.log('Email:', formData.birthYearUser);
        console.log('phone Number:', formData.birthYearSpouse);
    };

    async function saveChanges(){
        try {
            const response = await axios.post("http://localhost:10000/api/scenarios", formData);
            console.log("Scenario 생성 성공:", response.data);
        } catch (error) {
            console.error("Scenario Error:", error);
            alert("Try Again. Error.");
        }
    }

    function stateSelection() {
        return (<div className="login">
                <label htmlFor="stateOfResidence"></label>
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

    function chooseMone(){
        return <div>{formData.lifeExpectancyUser.distributionType == "FIXED" && (<input type="number" name="distributionType.FIXED"  id="distributionTypeFIXED"  placeholder="value" value={formData.lifeExpectancyUser.value} onChange={handleChange} required/>)}
            {formData.lifeExpectancyUser.distributionType == "UNIFORM" && (<div><input type="number" name="distributionType.UNIFORM"  id="distributionTypeUNIFORM"  placeholder="Lower" value={formData.lifeExpectancyUser.lower} onChange={handleChange} required/><input type="number" name="distributionType.UNIFORM"  id="distributionTypeUNIFORM"  placeholder="Upper" value={formData.lifeExpectancyUser.upper} onChange={handleChange} required/></div>)}
            {formData.lifeExpectancyUser.distributionType == "NORMAL" && (<div><input type="number" name="distributionType.NORMAL"  id="distributionTypeNORMAL"  placeholder="mean" value={formData.lifeExpectancyUser.mean} onChange={handleChange} required/><input type="number" name="distributionType.NORMAL"  id="distributionTypeNORMAL"  placeholder="standard deviation" value={formData.lifeExpectancyUser.stDev} onChange={handleChange} required/></div>)}</div>
    }
    function chooseFone(){
        return <div>{formData.lifeExpectancySpouse.distributionType == "FIXED" && (<input type="number" name="distributionType.SpouseFIXED"  id="distributionTypeSpouseFIXED"  placeholder="value" value={formData.lifeExpectancySpouse.value} onChange={handleChange} required/>)}
            {formData.lifeExpectancySpouse.distributionType == "UNIFORM" && (<div><input type="number" name="distributionType.SpouseUNIFORM"  id="distributionTypeSpouseUNIFORM"  placeholder="Lower" value={formData.lifeExpectancySpouse.lower} onChange={handleChange} required/><input type="number" name="distributionType.SpouseUNIFORM"  id="distributionTypeSpouseUNIFORM"  placeholder="Upper" value={formData.lifeExpectancySpouse.upper} onChange={handleChange} required/></div>)}
            {formData.lifeExpectancySpouse.distributionType == "NORMAL" && (<div><input type="number" name="distributionType.SpouseNORMAL"  id="distributionTypeSpouseNORMAL"  placeholder="mean" value={formData.lifeExpectancySpouse.mean} onChange={handleChange} required/><input type="number" name="distributionType.SpouseNORMAL"  id="distributionTypeSpouseNORMAL"  placeholder="standard deviation" value={formData.lifeExpectancySpouse.stDev} onChange={handleChange} required/></div>)}</div>
    }

    function chooseKone(){
        return <div>{formData.inflationAssumptionId.distributionType == "FIXED" && (<input type="number" name="distributionType.inflationAssumptionIdFIXED"  id="distributionTypeinflationAssumptionIdFIXED"  placeholder="value" value={formData.inflationAssumptionId.value} onChange={handleChange} required/>)}
            {formData.inflationAssumptionId.distributionType == "UNIFORM" && (<div><input type="number" name="distributionType.inflationAssumptionIdUNIFORM"  id="distributionTypeinflationAssumptionIdUNIFORM"  placeholder="Lower" value={formData.inflationAssumptionId.lower} onChange={handleChange} required/><input type="number" name="distributionType.inflationAssumptionIdUNIFORM"  id="distributionTypeinflationAssumptionIdeUNIFORM"  placeholder="Upper" value={formData.inflationAssumptionId.upper} onChange={handleChange} required/></div>)}
            {formData.inflationAssumptionId.distributionType == "NORMAL" && (<div><input type="number" name="distributionType.inflationAssumptionIdNORMAL"  id="distributionTypeinflationAssumptionIdNORMAL"  placeholder="mean" value={formData.inflationAssumptionId.mean} onChange={handleChange} required/><input type="number" name="distributionType.inflationAssumptionIdNORMAL"  id="distributionTypeinflationAssumptionIdNORMAL"  placeholder="standard deviation" value={formData.inflationAssumptionId.stDev} onChange={handleChange} required/></div>)}</div>
    }

    function profileSetup(){
        return (<form onSubmit={handleSubmit} className="profileSetting"> <div className="logoLetter" style={{fontSize: '50px', marginTop: "30px"}} >Scenario Setting</div>
            <div className="login"><label htmlFor="name"></label>
                <input
                    type="text"
                    id="name"
                    name="name"
                    value={formData.name}
                    onChange={handleChange}
                    placeholder="Edit your Name"
                    required
                /></div>
            <div className="login"><label htmlFor="maritalStatus"></label>
                <select name="maritalStatus" id="maritalStatus" value={formData.maritalStatus} onChange={handleChange}>
                    <option value = "Y">I am married</option>
                    <option value = "N">No, I am not</option>
                </select></div>
            <div className="login"><label htmlFor="birthYearUser"></label>
                <input type="number" name="birthYearUser"  id="birthYearUser"  placeholder="YYYY" value={formData.birthYearUser} onChange={handleChange} maxLength={4} required/></div>
            {formData.maritalStatus === "Y" && (
                <div className="login">
                    <label htmlFor="birthYearSpouse"></label>
                    <input
                        placeholder="birthYear spouse"
                        type="number"
                        id="birthYearSpouse"
                        name="birthYearSpouse"
                        value={formData.birthYearSpouse}
                        onChange={handleChange}
                        required
                    />
                </div>
            )}
            <div className="login"><label htmlFor="lifeExpectancyUseramountOrPercent">Life Expectancy User: </label>
                <select name="lifeExpectancyUser.amountOrPercent" id="lifeExpectancyUseramountOrPercent" value={formData.lifeExpectancyUser.amountOrPercent} onChange={handleChange} required>
                    <option value = "AMOUNT">Amount</option>
                    <option value = "PERCENT">Percent</option>
                </select></div>
            <div className="login"><label htmlFor="lifeExpectancyUserdistributionType">Distribution Type: </label>
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
            <div className="login"><label htmlFor="financialGoal"></label>
                <input type="number" name="financialGoal"  id="financialGoal"  placeholder="Financial Goal" value={formData.financialGoal} onChange={handleChange} required/></div>
            <div className="login"><label htmlFor="preTaxContributionLimit"></label>
                <input type="number" name="preTaxContributionLimit"  id="preTaxContributionLimit"  placeholder="pre TaxContribution Limit" value={formData.preTaxContributionLimit} onChange={handleChange} required/></div>
            <div className="login"><label htmlFor="afterTaxContributionLimit"></label>
                <input type="number" name="afterTaxContributionLimit"  id="afterTaxContributionLimit"  placeholder="after TaxContribution Limit" value={formData.afterTaxContributionLimit} onChange={handleChange} required/></div>
            <div className="login"><label htmlFor="stateOfResidence"></label>
                {stateSelection()}</div>
            <div className="login"><label htmlFor="inflationAssumptionIdamountOrPercent">inflation Assumption Id: </label>
                <select name="inflationAssumptionId.amountOrPercent" id="inflationAssumptionIdamountOrPercent" value={formData.inflationAssumptionId.amountOrPercent} onChange={handleChange} required>
                    <option value = "PERCENT">Percent</option>
                </select></div>
            <div className="login"><label htmlFor="inflationAssumptionIddistributionType">Distribution Type: </label>
                <select name="inflationAssumptionId.distributionType" id="inflationAssumptionIddistributionType" value={formData.inflationAssumptionId.distributionType} onChange={handleChange}>
                    <option value = "FIXED">FIXED</option>
                    <option value = "UNIFORM">UNIFORM</option>
                    <option value = "NORMAL">NORMAL</option>
                </select></div>
            {chooseKone()}
            <button className="submitButton" type="submit" style={{marginBottom:"20px"}} onClick={saveChanges}>Save Changes</button>
        </form>);
    }

    return (<div className="total">
        <nav className="navBarTop">
            <img onClick={toHome} src ="/public/caffeineOverloadLogo.png" className = "logoSize"></img>
            <p className= "logoLetter">Life Time Financial Planner</p>
            <button className="commonButton">About us</button>
        </nav>
        <nav className= "navBarSub">
            <button className="commonButton" onClick={popupMenu}>Menu</button>
            {sideElements()}
        </nav>
        {profileSetup()}
    </div>);
}
export default profileSetting;