import React, { useState } from 'react';
import './common.css';
import {useEffect} from "react";
import axios from "axios";
import profileImage from '/public/back.jpg';
import { useNavigate } from 'react-router-dom';

function homePage(){
    useEffect(() => {
        const fetchData = async () => {
            try {
                const planResp = await axios.get("http://localhost:10000/test");
                console.log(planResp.data);
            } catch (err) {
                console.log("inital error");
                console.log(err);
            }
        };

        fetchData();}, []);

    const [openSide, setSide] = useState(false);
    const [pro, setPro] = useState([{name: '', profile: {profileImage}}]);
    const navPage = useNavigate();

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

    function toHome(){
        navPage('/Homepage')
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
        navPage('/Imex')
    }

    function toHome(){
        navPage('/Homepage')
    }

    function defineProfile(){
        if(pro[0].profile === null || pro[0].profile === undefined){
            return profileImage;
        }else{
            return pro[0].profile;
        }
    }

    const handleImage = (e) => {
        e.target.onError = null;
        e.target.src = profileImage;
    }

    function toProfile(){
        navPage('/Profset');
    }

    function toLogin(){
        navPage('/Loginpage');
    }

    return (<div>
        <nav className="navBarTop">
            <img onClick={toHome} src ="/public/caffeineOverloadLogo.png" className = "logoSize"></img>
            <p className= "logoLetter">Life Time Financial Planner</p>
            <button className="commonButton" onClick={toLogin}>Sign-In</button>
        </nav>
        <nav className= "navBarSub">
            <button className="commonButton" onClick={popupMenu}>Menu</button>
            {sideElements()}
            <button className="noShape" onClick={toProfile}>
                <img  className="profile" src={defineProfile()} onError={handleImage} alt="profile"></img>
            </button>
        </nav>
    </div>);
}
export default homePage;
