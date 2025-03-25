import React, { useState } from 'react';
import './common.css';
import {useEffect} from "react";
import axios from "axios";
import { useNavigate } from 'react-router-dom';

function investment(){
    const [investEvents, setInvestEvents] = useState([]);
    const [selectedInvestment, setSelectedInvestment] = useState(null);
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
                    console.log(response.data)
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
        investmentTypeId: '',
        value: '',
        taxStatus: ''
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
        const updatedFormData = {
            ...formData,
            scenarioId: localStorage.getItem("scenario"),
            investmentTypeId: selectedInvestment.id
        };
        console.log(updatedFormData);
        try {
            const response = await axios.post(
                "http://localhost:10000/api/investments",
                updatedFormData,
                { withCredentials: true, headers: { "Content-Type": "application/json" } }
            );
            console.log("Scenario ID:", response.data);
            setInvestEvents(prev =>
                prev.map(item =>
                    item.id === selectedInvestment.id
                        ? { ...item, submitted: true }
                        : item
                )
            );
            setSelectedInvestment(null);
            setFormData({ investmentTypeId: '', value: '', taxStatus: '' });
            navPage('/Investment');
        } catch (error) {
            console.error("Scenario Error:", error);
            alert("Try again");
        }
    }

    // const handleChange = (e) => {
    //     const { name, value } = e.target;
    //     if (name === "userPercentage") {
    //         let numericValue = parseFloat(value);
    //         if (isNaN(numericValue)) {
    //             numericValue = 0;
    //         }
    //         if (numericValue < 0) numericValue = 0;
    //         if (numericValue > 1) numericValue = 1;
    //
    //         setFormData(prev => ({
    //             ...prev,
    //             userPercentage: numericValue,
    //         }));
    //         return;
    //     }
    //     if (name.includes('.')) {
    //         const [parentKey, childKey] = name.split('.');
    //         setFormData(prevState => ({
    //             ...prevState,
    //             [parentKey]: {
    //                 ...prevState[parentKey],
    //                 [childKey]: value
    //             }
    //         }));
    //     } else {
    //         setFormData(prevState => ({
    //             ...prevState,
    //             [name]: value
    //         }));
    //     }
    // }

    function handleButtonClick(item) {
        setSelectedInvestment(item);
        setFormData({ investmentTypeId: '', value: '', taxStatus: 'NON-RETIREMENT' });
    }

    function investmentSetting(){
        return (<div>
            <div>
                <h2>Update Investment for {selectedInvestment.name}</h2>
                <form onSubmit={handleSubmit}>
                    <div className="login">
                        <label htmlFor="value">Value:</label>
                        <input
                            type="number"
                            id="value"
                            name="value"
                            value={formData.value}
                            onChange={(e) =>
                                setFormData(prev => ({ ...prev, value: e.target.value }))
                            }
                            required
                        />
                    </div>
                    <div className="login">
                        <label htmlFor="taxStatus">Tax Status: </label>
                        <select name="taxStatus" id="taxStatus" value={formData.taxStatus} onChange={(e) =>
                            setFormData(prev => ({ ...prev, status: e.target.value }))} required>
                            <option value = "NON-RETIREMENT">NON-RETIREMENT</option>
                            <option value = "PRE-TAX">PRE-TAX</option>
                            <option value = "AFTER-TAX">AFTER-TAX</option>
                        </select>
                    </div>
                    <button type="submit">Submit</button>
                    <button type="button" onClick={() => setSelectedInvestment(null)}>
                        Cancel
                    </button>
                </form>
            </div>
        </div>);
    }

    function investMentPage(){
        return (
            <div className="profileSetting">
                <p className="logoLetter" style={{color: 'black',fontSize: '5vh', marginTop: "30px"}} >Investment Types</p>
                {investEvents.map((item, index) => (
                    <form key={item.id || index} className="investment-form">
                        <div className="login">
                            <label htmlFor={`name-${index}`}>Name: </label>
                            <button
                                onClick={() => handleButtonClick(item)}
                                type="button"
                                id={`name-${index}`}
                                name="name"
                            >{item.name}</button>
                        </div>
                    </form>
                ))}
                {selectedInvestment && (investmentSetting())}
            </div>
        );
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
