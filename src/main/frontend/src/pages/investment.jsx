import React, { useState } from 'react';
import './common.css';
import {useEffect} from "react";
import axios from "axios";
import { useNavigate } from 'react-router-dom';

function investment(){
    const [investEvents, setInvestEvents] = useState([]);
    useEffect(() => {
        const token = localStorage.getItem("token");
        if (token) {
            setLoggedIn(true);
        }
    }, []);
    useEffect(() => {
        const scenarioId = localStorage.getItem("scenario");
        if (scenarioId) {
            axios.get(`http://localhost:10000/api/investment-types/scenario/${scenarioId}`)
                .then(response => {
                    setInvestEvents(response.data);
                    console.log(investEvents)
                })
                .catch(error => {
                    console.error("Error fetching invest events:", error);
                });
        }
    }, []);

    const [openSide, setSide] = useState(false);
    const navPage = useNavigate();
    const [loggedIn, setLoggedIn] = useState(false)
    const [formData, setFormData] = useState({

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
        } catch (error) {
            console.error("Scenario Error:", error);
            alert("Try again");
        }
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

    // function chooseMone() {
    //     return (
    //         <div>
    //             {formData.expectedAnnualReturn.distributionType === "FIXED" && (
    //                 <input
    //                     type="number"
    //                     name="expectedAnnualReturn.value"
    //                     id="expectedAnnualReturn.FIXED"
    //                     placeholder="value"
    //                     value={formData.expectedAnnualReturn.value || ""}
    //                     onChange={handleChange}
    //                     required
    //                 />
    //             )}
    //             {formData.expectedAnnualReturn.distributionType === "UNIFORM" && (
    //                 <div>
    //                     <input
    //                         type="number"
    //                         name="expectedAnnualReturn.lower"
    //                         id="expectedAnnualReturn.LOWER"
    //                         placeholder="Lower"
    //                         value={formData.expectedAnnualReturn.lower || ""}
    //                         onChange={handleChange}
    //                         required
    //                     />
    //                     <input
    //                         type="number"
    //                         name="expectedAnnualReturn.upper"
    //                         id="expectedAnnualReturn.UPPER"
    //                         placeholder="Upper"
    //                         value={formData.expectedAnnualReturn.upper || ""}
    //                         onChange={handleChange}
    //                         required
    //                     />
    //                 </div>
    //             )}
    //             {formData.expectedAnnualReturn.distributionType === "NORMAL" && (
    //                 <div>
    //                     <input
    //                         type="number"
    //                         name="expectedAnnualReturn.mean"
    //                         id="expectedAnnualReturn.MEAN"
    //                         placeholder="mean"
    //                         value={formData.expectedAnnualReturn.mean || ""}
    //                         onChange={handleChange}
    //                         required
    //                     />
    //                     <input
    //                         type="number"
    //                         name="expectedAnnualReturn.stDev"
    //                         id="expectedAnnualReturn.STDEV"
    //                         placeholder="standard deviation"
    //                         value={formData.expectedAnnualReturn.stDev || ""}
    //                         onChange={handleChange}
    //                         required
    //                     />
    //                 </div>
    //             )}
    //         </div>
    //     );
    // }
    //
    // function chooseKone() {
    //     return (
    //         <div>
    //             {formData.expectedAnnualIncome.distributionType === "FIXED" && (
    //                 <input
    //                     type="number"
    //                     name="expectedAnnualIncome.value"
    //                     id="expectedAnnualIncome.FIXED"
    //                     placeholder="value"
    //                     value={formData.expectedAnnualIncome.value || ""}
    //                     onChange={handleChange}
    //                     required
    //                 />
    //             )}
    //             {formData.expectedAnnualIncome.distributionType === "UNIFORM" && (
    //                 <div>
    //                     <input
    //                         type="number"
    //                         name="expectedAnnualIncome.lower"
    //                         id="expectedAnnualIncome.LOWER"
    //                         placeholder="Lower"
    //                         value={formData.expectedAnnualIncome.lower || ""}
    //                         onChange={handleChange}
    //                         required
    //                     />
    //                     <input
    //                         type="number"
    //                         name="expectedAnnualIncome.upper"
    //                         id="expectedAnnualIncome.UPPER"
    //                         placeholder="Upper"
    //                         value={formData.expectedAnnualIncome.upper || ""}
    //                         onChange={handleChange}
    //                         required
    //                     />
    //                 </div>
    //             )}
    //             {formData.expectedAnnualIncome.distributionType === "NORMAL" && (
    //                 <div>
    //                     <input
    //                         type="number"
    //                         name="expectedAnnualIncome.mean"
    //                         id="expectedAnnualIncome.MEAN"
    //                         placeholder="mean"
    //                         value={formData.expectedAnnualIncome.mean || ""}
    //                         onChange={handleChange}
    //                         required
    //                     />
    //                     <input
    //                         type="number"
    //                         name="expectedAnnualIncome.stDev"
    //                         id="expectedAnnualIncome.STDEV"
    //                         placeholder="standard deviation"
    //                         value={formData.expectedAnnualIncome.stDev || ""}
    //                         onChange={handleChange}
    //                         required
    //                     />
    //                 </div>
    //             )}
    //         </div>
    //     );
    // }

    function investMentPage(){
        return (<div></div>);
    }

    function investMentPage(){
        return (<form onSubmit={handleSubmit} className="profileSetting">
            <div className="logoLetter" style={{color: 'black', fontSize: '5vh', marginTop: "30px"}}>Edit Investment</div>

            <div>
                <button onClick={toInvest} className="submitButton" type="button" style={{marginBottom:"20px"}}>Back</button><button className="submitButton" type="submit">Save Changes</button></div>
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
        {investMentPage()}
    </div>);
}
export default investment;
