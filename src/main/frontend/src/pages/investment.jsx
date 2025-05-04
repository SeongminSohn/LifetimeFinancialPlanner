import React, { useState, useEffect } from 'react';
import './common.css';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

function Investment() {
    const [investmentTypes, setInvestmentTypes] = useState([]);
    const [existingInvestments, setExistingInvestments] = useState([]);
    const [selectedInvestment, setSelectedInvestment] = useState(null);
    const [loggedIn, setLoggedIn] = useState(false);
    const [openSide, setSide] = useState(false);
    const [viewedId, setViewedId] = useState(null);

    const [formData, setFormData] = useState({
        id: '',
        investmentTypeId: '',
        value: '',
        taxStatus: 'NON-RETIREMENT',
    });

    const navPage = useNavigate();
    //check login
    useEffect(() => {
        const token = localStorage.getItem("token");
        if (token) {
            setLoggedIn(true);
        }
    }, []);

    //get investment Data
    useEffect(() => {
        const scenarioId = localStorage.getItem("scenario");
        if (scenarioId) {
            axios.get(`http://localhost:10000/api/investments/scenario/${scenarioId}`)
                .then(response => {
                    console.log("Existing Investments:", response.data);
                    setExistingInvestments(response.data);
                })
                .catch(error => {
                    console.error("Error fetching investments:", error);
                });
        }
    }, []);

    //get investment Type Data
    useEffect(() => {
        const scenarioId = localStorage.getItem("scenario");
        if (scenarioId) {
            axios.get(`http://localhost:10000/api/investment-types/scenario/${scenarioId}`)
                .then(response => {
                    console.log("Investment Types:", response.data);
                    setInvestmentTypes(response.data);
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
                <button onClick={() => navPage('/IncomeSetting')}>View Income Status</button>
                <button onClick={() => navPage('/ExpenseSetting')}>view Expense Status</button>
                <button onClick={() => navPage('/ExpenseW')}>Expense Withdrawal Edit</button>
                <button onClick={() => navPage('/SimulationManagement')}>Invest Event Edit</button>
                <button onClick={() => navPage('/simulationPage')}>Scenario Simulation</button>
                <button onClick={() => navPage('/ImportExp')}>Import & Export Data</button>
            </aside>
        );
    }

    function toUserGuide(){
        navPage("/UserGuide")
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

    async function deleteButton(investmentTypeId) {
        const existingRecord = existingInvestments.find(
            item => item.investmentTypeId === investmentTypeId
        );
        if (!existingRecord) {
            alert("Data does not exist");
            return;
        }
        try {
            await axios.delete(
                `http://localhost:10000/api/investments/${existingRecord.id}`,
                { withCredentials: true }
            );
            console.log("Deleted investment:", existingRecord);
            setExistingInvestments(prev =>
                prev.filter(item => item.id !== existingRecord.id)
            );
            if (selectedInvestment && selectedInvestment.id === investmentTypeId) {
                setFormData({
                    id: '',
                    investmentTypeId: investmentTypeId,
                    value: '',
                    taxStatus: 'NON-RETIREMENT',
                });
            }
        } catch (error) {
            console.error("Error deleting investment:", error);
            alert("Fail to Delete");
        }
    }

    function handleViewClick(id) {
        setViewedId(prev => (prev === id ? null : id));
    }

    function investEvents() {
        return (
            <div className="profileSetting">
                <div style={{ display: 'flex', flexDirection: 'row', justifyContent: 'space-between', margin:'10px'}}><p className="logoLetter" style={{ color: 'black', fontSize: '5vh', marginTop: "30px", marginRight: '50px'}}>Investments</p><button onClick={toInvest} className="addButton">Add Investment Type</button></div>
                {investmentTypes.map((item, index) => (
                    <form key={item.id || index} className="investment-form">
                        <div className="login">
                            <label htmlFor={`name-${index}`}>Name:</label>
                            <button type="button" id={`name-${index}`} name="name" onClick={() => handleButtonClick(item)}>{item.name}
                            </button>
                            <button
                                type = "button"
                                onClick={() => handleViewClick(item.id)}
                                style={{ backgroundColor: "black", color: "white" }}>
                                {viewedId === item.id ? "Hide Details" : "VIEW INVESTMENT TYPE"}
                            </button>

                            {viewedId === item.id && (
                                <div className="investment-details">
                                    <p><strong>Description:</strong> {item.description}</p>
                                    {item.expectedAnnualReturn.value !== null && (<p><strong>Expected Annual Return value:</strong> {item.expectedAnnualReturn.value}</p>)}
                                    {item.expectedAnnualReturn.lower !== null && (<p><strong>Expected Annual Return lower value:</strong> {item.expectedAnnualReturn.lower}</p>)}
                                    {item.expectedAnnualReturn.upper !== null && (<p><strong>Expected Annual Return upper value:</strong> {item.expectedAnnualReturn.upper}</p>)}
                                    {item.expectedAnnualReturn.mean !== null && (<p><strong>Expected Annual Return mean:</strong> {item.expectedAnnualReturn.mean}</p>)}
                                    {item.expectedAnnualReturn.stDev !== null && (<p><strong>Expected Annual Return standard deviation:</strong> {item.expectedAnnualReturn.stDev}</p>)}
                                    <p><strong>Expense Ratio:</strong> {item.expenseRatio}</p>
                                    {item.expectedAnnualIncome.value !== null && (<p><strong>Expected Annual Income value:</strong> {item.expectedAnnualIncome.value}</p>)}
                                    {item.expectedAnnualIncome.lower !== null && (<p><strong>Expected Annual Income lower value:</strong> {item.expectedAnnualIncome.lower}</p>)}
                                    {item.expectedAnnualIncome.upper !== null && (<p><strong>Expected Annual Income upper value:</strong> {item.expectedAnnualIncome.upper}</p>)}
                                    {item.expectedAnnualIncome.mean !== null && (<p><strong>Expected Annual Income mean:</strong> {item.expectedAnnualIncome.mean}</p>)}
                                    {item.expectedAnnualIncome.stDev !== null && (<p><strong>Expected Annual Income standard deviation:</strong> {item.expectedAnnualIncome.stDev}</p>)}
                                    <p><strong>Tax ability:</strong> {item.taxability}</p>
                                </div>)}

                            {viewedId === null && (<button type="button" style={{ backgroundColor: "Black", color: "White" }}  onClick={() => navPage(`/investment-types/edit/${item.id}`)}>
                                Edit Investment type
                            </button>)}
                        </div>
                    </form>
                ))}
                {selectedInvestment && investmentSetting()}
                <div>
                    <button onClick={toInvestEvent}>Save</button>
                </div>
            </div>
        );
    }

    return (
        <div className="total">
            <nav className="navBarTop">
                <img onClick={toHome} src="/public/caffeineOverloadLogo.png" alt="logo" className="logoSize" />
                <p className="logoLetter">Life Time Financial Planner</p>
                <button onClick={toUserGuide}>User Guide</button>
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
            {investEvents()}
        </div>
    );
}

export default Investment;
