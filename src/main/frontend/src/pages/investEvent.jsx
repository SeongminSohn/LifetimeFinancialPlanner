import React, { useState, useEffect } from 'react';
import './common.css';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

function InvestEventPage() {
    const [investmentTypes, setInvestmentTypes] = useState([]);
    const [existingInvestments, setExistingInvestments] = useState([]);
    const [selectedInvestment, setSelectedInvestment] = useState(null);
    const [loggedIn, setLoggedIn] = useState(false);
    const [openSide, setSide] = useState(false);
    const [formData, setFormData] = useState({
        id: "",
        scenarioId: "", //private Long scenarioId;
        name: "", //private String name;
        startYear: {
            amountOrPercent: "AMOUNT",
            distributionType: "FIXED",
            value: null,
            lower: null,
            upper: null,
            mean: null,
            stDev: null
        }, //private DistributionDTO startYear;
        duration: {
            amountOrPercent: "AMOUNT",
            distributionType: "FIXED",
            value: null,
            lower: null,
            upper: null,
            mean: null,
            stDev: null
        }, // private DistributionDTO duration;
        eventType: "", //private String eventType;                   // 'INCOME', 'EXPENSE', 'INVEST'
        assetAllocation: {

        }, //private DistributionDTO assetAllocation;
        maxCash: "" //private Double maxCash;
    });
    const navPage = useNavigate();

    useEffect(() => {
        const token = localStorage.getItem("token");
        if (token) {
            setLoggedIn(true);
        }
    }, []);

    useEffect(() => {
        const scenarioId = localStorage.getItem("scenario");
        if (scenarioId) {
            axios.get(`http://localhost:10000/api/investments/scenario/${scenarioId}`)
                .then(response => {
                    setExistingInvestments(response.data);
                    console.log("This data is from investments: ", response.data);
                })
                .catch(error => {
                    console.error("Error fetching investments:", error);
                });
        }

    }, []);

    useEffect(() => {
        const scenarioId = localStorage.getItem("scenario");
        if (scenarioId) {
            axios.get(`http://localhost:10000/api/investment-types/scenario/${scenarioId}`)
                .then(response => {
                    setInvestmentTypes(response.data);
                    console.log("This data is from investmnet type: ", response.data);
                })
                .catch(error => {
                    console.error("Error fetching investment types:", error);
                });
        }

    }, []);

    const popupMenu = () => {
        setSide(prev => !prev);
    };

    function sideElements() {
        return openSide && (
            <aside className="sidebar">
                <button onClick={toIncome}>Income Edit</button>
                <button onClick={toExpense}>Expense Edit</button>
                <button onClick={toInvest}>Invest Edit</button>
                <button onClick={toSim}>Scenario Simulation</button>
                <button>Reports & Logs</button>
                <button>Import & Export Data</button>
            </aside>
        );
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

    function handleButtonClick(item) {
        setSelectedInvestment(item);
        const savedRecord = existingInvestments.find(
            inv => inv.investmentTypeId === item.id
        );
        if (savedRecord) {
            setFormData({
                id: savedRecord.id,
                investmentTypeId: savedRecord.investmentTypeId,
                value: savedRecord.value,
                taxStatus: savedRecord.taxStatus,
            });
        } else {
            setFormData({
                id: '',
                investmentTypeId: item.id,
                value: '',
                taxStatus: 'NON-RETIREMENT',
            });
        }
    }

    async function handleSubmit(event) {
        if (formData.value === '' && formData.taxStatus === '') {
            alert("Fill out all the fields");
            return;
        }
        const scenarioId = localStorage.getItem("scenario");
        const updatedData = { ...formData, scenarioId };
        console.log("This is Updated Data: ", updatedData.investmentTypeId);
        try {
            if (formData.id) {
                const response = await axios.put(
                    `http://localhost:10000/api/investments/${formData.id}`,
                    updatedData,
                    { withCredentials: true, headers: { "Content-Type": "application/json" } }
                );
                console.log("Updated Investment:", response.data);
            } else {
                const response = await axios.post(
                    `http://localhost:10000/api/investments`, updatedData, { withCredentials: true, headers: { "Content-Type": "application/json" } });console.log("Created Investment:", response.data);
            }
            setSelectedInvestment(null);
            setFormData({
                id: '',
                investmentTypeId: '',
                value: '',
                taxStatus: 'NON-RETIREMENT',
            });

        } catch (error) {
            console.error("Submit Error:", error);
            alert("Try again");
        }
    }

    function investmentSetting() {
        return (
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
                        <label htmlFor="taxStatus">Tax Status:</label>
                        <select
                            id="taxStatus"
                            name="taxStatus"
                            value={formData.taxStatus}
                            onChange={(e) =>
                                setFormData(prev => ({ ...prev, taxStatus: e.target.value }))
                            }
                            required
                        >
                            <option value="NON-RETIREMENT">NON-RETIREMENT</option>
                            <option value="PRE-TAX">PRE-TAX</option>
                            <option value="AFTER-TAX">AFTER-TAX</option>
                        </select>
                    </div>
                    <button type="submit">Submit</button>
                    <button type="button" onClick={() => setSelectedInvestment(null)}>
                        Cancel
                    </button>
                </form>
            </div>
        );
    }

    function investmentPage() {
        return (
            <div className="profileSetting">
                <p className="logoLetter" style={{ color: 'black', fontSize: '5vh', marginTop: "30px" }}>
                    Invest Events
                </p>
                {investmentTypes.map((item, index) => (<form key={item.id || index} className="investment-form">
                        <div className="login">
                            <label htmlFor={`name-${index}`}>Name:</label>
                            <button
                                type="button"
                                id={`name-${index}`}
                                name="name"
                                onClick={() => handleButtonClick(item)}>
                                {item.name}
                            </button>
                        </div>
                    </form>))}
                {selectedInvestment && investmentSetting()}
                <div>
                    <button onClick={toInvestEvent}>Save</button>
                </div>
            </div>
        );
    }

    return (<div className="total">
            <nav className="navBarTop">
                <img onClick={toHome} src="/public/caffeineOverloadLogo.png" alt="logo" className="logoSize" />
                <p className="logoLetter">Life Time Financial Planner</p>
                <div></div>
            </nav>
            <nav className="navBarSub">
                <button className="commonButton" onClick={popupMenu}>Menu</button>
                {sideElements()}
                {loggedIn && (
                    <button className="commonButton" onClick={toProfile}>
                        Scenario Setting
                    </button>
                )}
            </nav>
            {investmentPage()}
        </div>
    );
}

export default InvestEventPage;
