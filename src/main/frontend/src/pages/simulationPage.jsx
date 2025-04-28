import React, {useEffect, useState} from 'react';
import './common.css';
import { useNavigate } from 'react-router-dom';
import Axios from "axios"

function simulationPage(){
    useEffect(() => {
        const token = localStorage.getItem("token");
        if (token) {
            setLoggedIn(true);
        }
    }, []);

    const [openSide, setSide] = useState(false);
    const navPage = useNavigate();
    const [loggedIn, setLoggedIn] = useState(false)

    const popupMenu = () => {
        setSide(prevState => !prevState);
    };

    function sideElements(){
        return openSide && (
            <aside className="sidebar">
                <button onClick={toIncome}>Income Edit</button>
                <button onClick={toExpense}>Expense Edit</button>
                <button onClick={toInvest}>Invest Edit</button>
                <button onClick={toWithDrawal}>Expense Withdrawal Edit</button>
                <button onClick={toInvestEvent}>Invest Event Edit</button>
                {/*<button onClick={toSim}>Scenario Simulation</button>*/}
                <button disabled>Import & Export Data</button>
            </aside>
        )
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

    function simulationSetting(){
        return(<div className="loginBox">
            <p><strong>Enter Simulation Times</strong></p>
            <input
                type="number"
                name="simulationTime"
                id="simulationTime"
                placeholder="Please specify the number of simulation runs."
                style = {{width:"280px"}}
                required
            />
            <button onClick = {toSimulationResult}>Submit</button>
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
