import React, { useState } from 'react';
import './common.css';
import {useEffect} from "react";
import axios from "axios";
import profileImage from '/public/back.jpg';
import { useNavigate } from 'react-router-dom';
import Axios from "axios"

function homePage(){
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
    navPage('/Imex')
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

  function homeManage(){
    return(<div className="loginBox">
      <></>
    </div>)
  }


  function handleLogout() {
    localStorage.removeItem("token");
    setLoggedIn(false);
    navPage("/Homepage");
  }

  return (<div className="total">
        <nav className="navBarTop">
          <img src="/public/caffeineOverloadLogo.png" className="logoSize" alt="logo" />
          <p className="logoLetter">Life Time Financial Planner</p>
          { !loggedIn ? (
              <button className="commonButton" onClick={toLogin}>Sign-In</button>) : (<button className="commonButton" onClick={handleLogout}>Log Out</button>)
          }
        </nav>
        <nav className="navBarSub">
          <button className="commonButton" onClick={popupMenu}>Menu</button>
          {sideElements()}
          <button className="commonButton" onClick={toProfile}>
           profile Setting
          </button>
        </nav>
        {homeManage()}
      </div>);
}
export default homePage;
