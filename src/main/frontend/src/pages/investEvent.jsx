import React, { useState, useEffect } from 'react';
import './common.css';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

function InvestEventPage() {
    const [investmentTypes, setInvestmentTypes] = useState([]);
    const [existingInvestments, setExistingInvestments] = useState([]);
    const [investEvents, setInvestEvents] = useState([]);
    const [selectedInvestment, setSelectedInvestment] = useState(null);
    const [loggedIn, setLoggedIn] = useState(false);
    const [openSide, setSide] = useState(false);
    const [allocationValues, setAllocationValues] = useState({});
    const [formData, setFormData] = useState({
        scenarioId: "", //private Long scenarioId;
        // eventSeriesId: "", //private Long eventSeriesId;
        // investmentId: '', //private Long investmentId;
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
        assetAllocations: [], //private List<AllocationDTO> assetAllocations;
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
                    console.error("Error fetching invest Event:", error);
                });
        }

    }, []);

    useEffect(() => {
        if (formData.assetAllocations && Array.isArray(formData.assetAllocations)) {
            const newAllocationValues = {};
            formData.assetAllocations.forEach(allocation => {
                newAllocationValues[allocation.investmentKey] = allocation.ratio;
            });
            setAllocationValues(newAllocationValues);
        }
    }, [formData.assetAllocations]);





    // useEffect(() => {
    //     const scenarioId = localStorage.getItem("scenario");
    //     if (scenarioId) {
    //         axios.get(`http://localhost:10000/api/invest-events/scenario/${scenarioId}`)
    //             .then(response => {
    //                 setFormData(response.data);
    //                 console.log("This data is from invest Event and Data: ", response.data);
    //             })
    //             .catch(error => {
    //                 console.error("Error fetching invest Event:", error);
    //             });
    //     }
    // }, []);

    useEffect(() => {
        const scenarioId = localStorage.getItem("scenario");
        if (scenarioId) {
            axios.get(`http://localhost:10000/api/invest-events/scenario/${scenarioId}`)
                .then(response => {
                    if(response.data[0] !== undefined){
                        setFormData(response.data[0]);
                        console.log("This data is from invest Event and Data: ", response.data[0]);
                    }else{
                        console.log("There is no DATA")
                    }
                })
                .catch(error => {
                    console.error("Error fetching invest Event:", error);
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

    //If there is any change in array
    useEffect(() => {
        if (formData.assetAllocations && Array.isArray(formData.assetAllocations)) {
            const newAllocationValues = {};
            formData.assetAllocations.forEach(allocation => {
                newAllocationValues[allocation.investmentKey] = allocation.ratio;
            });
            console.log("New Allocation Values (before state update): ", newAllocationValues);
            setAllocationValues(newAllocationValues);
        }
    }, [formData.assetAllocations]);

    const popupMenu = () => {
        setSide(prev => !prev);
    };

    function sideElements() {
        return openSide && (
            <aside className="sidebar">
                <button onClick={toInvestment}>Investment</button>
                <button onClick={toIncome}>Income Edit</button>
                <button onClick={toExpense}>Expense Edit</button>
                {/*<button onClick={toInvest}>Invest Edit</button>*/}
                <button onClick={toWithDrawal}>Expense Withdrawal Edit</button>
                <button onClick={toInvestEvent}>Invest Event Edit</button>
                <button onClick={toSim} disabled>Scenario Simulation</button>
                <button>Import & Export Data</button>
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
        if (name === "startYear") {
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

    function chooseMone() {
        return (
            <div>
                {formData.startYear.distributionType === "FIXED" && (
                    <input
                        type="number"
                        name="startYear.value"
                        id="startYear.FIXED"
                        placeholder="value"
                        value={formData.startYear.value || ""}
                        onChange={handleChange}
                        required
                    />
                )}
                {formData.startYear.distributionType === "UNIFORM" && (
                    <div>
                        <input
                            type="number"
                            name="startYear.lower"
                            id="startYear.LOWER"
                            placeholder="Lower"
                            value={formData.startYear.lower || ""}
                            onChange={handleChange}
                            required
                        />
                        <input
                            type="number"
                            name="startYear.upper"
                            id="startYear.UPPER"
                            placeholder="Upper"
                            value={formData.startYear.upper || ""}
                            onChange={handleChange}
                            required
                        />
                    </div>
                )}
                {formData.startYear.distributionType === "NORMAL" && (
                    <div>
                        <input
                            type="number"
                            name="startYear.mean"
                            id="startYear.MEAN"
                            placeholder="mean"
                            value={formData.startYear.mean || ""}
                            onChange={handleChange}
                            required
                        />
                        <input
                            type="number"
                            name="startYear.stDev"
                            id="startYear.STDEV"
                            placeholder="standard deviation"
                            value={formData.startYear.stDev || ""}
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
                {formData.duration.distributionType === "FIXED" && (
                    <input
                        type="number"
                        name="duration.value"
                        id="duration.FIXED"
                        placeholder="value"
                        value={formData.duration.value || ""}
                        onChange={handleChange}
                        required
                    />
                )}
                {formData.duration.distributionType === "UNIFORM" && (
                    <div>
                        <input
                            type="number"
                            name="duration.lower"
                            id="duration.LOWER"
                            placeholder="Lower"
                            value={formData.duration.lower || ""}
                            onChange={handleChange}
                            required
                        />
                        <input
                            type="number"
                            name="duration.upper"
                            id="duration.UPPER"
                            placeholder="Upper"
                            value={formData.duration.upper || ""}
                            onChange={handleChange}
                            required
                        />
                    </div>
                )}
                {formData.duration.distributionType === "NORMAL" && (
                    <div>
                        <input
                            type="number"
                            name="duration.mean"
                            id="duration.MEAN"
                            placeholder="mean"
                            value={formData.duration.mean || ""}
                            onChange={handleChange}
                            required
                        />
                        <input
                            type="number"
                            name="duration.stDev"
                            id="duration.STDEV"
                            placeholder="standard deviation"
                            value={formData.duration.stDev || ""}
                            onChange={handleChange}
                            required
                        />
                    </div>
                )}
            </div>
        );
    }

    function handleButtonClick(item) {
        setSelectedInvestment(item);
        const savedRecord = existingInvestments.find(
            inv => inv.investmentTypeId === item.id
        );
        setFormData(prev => ({
            ...prev,
            investmentTypeId: savedRecord ? savedRecord.investmentTypeId : item.id
        }));
    }

    function setupAssetAllocation(){
        return (
            <div>
                {existingInvestments.map((item, index) => {
                    const matchedType = investmentTypes.find(type => type.id === item.investmentTypeId);

                    const allocationKey = matchedType ? `${matchedType.name} ${item.taxStatus}` : item.investmentTypeId;
                    return (
                        <form key={item.investmentTypeId || index} className="investment-form">
                            <div className="login">
                                <label htmlFor={`name-${index}`}></label>
                                <button
                                    type="button"
                                    id={`name-${index}`}
                                    name="name"
                                    onClick={() => handleButtonClick(item)}>
                                    {matchedType ? <span>{matchedType.name}</span> : null}
                                    {" "}
                                    {item.taxStatus}
                                    <input
                                        type="number"
                                        step="any"
                                        value={allocationValues[allocationKey] !== undefined ? allocationValues[allocationKey] : ""}
                                        onChange={(e) => {
                                            const val = parseFloat(e.target.value);
                                            setAllocationValues(prev => ({
                                                ...prev,
                                                [allocationKey]: isNaN(val) ? 0 : val
                                            }));
                                        }}
                                    />
                                </button>
                            </div>
                        </form>
                    );
                })}
            </div>
        );
    }


    function handleSaveList() {
        const savedList = existingInvestments.reduce((acc, item) => {
            const matchedType = investmentTypes.find(
                type => type.id === item.investmentTypeId
            );
            const allocationKey = matchedType ? `${matchedType.name} ${item.taxStatus}` : item.investmentTypeId;
            if (allocationValues.hasOwnProperty(allocationKey)) {
                acc.push({
                    investmentKey: allocationKey,
                    ratio: allocationValues[allocationKey]
                });
            }
            return acc;
        }, []);

        setFormData(prev => ({
            ...prev,
            assetAllocations: savedList
        }));
        console.log("Saved List:", savedList);
    }


    async function handleSubmit(event) {
        const scenarioId = localStorage.getItem("scenario");
        const check = localStorage.getItem("InvestEvent");
        const allocationSum = Object.values(allocationValues).reduce((acc, cur) => acc + cur, 0);
        if (allocationSum !== 1) {console.log("This is Allocation Sum: ", allocationSum); alert("Sum of the values that you put must be 1."); return;
        }
        const updatedData = {
            ...formData,
            scenarioId,
            assetAllocations: formData.assetAllocations
        };
        console.log("This is Updated Data: ", updatedData);
        try {
            if (check) {
                const response = await axios.put(
                    `http://localhost:10000/api/invest-events/1`,
                    updatedData,
                    { withCredentials: true, headers: { "Content-Type": "application/json" } }
                );
                console.log("Updated Investment!:", response.data);
            } else {
                const response = await axios.post(
                    `http://localhost:10000/api/invest-events`,
                    updatedData,
                    { withCredentials: true, headers: { "Content-Type": "application/json" } });
                localStorage.setItem("InvestEvent", response.data.scenarioId);
                console.log("Created Investment!:", response.data);
            }
            setSelectedInvestment(null);
            // setFormData({
            //     scenarioId: "",
            //     // eventSeriesId: "",
            //     // investmentId: '',
            //     name: "",
            //     startYear: {
            //         amountOrPercent: "AMOUNT",
            //         distributionType: "FIXED",
            //         value: null,
            //         lower: null,
            //         upper: null,
            //         mean: null,
            //         stDev: null
            //     },
            //     duration: {
            //         amountOrPercent: "AMOUNT",
            //         distributionType: "FIXED",
            //         value: null,
            //         lower: null,
            //         upper: null,
            //         mean: null,
            //         stDev: null
            //     },
            //     eventType: "",
            //     assetAllocations: "",
            //     maxCash: ""
            // });
            // setAllocationValues({});
        } catch (error) {
            console.error("Submit Error:", error);
            alert("Try again");
        }
    }

    function investmentSetting() {
        return (
            <div>
                <form>
                    <div className="login">
                        <label htmlFor="name">name:</label>
                        <input type="text" id="name" name="name" value={formData.name} onChange={(e) => setFormData(prev => ({ ...prev, name: e.target.value }))} required
                        />
                    </div>
                    <div className="login"><label htmlFor="startYear.amountOrPercent">Start Year: </label>
                        <select
                            name="startYear.amountOrPercent"
                            id="startYear.amountOrPercent"
                            value={formData.startYear.amountOrPercent}
                            onChange={handleChange} required>
                            <option value = "AMOUNT">Amount</option>
                            <option value = "PERCENT">Percent</option>
                        </select>
                    </div>
                    <div className="login"><label htmlFor="startYear.distributionType">Distribution Type </label>
                        <select name="startYear.distributionType" id="startYear.distributionType" value={formData.startYear.distributionType} onChange={handleChange} required>
                            <option value = "FIXED">FIXED</option>
                            <option value = "UNIFORM">UNIFORM</option>
                            <option value = "NORMAL">NORMAL</option>
                        </select>
                    </div>
                    {chooseMone()}
                    <div className="login"><label htmlFor="duration.amountOrPercent">Duration: </label>
                        <select
                            name="duration.amountOrPercent" id="duration.amountOrPercent" value={formData.duration.amountOrPercent} onChange={handleChange} required>
                            <option value = "AMOUNT">Amount</option>
                            <option value = "PERCENT">Percent</option>
                        </select></div>
                    <div className="login"><label htmlFor="duration.distributionType">Distribution Type </label>
                        <select name="duration.distributionType" id="duration.distributionType" value={formData.duration.distributionType} onChange={handleChange} required>
                            <option value = "FIXED">FIXED</option>
                            <option value = "UNIFORM">UNIFORM</option>
                            <option value = "NORMAL">NORMAL</option>
                        </select></div>
                    {chooseKone()}
                    <div className="login">
                        <label htmlFor="eventType">Event Type: </label>
                        <input
                            type="text" id="eventType" name="eventType" value={formData.eventType}
                            onChange={(e) => setFormData(prev => ({ ...prev, eventType: e.target.value }))} required/>
                    </div>
                    {setupAssetAllocation()}
                    <button type = "button" onClick={handleSaveList}>Save to List</button>
                    <div className="login">
                        <label htmlFor="maxCash">Max Cash: </label>
                        <input type="number" id="maxCash" name="maxCash" value={formData.maxCash} onChange={(e) => setFormData(prev => ({ ...prev, maxCash: e.target.value }))} required/>
                    </div>

                </form>
                    <button type="Submit" onClick={handleSubmit}>Submit</button>
            </div>
        );
    }

    function investmentPage() {
        return (
            <div className="profileSetting">
                <p className="logoLetter" style={{ color: 'black', fontSize: '5vh', marginTop: "30px" }}>
                    Invest Events
                </p>
                {investmentSetting()}
                <div>
                    {/*<button onClick={toInvestEvent}>Save</button>*/}
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
                {loggedIn && (<button className="commonButton" onClick={toProfile}>Scenario Setting</button>
                )}
            </nav>
            {investmentPage()}
        </div>
    );
}

export default InvestEventPage;
