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
        navPage('/SimulationPage')
    }

    function toInvestment(){
        navPage('/Investment')
    }

    function toProfile(){
        navPage('/Profset');
    }

    function toLogin(){
        navPage('/Loginpage');
    }

    function homeManage(){
        return(<div className="guideBox">
            <p className="logoLetter" style={{fontSize: 'xx-large', color: 'black', fontWeight: 'bold'}}>Life Financial Planner User Guide</p>
            {/*{loggedIn === true && (<button className="submitButton" onClick={gettingStart}>Getting Start</button>)}*/}
            {/*{loggedIn === false && (<button className="submitButton" onClick={toLogin} >Sign in</button>)}*/}
            {/*{loggedIn === false && (<button className="submitButton">Try as a guest</button>)}*/}
            <p style={{paddingLeft: "100px", paddingRight: "100px"}}>1. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed non risus. Suspendisse lectus tortor, dignissim sit amet, adipiscing nec, ultricies sed, dolor. Cras elementum ultrices diam. Maecenas ligula massa, varius a, semper congue, euismod non, mi. Proin porttitor, orci nec nonummy molestie, enim est eleifend mi, non fermentum diam nisl sit amet erat.Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed non risus. Suspendisse lectus tortor, dignissim sit amet, adipiscing nec, ultricies sed, dolor. Cras elementum ultrices diam. Maecenas ligula massa, varius a, semper congue, euismod non, mi. Proin porttitor, orci nec nonummy molestie, enim est eleifend mi, non fermentum diam nisl sit amet erat.Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed non risus. Suspendisse lectus tortor, dignissim sit amet, adipiscing nec, ultricies sed, dolor. Cras elementum ultrices diam. Maecenas ligula massa, varius a, semper congue, euismod non, mi. Proin porttitor, orci nec nonummy molestie, enim est eleifend mi, non fermentum diam nisl sit amet erat.</p>
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
