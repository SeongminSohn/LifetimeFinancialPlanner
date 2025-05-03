import React, {useEffect, useState} from 'react';
import './common.css';
import { useNavigate } from 'react-router-dom';
import Axios from "axios"
import axios from "axios";

function investEventManagement(){
    const [loggedIn, setLoggedIn] = useState(false);
    const [openSide, setSide] = useState(false);
    const [viewedId, setViewedId] = useState(null);
    const [formData, setFormData] = useState([]);

    //check log in
    useEffect(() => {
        const token = localStorage.getItem("token");
        if (token) {
            setLoggedIn(true);
        }
    }, []);

    // get invest event Data
    useEffect(() => {
        const scenarioId = localStorage.getItem("scenario");
        if (scenarioId) {
            axios.get(`http://localhost:10000/api/invest-events/scenario/${scenarioId}`)
                .then(response => {
                    if(response.data !== undefined){
                        setFormData(response.data);
                        console.log("This data is from invest Event and Data: ", response.data);
                    }else{
                        console.log("There is no invest - event DATA")
                    }
                })
                .catch(error => {
                    console.error("Error fetching invest Event:", error);
                });
        }
    }, []);

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

    const popupMenu = () => {
        setSide(prev => !prev);
    };

    function sideElements() {
        return openSide && (
            <aside className="sidebar">
                <button onClick={toIncome}>Income Edit</button>
                <button onClick={toExpense}>Expense Edit</button>
                <button onClick={toInvest}>Invest Edit</button>
                <button onClick={toWithDrawal}>Expense Withdrawal Edit</button>
                <button onClick={toInvestEvent}>Invest Event Edit</button>
                <button onClick={toSim}>Scenario Simulation</button>
                <button disabled>Import & Export Data</button>
            </aside>
        );
    }

    function handleViewClick(id) {
        setViewedId(prev => (prev === id ? null : id));
    }

    // function handleButtonClick(item) {
    //     setSelectedInvestment(item);
    //     const savedRecord = existingInvestments.find(
    //         inv => inv.investmentTypeId === item.id
    //     );
    //     if (savedRecord) {
    //         setFormData({
    //             id: savedRecord.id,
    //             investmentTypeId: savedRecord.investmentTypeId,
    //             value: savedRecord.value,
    //             taxStatus: savedRecord.taxStatus,
    //         });
    //     } else {
    //         setFormData({
    //             id: '',
    //             investmentTypeId: item.id,
    //             value: '',
    //             taxStatus: 'NON-RETIREMENT',
    //         });
    //     }
    // }

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

    function investEventList() {
        return (
            <div className="profileSetting">
                <div style={{ display: 'flex', justifyContent: 'space-between', margin: '10px' }}>
                    <p className="logoLetter" style={{ color: 'black', fontSize: '5vh', marginTop: '30px', marginRight: '50px' }}>Investment Event</p>
                    <button onClick={toInvest} className="addButton">
                        Add Invest Event
                    </button>
                </div>

                {formData.map((item, index) => (
                    <form key={item.eventSeriesId || index} className="investment-form">
                        <div className="login">
                            <label htmlFor={`name-${index}`}>Name:</label>
                            <button
                                type="button"
                                id={`name-${index}`}
                                onClick={() => handleButtonClick(item)}>
                                {item.name}
                            </button>

                            <button
                                type="button"
                                onClick={() => handleViewClick(item.eventSeriesId)}
                                style={{ backgroundColor: 'black', color: 'white' }}>
                                {viewedId === item.eventSeriesId ? 'Hide Details' : "View Invest Event Detail"}
                            </button>

                            {viewedId === item.eventSeriesId && (
                                <div className="investment-details">
                                    <p >
                                        <strong style={{color: "darkcyan"}}>Start Year:</strong> {item.startYear.value}
                                    </p>
                                    <p style={{color: "darkcyan"}}><strong style={{color: "darkcyan"}}>Duration:</strong> {item.duration.value}</p>
                                    {/*<p>*/}
                                    {/*    <strong>Event Type:</strong> {item.eventType}*/}
                                    {/*</p>*/}
                                    <p>
                                        <strong style={{color: "darkcyan"}}>Max Cash:</strong> {item.maxCash}
                                    </p>

                                    <p style={{color: "darkcyan"}}><strong>Asset Allocations:</strong></p>
                                    <div>
                                    {item.assetAllocations.map((alloc, i) => (
                                        <p style = {{fontSize:"x-small"}} key={i}>{alloc.investmentKey}: {alloc.ratio * 100} {" %"}</p>))}
                                    </div>
                                </div>
                            )}

                            {viewedId === null && (<button
                                    type="button"
                                    style={{ backgroundColor: 'Black', color: 'White' }}
                                    onClick={() => deleteButton(item.eventSeriesId)}>
                                    Edit Investment
                                </button>)}
                        </div>
                    </form>))}

                <div>
                    <button onClick={toInvestEvent} className="commonButton">
                        Save
                    </button>
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
            {investEventList()}
        </div>
    );
}
export default investEventManagement;