import React, {useEffect, useState} from 'react';
import './common.css';
import { useNavigate } from 'react-router-dom';
import Axios from "axios"
import axios from "axios";

function simulationPage(){
    useEffect(() => {
        const token = localStorage.getItem("token");
        if (token) {
            setLoggedIn(true);
        }
    }, []);

    const [openSide, setSide] = useState(false);
    const navPage = useNavigate();
    const [loggedIn, setLoggedIn] = useState(false);
    const [formData, setFormData] = useState({
        scenarioId: '', //private Long scenarioId;
        simulationCount: ''//private Integer simulationCount;
    });

    const popupMenu = () => {
        setSide(prevState => !prevState);
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

    function toSimulationResult(){
        navPage("/SimulationResult")
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
    function toResult(){
        navPage("/SimulationResult")
    }

    async function handleSubmit(){
        formData.scenarioId = localStorage.getItem("scenario")
        console.log(formData)
        try {
            const response = await axios.post("http://localhost:10000/api/simulations", formData, { withCredentials: true, headers: { "Content-Type": "application/json" } });
            console.log("Data:", response.data);
            toResult()
        } catch (error) {
            console.error("log in Error:", error);
            alert("Fail to Post Data");
        }
    }

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    function simulationSetting(){
        return(<div className="loginBox">
            <p><strong>Enter Simulation Times</strong></p>
            <input type="number"
                name="simulationCount"
                id="simulationCount"
                placeholder="Please specify the number of simulation runs."
                onChange={handleChange}
                value={formData.simulationCount}
                style={{ width:"280px" }}
                required/>
            <button onClick = {handleSubmit}>Submit</button>
        </div>)
    }

    return (<div className="total">
        <nav className="navBarTop">
            <img src ="/public/caffeineOverloadLogo.png" className = "logoSize" onClick={toHome}></img>
            <p className= "logoLetter">Life Time Financial Planner</p>
            <button onClick={toUserGuide}>User Guide</button>
        </nav>
        <nav className= "navBarSub">
            <button className="commonButton" onClick={popupMenu}>Menu</button>
            {sideElements()}
            {loggedIn === true && (<button className="commonButton" onClick={toProfile}>
                Scenario Setting
            </button>)}
        </nav>
        {simulationSetting()}
    </div>);
}
export default simulationPage;
