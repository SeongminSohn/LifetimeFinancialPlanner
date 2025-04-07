import React, { useState, useEffect } from 'react';
import './common.css';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

function ExpenseWithdrawlPage(){
    const [investmentTypes, setInvestmentTypes] = useState([]);
    const [existingInvestments, setExistingInvestments] = useState([]);
    const [selectedInvestment, setSelectedInvestment] = useState(null);
    const [loggedIn, setLoggedIn] = useState(false);
    const [openSide, setSide] = useState(false);
    const [formData, setFormData] = useState({
        id: '',//private Long id;
        scenarioId: '',//private Long scenarioId;
        sellingOrder: ''//private String sellingOrder;
    });
    const navPage = useNavigate();
    const [clickedItems, setClickedItems] = useState([]);


    // Check Log in or not
    useEffect(() => {
        const token = localStorage.getItem("token");
        if (token) {
            setLoggedIn(true);
        }
    }, []);
    // Get the data from Investment
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
    // Get data from Invest-type
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
    useEffect(() => {
        console.log("In the Array: ", clickedItems);
    }, [clickedItems]);

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
    function toggleClickedItem(item, index) {
        const matchedType = investmentTypes.find(type => type.id === item.investmentTypeId);
        if (!matchedType) return;
        const combinedString = `${matchedType.name} ${item.taxStatus} (${index})`;
        setClickedItems(prev => {
            if (prev.includes(combinedString)) {
                return prev.filter(str => str !== combinedString);
            } else {
                return [...prev, combinedString];}
        });
    };


    const renderClickedItems = () => (
        <div className="total" style={{ marginTop: '20px' }}>
            <p>Orders: </p>
            {clickedItems.map((item, idx) => (
                <button
                    key={idx}
                    onClick={() => setClickedItems(prev => prev.filter((_, i) => i !== idx))}
                    style={{ marginRight: '5px' }}>
                    {item}</button>
            ))}
        </div>
    );


    function expenseComponents(){
        return (<div className="profileSetting">
                <p className="logoLetter" style={{ color: 'black', fontSize: '5vh', marginTop: "30px" }}>
                    Expense WithDrawl. Choose Order.
                </p>{existingInvestments.map((item, index) => (
                <form key={item.investmentTypeId || index} className="investment-form">
                    <div className="login">
                        <label htmlFor={`name-${index}`}>Status</label>
                        <button
                            type="button"
                            id={`name-${index}`}
                            name="name"
                            onClick={() => handleButtonClick(item)}>
                            {(() => {const matchedType = investmentTypes.find(type => type.id === item.investmentTypeId);
                                return matchedType ? <span>{matchedType.name}</span> : null;
                            })()}
                            {item.taxStatus}
                        </button>
                        <button
                            type="button"
                            onClick={() => toggleClickedItem(item, index)}
                        >
                            {index}
                        </button>
                    </div>
                </form>
            ))}


                {selectedInvestment && investmentSetting()}
                <div>
                    <button onClick={toInvestEvent}>Save</button>
                </div>
                {renderClickedItems()}
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
                    <button className="commonButton" onClick={toProfile}>Scenario Setting
                    </button>
                )}
            </nav>
            {expenseComponents()}
        </div>);
}
export default ExpenseWithdrawlPage