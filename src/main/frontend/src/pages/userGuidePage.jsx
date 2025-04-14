import React, { useState } from 'react';
import './common.css';
import {useEffect} from "react";
import axios from "axios";
import profileImage from '/public/back.jpg';
import { useNavigate } from 'react-router-dom';
import Axios from "axios"

function userGuidePage(){
    useEffect(() => {
        const token = localStorage.getItem("token");
        if (token) {
            setLoggedIn(true);
        }
    }, []);

    const [openSide, setSide] = useState(false);
    const [pro, setPro] = useState([{name: '', profile: {profileImage}}]);
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
                <button onClick={toSim}>Scenario Simulation</button>
                <button>Reports & Logs</button>
                <button>Import & Export Date</button>
            </aside>
        )
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

    function homeManage(){
        return(<div className="guideBox">
            <p className="logoLetter" style={{fontSize: 'xx-large', color: 'black', fontWeight: 'bold'}}>Life Financial Planner User Guide</p>
            <button className="guider">Investment</button>
            <button>Income Edit</button>
            <button>Expense Edit</button>
            <button>Invest Edit</button>
            <button>Expense Withdrawal Edit</button>
            <button>Invest Event Edit</button>
            <button disabled>Scenario Simulation</button>
            <button disabled>Import & Export Data</button>
            <p style={{paddingLeft: "100px", paddingRight: "100px"}}></p>
        </div>)
    }

    function gettingStart(){
        const scenarioId = localStorage.getItem("scenario");
        console.log(scenarioId)
        if (scenarioId) {
            toInvestment();
        }else{
            toProfile();
        }
    }


    function handleLogout() {
        localStorage.removeItem("token");
        localStorage.removeItem("scenario")
        setLoggedIn(false);
        navPage("/Homepage");
    }

    return (<div className="total">
        <nav className="navBarTop">
            <img src="/public/caffeineOverloadLogo.png" className="logoSize" alt="logo" />
            <p className="logoLetter">Life Time Financial Planner</p>
            {loggedIn === true && (<button className="commonButton" onClick={handleLogout}>Log Out</button>)}
            {loggedIn === false && (<div></div>)}
        </nav>
        <nav className="navBarSub">
            {/*{loggedIn === true && <button className="commonButton" onClick={popupMenu}>Menu</button>}*/}
            {/*{sideElements()}*/}
            {/*{loggedIn === true && (<button className="commonButton" onClick={toProfile}>*/}
            {/* Scenario Setting*/}
            {/*</button>)}*/}
        </nav>
        {homeManage()}
    </div>);
}
export default userGuidePage;
