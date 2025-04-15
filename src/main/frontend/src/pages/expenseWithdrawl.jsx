import React, { useState, useEffect } from 'react';
import './common.css';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

function ExpenseWithdrawlPage() {
    const [investmentTypes, setInvestmentTypes] = useState([]);
    const [existingInvestments, setExistingInvestments] = useState([]);
    const [selectedInvestment, setSelectedInvestment] = useState(null);
    const [loggedIn, setLoggedIn] = useState(false);
    const [openSide, setSide] = useState(false);
    const [formData, setFormData] = useState({
        scenarioId: '',
        sellingOrder: ""
    });
    const [clickedItems, setClickedItems] = useState([]);
    const navPage = useNavigate();

    useEffect(() => {
        const token = localStorage.getItem("token");
        if (token) {
            setLoggedIn(true);
        }
    }, []);

    async function postArray() {
        console.log("FormData length: ", clickedItems.length);
        if (clickedItems.length < existingInvestments.length) {
            alert("Put all elements into the array!");
            return;
        }
        const scenarioId = localStorage.getItem("scenario");
        const updatedFormData = {
            ...formData,
            scenarioId: scenarioId,
            sellingOrder: clickedItems
        };
        console.log(updatedFormData);
        try {
            if (updatedFormData.id) {
                const response = await axios.put(
                    `http://localhost:10000/api/expense-withdrawal-strategies/${updatedFormData.id}`,
                    updatedFormData,
                    { withCredentials: true, headers: { "Content-Type": "application/json" } }
                );
                console.log("Updated Investment:", response.data);
            } else {
                const response = await axios.post(
                    "http://localhost:10000/api/expense-withdrawal-strategies",
                    updatedFormData,
                    { withCredentials: true, headers: { "Content-Type": "application/json" } }
                );
                console.log("Expense withdrawal strategy saved:", response.data);
            }
        } catch (error) {
            console.error("Error saving expense withdrawal strategy:", error);
        }
    }


    useEffect(() => {
        const scenarioId = localStorage.getItem("scenario");
        if (scenarioId) {
            axios.get(`http://localhost:10000/api/investments/scenario/${scenarioId}`)
                .then(response => {
                    setExistingInvestments(response.data);
                })
                .catch(error => {
                    console.error("Error fetching investments:", error);
                });
        }
    }, []);

    useEffect(() => {
        const scenarioId = localStorage.getItem("scenario");
        if (scenarioId) {
            axios.get(`http://localhost:10000/api/investments/scenario/${scenarioId}`)
                .then(response => {
                    setExistingInvestments(response.data);
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
                })
                .catch(error => {
                    console.error("Error fetching investment types:", error);
                });
        }
    }, []);

    useEffect(() => {
        console.log("clickedItems updated:", clickedItems);
    }, [clickedItems]);

    useEffect(() => {
        const scenarioId = localStorage.getItem("scenario");
        if (scenarioId) {
            axios.get(`http://localhost:10000/api/expense-withdrawal-strategies/1`)
                .then(response => {
                    setFormData(response.data);
                    console.log("This is Expense-withDrawlData: ", response.data)
                    console.log("TEST: ", response.data.sellingOrder)
                    setClickedItems(response.data.sellingOrder)
                })
                .catch(error => {
                    console.error("Error fetching expense-withdrawl-strategies:", error);
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
                {/*<button onClick={toInvest}>Invest Edit</button>*/}
                <button onClick={toInvestment}>Investment</button>
                <button onClick={toInvestEvent}>Invest Event Edit</button>
                <button onClick={toSim} disabled>Scenario Simulation</button>
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

    function handleButtonClick(item) {
        setSelectedInvestment(item);
        const savedRecord = existingInvestments.find(inv => inv.investmentTypeId === item.id);
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

    const toggleClickedItem = (item) => {
        const matchedType = investmentTypes.find(type => type.id === item.investmentTypeId);
        if (!matchedType) return;
        const label = `${matchedType.name} ${item.taxStatus}`;
        setClickedItems(prev => {
            if (prev.includes(label)) {
                return prev.filter(currentLabel => currentLabel !== label);
            } else {
                return [...prev, label];
            }
        });
    };



    const renderClickedItems = () => (
        <div className="forOrdering">
            <p>Orders : </p>
            {clickedItems.map((label, index) => (
                <div key={index} className="arrays">{label}</div>
            ))}
        </div>
    );


    function expenseComponents() {
        return (
            <div className="profileSetting">
                <p className="logoLetter" style={{ color: 'black', fontSize: '5vh', marginTop: "30px" }}>
                    Expense WithDrawl. Choose Order.
                </p>
                {existingInvestments.map((item, index) => (
                    <form key={item.investmentTypeId || index} className="investment-form">
                        <div className="login">
                            <label htmlFor={`name-${index}`}>Status</label>
                            <button
                                type="button"
                                id={`name-${index}`}
                                name="name"
                                onClick={() => handleButtonClick(item)}>
                                {(() => {
                                    const matchedType = investmentTypes.find(type => type.id === item.investmentTypeId);
                                    return matchedType ? <span>{matchedType.name}</span> : null;
                                })()}
                                {item.taxStatus}
                            </button>
                            <button type="button" onClick={() => toggleClickedItem(item)}>Add or Remove</button>
                        </div>
                    </form>
                ))}
                {selectedInvestment && null}
                <div>
                    <button onClick={postArray}>Save</button>
                </div>
                {renderClickedItems()}
            </div>
        );
    }

    return (
        <div className="total">
            <nav className="navBarTop">
                <img onClick={toHome} src="/public/caffeineOverloadLogo.png" alt="logo" className="logoSize" />
                <p className="logoLetter">Life Time Financial Planner</p>
                <div></div>
            </nav>
            <nav className="navBarSub">
                <button className="commonButton" onClick={popupMenu}>Menu</button>
                {sideElements()}
                {loggedIn && (
                    <button className="commonButton" onClick={toProfile}>Scenario Setting</button>
                )}
            </nav>
            {expenseComponents()}
        </div>
    );
}

export default ExpenseWithdrawlPage;
